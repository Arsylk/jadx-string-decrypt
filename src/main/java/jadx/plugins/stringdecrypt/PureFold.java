package jadx.plugins.stringdecrypt;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import jadx.core.dex.info.FieldInfo;
import jadx.core.dex.info.MethodInfo;
import jadx.core.dex.instructions.ArithOp;
import jadx.core.dex.instructions.IfOp;
import jadx.core.dex.instructions.args.ArgType;
import jadx.plugins.stringdecrypt.eval.TypeMap;
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
	 * Mutable slot for a {@code new-instance} object (any constructed JDK value — {@code String},
	 * {@code BigInteger}, {@code StringBuilder}, ...). Compiled code aliases the uninitialized
	 * reference across registers (modelled as MOVE); the {@code <init>} call writes through this
	 * shared holder so every alias observes the built object.
	 */
	private static final class Holder {
		Object value;
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
	Object foldCall(jadx.core.dex.instructions.InvokeNode invoke, Object[] args, int depth) {
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
					if (r == TypeMap.UNRESOLVED) {
						return null; // never return an unresolved value as if it were a constant
					}
					if (r instanceof Holder) {
						return ((Holder) r).value;
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
					// The snapshot carries the literal text as the op payload (obfuscators build helper
					// strings like "toCharArray" and feed them to reflective getMethod/forName calls).
					if (!(op.payload instanceof String)) {
						return null;
					}
					put(reg, op, op.payload);
					break;
				}
				case CONST_CLASS: {
					// SomeClass.class — root of a reflective bridge inside the helper (getMethod/...).
					Object cls = resolveConstClass(op.argType);
					if (cls == null) {
						return null;
					}
					put(reg, op, cls);
					break;
				}
				case MOVE:
					put(reg, op, value(reg, op.args[0]));
					break;
				case CAST:
				case CHECK_CAST: {
					Object src = value(reg, op.args[0]);
					if (src instanceof Long) {
						put(reg, op, op.argType != null ? Eval.extend(op.argType, (Long) src) : src);
					} else {
						put(reg, op, src); // reference cast: carry the object through unchanged
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
						// Not in the reconstructed app constants → try a JDK static-final lookup
						// (e.g. StandardCharsets.UTF_8, Long.TYPE, ...) gated to the whitelist.
						v = boxIncoming(jdk.resolveStaticFinal(f.getDeclClass().getFullName(), f.getName()));
						if (v == null) {
							return null;
						}
					}
					put(reg, op, v);
					break;
				}
				case AGET: {
					Object arr = value(reg, op.args[0]);
					Long idx = asLong(value(reg, op.args[1]));
					if (idx == null) {
						return null;
					}
					int i = idx.intValue();
					if (arr instanceof ArrVal) {
						ArrVal av = (ArrVal) arr;
						if (i < 0 || i >= av.data.length) {
							return null;
						}
						put(reg, op, av.data[i]);
						break;
					}
					if (arr instanceof Object[]) {
						Object[] oa = (Object[]) arr;
						if (i < 0 || i >= oa.length) {
							return null;
						}
						put(reg, op, boxIncoming(oa[i]));
						break;
					}
					return null;
				}
				case NEW_ARRAY: {
					ArgType elem = op.argType;
					Long size = asLong(value(reg, op.args[0]));
					if (elem == null || size == null || size < 0 || size > keys.maxArraySize()) {
						return null;
					}
					if (Eval.isIntegral(elem)) {
						put(reg, op, new ArrVal(new long[size.intValue()], elem));
					} else {
						// object array (e.g. new Object[]{...} / new Class[]{...} feeding a reflective call):
						// hold real Java values so APUT/AGET and the JDK boundary see ordinary objects.
						put(reg, op, new Object[size.intValue()]);
					}
					break;
				}
				case FILL_ARRAY: {
					Object arr = value(reg, op.args[0]);
					if (!(arr instanceof ArrVal) || !(op.payload instanceof long[])) {
						return null;
					}
					ArrVal av = (ArrVal) arr;
					long[] data = (long[]) op.payload;
					for (int i = 0; i < data.length && i < av.data.length; i++) {
						av.data[i] = Eval.extend(av.elem, data[i]);
					}
					break;
				}
				case APUT: {
					Object arr = value(reg, op.args[0]);
					Long idx = asLong(value(reg, op.args[1]));
					if (idx == null) {
						return null;
					}
					int i = idx.intValue();
					if (arr instanceof ArrVal) {
						ArrVal av = (ArrVal) arr;
						Long val = asLong(value(reg, op.args[2]));
						if (val == null || i < 0 || i >= av.data.length) {
							return null;
						}
						av.data[i] = Eval.extend(av.elem, val);
						break;
					}
					if (arr instanceof Object[]) {
						Object[] oa = (Object[]) arr;
						if (i < 0 || i >= oa.length) {
							return null;
						}
						oa[i] = unboxOutgoing(value(reg, op.args[2]));
						break;
					}
					return null;
				}
				case NEW_INSTANCE:
					put(reg, op, new Holder()); // a mutable slot; filled at its <init> invoke
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
		// <init>: construct the object and write it into the new-instance holder (every aliased
		// register then observes the built value). new String(char[]) keeps a direct fast path for
		// char fidelity; everything else (BigInteger(byte[]), StringBuilder(String), ...) is built
		// through the JDK handler's constructor.
		if ("<init>".equals(callMth.getName())) {
			if (op.args.length < 1) {
				return NULL;
			}
			Object instance = value(reg, op.args[0]);
			if (!(instance instanceof Holder)) {
				return NULL;
			}
			Holder holder = (Holder) instance;
			Object arr = op.args.length >= 2 ? value(reg, op.args[1]) : null;
			if ("java.lang.String".equals(declClass) && arr instanceof ArrVal && ArgType.CHAR.equals(((ArrVal) arr).elem)) {
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
				holder.value = new String(chars);
				return null; // result is the instance, not a move-result
			}
			if (jdk.handles(declClass)) {
				Object[] ctorArgs = new Object[op.args.length - 1];
				for (int i = 0; i < ctorArgs.length; i++) {
					ctorArgs[i] = unboxOutgoing(value(reg, op.args[1 + i]));
				}
				Object built = jdk.invoke(callMth, null, ctorArgs); // ctor: handler creates the instance
				if (built == null) {
					return NULL;
				}
				holder.value = built;
				return null;
			}
			return NULL;
		}
		// setAccessible(...) on a reflective handle is a pure no-op for constant interpretation (we
		// already set it ourselves when invoking). Return a real null (success, no move-result) rather
		// than the NULL refuse-sentinel so the obfuscator's mandatory setAccessible call doesn't abort.
		if ("setAccessible".equals(callMth.getName())) {
			return null;
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

	/**
	 * Resolve a {@code CONST_CLASS} type to its live {@link Class}, gated to JDK-modelled types (or
	 * primitives / arrays of them) — mirrors {@code ObjectEvaluator.resolveConstClass} so a helper
	 * body never hands back a live {@code Class} for app code we must not reflect into.
	 */
	@Nullable
	private Object resolveConstClass(@Nullable ArgType clsType) {
		Class<?> c = TypeMap.toClass(clsType);
		if (c == null) {
			return null;
		}
		Class<?> base = c;
		while (base.isArray()) {
			base = base.getComponentType();
		}
		if (base.isPrimitive() || jdk.handles(base.getName())) {
			return c;
		}
		return null;
	}

	/** Box a real Java value into the interpreter's internal representation for a register. */
	private static Object boxIncoming(Object v) {
		if (v == null) {
			return null;
		}
		if (v instanceof Long) {
			return v;
		}
		if (v instanceof Integer || v instanceof Short || v instanceof Byte) {
			return ((Number) v).longValue();
		}
		if (v instanceof Character) {
			return (long) (Character) v;
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
		if (v instanceof Holder) {
			return ((Holder) v).value;
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
