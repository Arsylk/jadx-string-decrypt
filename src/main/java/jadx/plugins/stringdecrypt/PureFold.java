package jadx.plugins.stringdecrypt;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import jadx.core.dex.info.FieldInfo;
import jadx.core.dex.info.MethodInfo;
import jadx.core.dex.instructions.ArithOp;
import jadx.core.dex.instructions.InvokeNode;
import jadx.core.dex.instructions.args.ArgType;
import jadx.core.dex.nodes.RootNode;

/**
 * Prototype interprocedural constant folder: deterministically interprets a <b>pure,
 * straight-line</b> static method (snapshotted as a {@link MethodBody} in the prepare pass) with
 * constant arguments and returns its constant result ({@link Long} or {@link String}), or
 * {@code null} if it cannot be fully interpreted.
 *
 * <p>
 * "Fully interpretable" is the safety gate (there is no decrypt checksum here): the interpreter
 * supports only side-effect-free integer arithmetic, key-table/field reads, integral array building
 * and {@code new String(char[][,int,int])}, plus recursion into other pure static methods. Any
 * control flow (loop/branch), any unsupported instruction, and any unknown/impure call (e.g.
 * {@code java.util.Random}, I/O, {@code StringBuilder}) is never snapshotted, so a
 * non-deterministic
 * helper like a random name generator is never folded.
 *
 * <p>
 * Reading from the immutable snapshot (rather than the live {@code MethodNode}) is what makes this
 * safe during the threaded decompile pass: the callee may already be decompiled and its raw
 * instructions unloaded, but its snapshot stays available and stable.
 */
final class PureFold {

	private static final int MAX_DEPTH = 16;
	private static final int MAX_INSNS = 100_000;
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

	PureFold(KeyData keys) {
		this.keys = keys;
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

	@Nullable
	Object foldCall(RootNode root, InvokeNode invoke, Object[] args, int depth) {
		return foldCall(invoke.getCallMth(), args, depth);
	}

	@Nullable
	private Object foldCall(MethodInfo callMth, Object[] args, int depth) {
		MethodBody body = keys.bodies().get(callMth.getRawFullId());
		return body != null ? fold(body, args, depth) : null;
	}

	@Nullable
	Object fold(MethodBody body, Object[] args, int depth) {
		if (depth > MAX_DEPTH || body.argRegs.length != args.length) {
			return null;
		}
		Map<Integer, Object> reg = new HashMap<>();
		for (int k = 0; k < args.length; k++) {
			reg.put(body.argRegs[k], args[k]);
		}
		Object pendingResult = null; // set by INVOKE, consumed by MOVE_RESULT
		int count = 0;
		for (MethodBody.Op op : body.ops) {
			if (++count > MAX_INSNS) {
				return null;
			}
			switch (op.type) {
				case RETURN: {
					if (op.args.length == 0) {
						return null;
					}
					Object r = value(reg, op.args[0]);
					return r instanceof StrHolder ? ((StrHolder) r).value : r; // unwrap a built string
				}
				case CONST: {
					Long v = asLong(value(reg, op.args[0]));
					if (v == null) {
						return null;
					}
					put(reg, op, v);
					break;
				}
				case MOVE:
					put(reg, op, value(reg, op.args[0]));
					break;
				case CAST: {
					Long v = asLong(value(reg, op.args[0]));
					put(reg, op, v == null ? null : (op.argType != null ? Eval.extend(op.argType, v) : v));
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
				case SGET: {
					FieldInfo f = op.field;
					String id = f.getRawFullId();
					if (!keys.isImmutable(id)) {
						return null; // a runtime-mutated static must not be folded to its <clinit> value
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
					return null; // control flow or any unsupported instruction -> refuse (stay sound)
			}
		}
		return null; // no return reached
	}

	private Object execInvoke(Map<Integer, Object> reg, MethodBody.Op op, int depth) {
		MethodInfo callMth = op.callMth;
		String declClass = callMth.getDeclClass().getFullName();
		// new String(char[]) / new String(char[], int, int): build the string in the instance register
		if ("<init>".equals(callMth.getName()) && "java.lang.String".equals(declClass)) {
			if (op.args.length < 2) {
				return NULL;
			}
			Object instance = value(reg, op.args[0]); // the (aliased) new-instance holder
			Object arr = value(reg, op.args[1]);
			if (!(instance instanceof StrHolder) || !(arr instanceof ArrVal)) {
				return NULL;
			}
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
			((StrHolder) instance).value = new String(chars); // visible through every aliased register
			return null; // result is the instance, not a move-result
		}
		// recurse into another pure static app method (only if it was snapshotted)
		MethodBody callee = keys.bodies().get(callMth.getRawFullId());
		if (callee != null) {
			Object[] args = new Object[op.args.length];
			for (int i = 0; i < args.length; i++) {
				args[i] = value(reg, op.args[i]);
			}
			Object r = fold(callee, args, depth + 1);
			return r != null ? r : NULL;
		}
		return NULL; // unknown / impure call -> refuse
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
