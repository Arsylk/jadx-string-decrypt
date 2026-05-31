package jadx.plugins.stringdecrypt;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import jadx.core.dex.info.FieldInfo;
import jadx.core.dex.info.MethodInfo;
import jadx.core.dex.instructions.ArithOp;
import jadx.core.dex.instructions.IfOp;
import jadx.core.dex.instructions.args.ArgType;
import jadx.core.dex.nodes.RootNode;
import jadx.plugins.stringdecrypt.jdk.JdkInterpreter;

/**
 * Interprocedural constant folder: deterministically interprets a <b>pure</b> static method
 * (snapshotted as a {@link MethodBody} in the prepare pass) with constant arguments and returns its
 * constant result ({@link Long}, {@link String}, {@code byte[]}, {@code char[]}, ...), or
 * {@code null} if it cannot be fully interpreted.
 *
 * <p>
 * "Pure" is the safety gate (there is no decrypt checksum here): the interpreter supports only
 * side-effect-free integer arithmetic, key-table/field reads, integral array building, branching,
 * goto, calls into {@link JdkInterpreter}'s pure JDK whitelist, and recursion into other
 * snapshotted pure static methods. Any unsupported instruction and any unknown/impure call refuses
 * the snapshot up-front, so a non-deterministic helper like a random name generator is never
 * folded.
 *
 * <p>
 * Control flow: snapshots include {@code IF}/{@code GOTO}/{@code ARRAY_LENGTH}, and this
 * interpreter drives a program counter with hard caps on iteration count so a malformed loop in a
 * snapshotted helper can never wedge the decompiler.
 *
 * <p>
 * Reading from the immutable snapshot (rather than the live {@code MethodNode}) is what makes this
 * safe during the threaded decompile pass: the callee may already be decompiled and its raw
 * instructions unloaded, but its snapshot stays available and stable.
 */
final class PureFold {

	private static final int MAX_DEPTH = 16;
	private static final int MAX_INSNS = 200_000;
	/** Sentinel distinguishing "refuse" (NULL) from a legitimately void/null invoke result. */
	private static final Object NULL = new Object();

	/**
	 * Mutable slot for a {@code new-instance} object. JVM-compiled code {@code dup}s the uninitialized
	 * reference (modelled as MOVE), so several registers alias the same not-yet-built object; the
	 * {@code <init>} call writes through this shared holder so every alias observes the built string.
	 */
	private static final class StrHolder {
		String value;
	}

	private final KeyData keys;
	private final JdkInterpreter jdk;

	PureFold(KeyData keys) {
		this(keys, new JdkInterpreter());
	}

	PureFold(KeyData keys, JdkInterpreter jdk) {
		this.keys = keys;
		this.jdk = jdk;
	}

	/** Integral-valued constant array, modelled with its element type for correct load extension. */
	private static final class ArrVal {
		final long[] data;
		final ArgType elem;

		ArrVal(long[] data, ArgType elem) {
			this.data = data;
			this.elem = elem;
		}
	}

	/**
	 * Look up the snapshotted body for {@code callMth} and interpret it with {@code args}. Accessible
	 * to {@link ObjectEvaluator} so the JDK fold path can recurse into pure app helpers uniformly.
	 */
	@Nullable
	Object foldCall(MethodInfo callMth, Object[] args, int depth) {
		MethodBody body = keys.bodies().get(callMth.getRawFullId());
		return body != null ? fold(body, args, depth) : null;
	}

	@Nullable
	Object foldCall(RootNode root, jadx.core.dex.instructions.InvokeNode invoke, Object[] args, int depth) {
		return foldCall(invoke.getCallMth(), args, depth);
	}

	@Nullable
	Object fold(MethodBody body, Object[] args, int depth) {
		if (depth > MAX_DEPTH || body.argRegs.length != args.length) {
			return null;
		}
		Map<Integer, Object> reg = new HashMap<>();
		for (int k = 0; k < args.length; k++) {
			reg.put(body.argRegs[k], boxIncoming(args[k]));
		}
		Object pendingResult = null; // set by INVOKE, consumed by MOVE_RESULT
		int pc = 0;
		int count = 0;
		while (pc >= 0 && pc < body.ops.length) {
			if (++count > MAX_INSNS) {
				return null;
			}
			MethodBody.Op op = body.ops[pc];
			int nextPc = pc + 1; // default fall-through; overridden by branches
			switch (op.type) {
				case RETURN: {
					if (op.args.length == 0) {
						return null;
					}
					Object r = value(reg, op.args[0]);
					if (r instanceof StrHolder) {
						return ((StrHolder) r).value;
					}
					if (r instanceof ArrVal) {
						return unboxArr((ArrVal) r);
					}
					return r;
				}
				case CONST: {
					Long v = asLong(value(reg, op.args[0]));
					if (v == null) {
						return null;
					}
					put(reg, op, v);
					break;
				}
				case CONST_STR: {
					// CONST_STR loads a string literal into a register; snapshotted as a single literal arg.
					if (op.args.length < 1 || !op.args[0].literal) {
						return null;
					}
					// Snapshot represents the literal text via a side channel — we keep CONST_STR opaque for
					// now (none of the obfuscation in scope uses it inside a helper body). Refuse if seen.
					return null;
				}
				case MOVE:
					put(reg, op, value(reg, op.args[0]));
					break;
				case CAST: {
					Object src = value(reg, op.args[0]);
					if (src instanceof Long) {
						put(reg, op, op.argType != null ? Eval.extend(op.argType, (Long) src) : src);
					} else {
						// reference cast (CHECK_CAST is a separate type, but some snapshots see CAST on refs)
						put(reg, op, src);
					}
					break;
				}
				case NEG: {
					Long v = asLong(value(reg, op.args[0]));
					put(reg, op, v == null ? null : -v);
					break;
				}
				case NOT: {
					Long v = asLong(value(reg, op.args[0]));
					put(reg, op, v == null ? null : ~v);
					break;
				}
				case ARITH: {
					Long a = asLong(value(reg, op.args[0]));
					Long b = asLong(value(reg, op.args[1]));
					if (a == null || b == null) {
						return null;
					}
					Long r = arith(op.arithOp, a, b);
					if (r == null) {
						return null;
					}
					put(reg, op, r);
					break;
				}
				case ARRAY_LENGTH: {
					Object arr = value(reg, op.args[0]);
					if (arr instanceof ArrVal) {
						put(reg, op, (long) ((ArrVal) arr).data.length);
						break;
					}
					if (arr != null && arr.getClass().isArray()) {
						put(reg, op, (long) java.lang.reflect.Array.getLength(arr));
						break;
					}
					return null;
				}
				case IF: {
					Long a = asLong(value(reg, op.args[0]));
					Long b = asLong(value(reg, op.args[1]));
					if (a == null || b == null) {
						return null;
					}
					nextPc = compare(op.ifOp, a, b) ? op.branchTarget : pc + 1;
					break;
				}
				case GOTO:
					nextPc = op.branchTarget;
					break;
				case SGET: {
					FieldInfo f = op.field;
					String id = f.getRawFullId();
					if (!keys.isImmutable(id)) {
						return null;
					}
					long[] table = keys.arrays().get(id);
					Object v = table != null ? new ArrVal(table, f.getType().getArrayElement()) : keys.scalars().get(id);
					if (v == null) {
						return null;
					}
					put(reg, op, v);
					break;
				}
				case AGET: {
					Object arr = value(reg, op.args[0]);
					Long idx = asLong(value(reg, op.args[1]));
					if (!(arr instanceof ArrVal) || idx == null) {
						return null;
					}
					ArrVal av = (ArrVal) arr;
					int i = idx.intValue();
					if (i < 0 || i >= av.data.length) {
						return null;
					}
					put(reg, op, av.data[i]);
					break;
				}
				case NEW_ARRAY: {
					ArgType elem = op.argType;
					Long size = asLong(value(reg, op.args[0]));
					if (elem == null || !Eval.isIntegral(elem) || size == null || size < 0 || size > keys.maxArraySize()) {
						return null;
					}
					put(reg, op, new ArrVal(new long[size.intValue()], elem));
					break;
				}
				case APUT: {
					Object arr = value(reg, op.args[0]);
					Long idx = asLong(value(reg, op.args[1]));
					Long val = asLong(value(reg, op.args[2]));
					if (!(arr instanceof ArrVal) || idx == null || val == null) {
						return null;
					}
					ArrVal av = (ArrVal) arr;
					int i = idx.intValue();
					if (i < 0 || i >= av.data.length) {
						return null;
					}
					av.data[i] = Eval.extend(av.elem, val);
					break;
				}
				case NEW_INSTANCE:
					put(reg, op, new StrHolder()); // a mutable slot; filled at its String.<init> invoke
					break;
				case INVOKE: {
					pendingResult = execInvoke(reg, op, depth);
					if (pendingResult == NULL) {
						return null;
					}
					break;
				}
				case MOVE_RESULT:
					put(reg, op, pendingResult == NULL ? null : pendingResult);
					break;
				default:
					return null; // any unsupported instruction -> refuse (stay sound)
			}
			pc = nextPc;
		}
		return null; // no return reached (fall off the end)
	}

	private Object execInvoke(Map<Integer, Object> reg, MethodBody.Op op, int depth) {
		MethodInfo callMth = op.callMth;
		String declClass = callMth.getDeclClass().getFullName();
		// new String(char[]) / new String(char[], int, int): build the string in the instance register
		if ("<init>".equals(callMth.getName()) && "java.lang.String".equals(declClass)) {
			if (op.args.length < 2) {
				return NULL;
			}
			Object instance = value(reg, op.args[0]);
			Object arr = value(reg, op.args[1]);
			if (!(instance instanceof StrHolder)) {
				return NULL;
			}
			if (arr instanceof ArrVal) {
				ArrVal av = (ArrVal) arr;
				int off = 0;
				int len = av.data.length;
				if (op.args.length >= 4) {
					Long o = asLong(value(reg, op.args[2]));
					Long l = asLong(value(reg, op.args[3]));
					if (o == null || l == null) {
						return NULL;
					}
					off = o.intValue();
					len = l.intValue();
				}
				if (off < 0 || len < 0 || off + len > av.data.length) {
					return NULL;
				}
				char[] chars = new char[len];
				for (int i = 0; i < len; i++) {
					chars[i] = (char) av.data[off + i];
				}
				((StrHolder) instance).value = new String(chars);
				return null; // result is the instance, not a move-result
			}
			// Fall through: defer to JDK reflective <init> for byte[]/etc forms
		}
		// Recurse into another pure static app method (only if it was snapshotted)
		MethodBody callee = keys.bodies().get(callMth.getRawFullId());
		if (callee != null) {
			Object[] args = new Object[op.args.length];
			for (int i = 0; i < args.length; i++) {
				args[i] = unboxOutgoing(value(reg, op.args[i]));
			}
			Object r = fold(callee, args, depth + 1);
			return r == null ? NULL : boxIncoming(r);
		}
		// JDK whitelist fall-through: route any remaining call to the registered handlers. Coerces
		// internal ArrVal / StrHolder values to real Java instances at the boundary.
		if (jdk.handles(declClass)) {
			Object instance = null;
			int firstArg = 0;
			// Distinguish static vs instance call; static MethodInfo's first arg is the actual first
			// param. For ctors we expect the StrHolder branch above to have caught String — anything
			// else for ctor is treated as instance-relative-to-newInstance i.e. firstArg=0.
			if (callMth.getArgsCount() != op.args.length) {
				// instance call: arg[0] is the receiver
				instance = unboxOutgoing(value(reg, op.args[0]));
				firstArg = 1;
			}
			Object[] coerced = new Object[op.args.length - firstArg];
			for (int i = 0; i < coerced.length; i++) {
				coerced[i] = unboxOutgoing(value(reg, op.args[firstArg + i]));
				if (coerced[i] == null && !callMth.getArgumentsTypes().get(i).isObject() && !callMth.getArgumentsTypes().get(i).isArray()) {
					return NULL;
				}
			}
			Object r = jdk.invoke(callMth, instance, coerced);
			return r == null ? NULL : boxIncoming(r);
		}
		return NULL; // unknown / impure call -> refuse
	}

	/** Box a real Java value into the interpreter's internal representation for a register. */
	private static Object boxIncoming(Object v) {
		if (v == null) {
			return null;
		}
		if (v instanceof Long) {
			return v;
		}
		if (v instanceof Integer || v instanceof Short || v instanceof Byte || v instanceof Character) {
			return (long) ((Number) v instanceof Number ? ((Number) v).longValue()
					: (long) ((Character) v).charValue());
		}
		if (v instanceof Boolean) {
			return ((Boolean) v) ? 1L : 0L;
		}
		if (v.getClass().isArray()) {
			return arrayToArrVal(v);
		}
		return v; // String / BigInteger / StringBuilder / etc kept as-is
	}

	/** Convert the interpreter's internal rep back to a real Java value for a JDK call boundary. */
	private static Object unboxOutgoing(Object v) {
		if (v instanceof StrHolder) {
			return ((StrHolder) v).value;
		}
		if (v instanceof ArrVal) {
			return unboxArr((ArrVal) v);
		}
		return v;
	}

	private static Object unboxArr(ArrVal av) {
		long[] d = av.data;
		ArgType el = av.elem;
		if (ArgType.BYTE.equals(el)) {
			byte[] out = new byte[d.length];
			for (int i = 0; i < d.length; i++) {
				out[i] = (byte) d[i];
			}
			return out;
		}
		if (ArgType.CHAR.equals(el)) {
			char[] out = new char[d.length];
			for (int i = 0; i < d.length; i++) {
				out[i] = (char) d[i];
			}
			return out;
		}
		if (ArgType.SHORT.equals(el)) {
			short[] out = new short[d.length];
			for (int i = 0; i < d.length; i++) {
				out[i] = (short) d[i];
			}
			return out;
		}
		if (ArgType.INT.equals(el)) {
			int[] out = new int[d.length];
			for (int i = 0; i < d.length; i++) {
				out[i] = (int) d[i];
			}
			return out;
		}
		if (ArgType.LONG.equals(el)) {
			return d.clone();
		}
		if (ArgType.BOOLEAN.equals(el)) {
			boolean[] out = new boolean[d.length];
			for (int i = 0; i < d.length; i++) {
				out[i] = d[i] != 0;
			}
			return out;
		}
		return d.clone();
	}

	@Nullable
	private static ArrVal arrayToArrVal(Object arr) {
		if (arr instanceof byte[]) {
			byte[] in = (byte[]) arr;
			long[] out = new long[in.length];
			for (int i = 0; i < in.length; i++) {
				out[i] = in[i];
			}
			return new ArrVal(out, ArgType.BYTE);
		}
		if (arr instanceof char[]) {
			char[] in = (char[]) arr;
			long[] out = new long[in.length];
			for (int i = 0; i < in.length; i++) {
				out[i] = in[i];
			}
			return new ArrVal(out, ArgType.CHAR);
		}
		if (arr instanceof short[]) {
			short[] in = (short[]) arr;
			long[] out = new long[in.length];
			for (int i = 0; i < in.length; i++) {
				out[i] = in[i];
			}
			return new ArrVal(out, ArgType.SHORT);
		}
		if (arr instanceof int[]) {
			int[] in = (int[]) arr;
			long[] out = new long[in.length];
			for (int i = 0; i < in.length; i++) {
				out[i] = in[i];
			}
			return new ArrVal(out, ArgType.INT);
		}
		if (arr instanceof long[]) {
			return new ArrVal(((long[]) arr).clone(), ArgType.LONG);
		}
		if (arr instanceof boolean[]) {
			boolean[] in = (boolean[]) arr;
			long[] out = new long[in.length];
			for (int i = 0; i < in.length; i++) {
				out[i] = in[i] ? 1 : 0;
			}
			return new ArrVal(out, ArgType.BOOLEAN);
		}
		return null;
	}

	private static boolean compare(IfOp op, long a, long b) {
		switch (op) {
			case EQ:
				return a == b;
			case NE:
				return a != b;
			case LT:
				return a < b;
			case LE:
				return a <= b;
			case GT:
				return a > b;
			case GE:
				return a >= b;
			default:
				throw new IllegalStateException("Unknown IfOp: " + op);
		}
	}

	private static void put(Map<Integer, Object> reg, MethodBody.Op op, Object value) {
		if (op.resultReg >= 0) {
			reg.put(op.resultReg, value);
		}
	}

	@Nullable
	private static Object value(Map<Integer, Object> reg, MethodBody.Arg arg) {
		return arg.literal ? Long.valueOf(arg.value) : reg.get(arg.reg);
	}

	@Nullable
	private static Long asLong(Object v) {
		return v instanceof Long ? (Long) v : null;
	}

	@Nullable
	private static Long arith(ArithOp opr, long a, long b) {
		switch (opr) {
			case ADD:
				return a + b;
			case SUB:
				return a - b;
			case MUL:
				return a * b;
			case DIV:
				return b != 0 ? a / b : null;
			case REM:
				return b != 0 ? a % b : null;
			case AND:
				return a & b;
			case OR:
				return a | b;
			case XOR:
				return a ^ b;
			case SHL:
				return a << ((int) b & 63);
			case SHR:
				return a >> ((int) b & 63);
			case USHR:
				return a >>> ((int) b & 63);
			default:
				return null;
		}
	}
}
