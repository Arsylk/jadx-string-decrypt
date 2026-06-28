package jadx.plugins.stringdecrypt;

import org.jetbrains.annotations.Nullable;

import jadx.core.dex.instructions.args.ArgType;
import jadx.core.dex.instructions.args.InsnArg;
import jadx.core.dex.instructions.args.RegisterArg;
import jadx.core.dex.nodes.InsnNode;
import jadx.plugins.stringdecrypt.eval.TypeMap;

/**
 * A script-friendly, lazily-evaluated view over one invoke argument (or receiver). Conversions are
 * routed through the plugin's existing static evaluators — {@link ObjectEvaluator#evalObject} for
 * objects/arrays/{@code Class}/boxed scalars and {@link Evaluator#evalInt} for integral scalars — and
 * memoized per value, so repeated {@code arg(0).bytes()} calls do not re-walk a large array.
 *
 * <p>
 * Every accessor returns {@code null} when the value is not a resolvable compile-time constant; that is
 * the normal "not constant" signal and a pipeline should respond with {@link PipelineResult#keep()}.
 */
public final class PipelineValue {

	private final PipelineFrame frame;
	private final InsnArg rawArg;
	private final int userIndex;
	private final int rawIndex;
	private final boolean receiver;

	private boolean objectComputed;
	private @Nullable Object objectCache;
	private boolean intComputed;
	private @Nullable Long intCache;

	PipelineValue(PipelineFrame frame, InsnArg rawArg, int userIndex, int rawIndex, boolean receiver) {
		this.frame = frame;
		this.rawArg = rawArg;
		this.userIndex = userIndex;
		this.rawIndex = rawIndex;
		this.receiver = receiver;
	}

	/** Logical (declared) argument index, or {@code -1} for the receiver / a non-user value. */
	public int userIndex() {
		return userIndex;
	}

	/** Raw jadx invoke argument index. */
	public int rawIndex() {
		return rawIndex;
	}

	public InsnArg rawArg() {
		return rawArg;
	}

	public ArgType type() {
		return rawArg.getType();
	}

	public boolean isReceiver() {
		return receiver;
	}

	public boolean isConstant() {
		return object() != null;
	}

	public boolean isNullRef() {
		return object() == TypeMap.NULL_REF;
	}

	/** The resolved compile-time value (string, array, {@code Class}, boxed scalar, …) or {@code null}. */
	public @Nullable Object object() {
		if (!objectComputed) {
			Object v = frame.objectEvaluator().evalObject(rawArg, 0);
			objectCache = v == TypeMap.NULL_REF ? null : v; // expose typed-null through isNullRef(), not object()
			objectComputed = true;
		}
		return objectCache;
	}

	// --- integral scalars -------------------------------------------------------------------------

	private @Nullable Long longBits() {
		if (!intComputed) {
			intCache = frame.evaluator().evalInt(rawArg, 0);
			intComputed = true;
		}
		return intCache;
	}

	public @Nullable Boolean booleanValue() {
		Long v = longBits();
		return v == null ? null : v != 0L;
	}

	public @Nullable Byte byteValue() {
		Long v = longBits();
		return v == null ? null : (byte) (long) v;
	}

	public @Nullable Short shortValue() {
		Long v = longBits();
		return v == null ? null : (short) (long) v;
	}

	public @Nullable Character charValue() {
		Long v = longBits();
		return v == null ? null : (char) (long) v;
	}

	public @Nullable Integer intValue() {
		Long v = longBits();
		return v == null ? null : (int) (long) v;
	}

	public @Nullable Long longValue() {
		return longBits();
	}

	public @Nullable Float floatValue() {
		Object o = object();
		return o instanceof Float ? (Float) o : (o instanceof Number ? ((Number) o).floatValue() : null);
	}

	public @Nullable Double doubleValue() {
		Object o = object();
		return o instanceof Double ? (Double) o : (o instanceof Number ? ((Number) o).doubleValue() : null);
	}

	// --- objects ----------------------------------------------------------------------------------

	public @Nullable String string() {
		Object o = object();
		return o instanceof String ? (String) o : null;
	}

	public @Nullable CharSequence charSequence() {
		Object o = object();
		return o instanceof CharSequence ? (CharSequence) o : null;
	}

	public @Nullable Class<?> classValue() {
		Object o = object();
		return o instanceof Class<?> ? (Class<?>) o : null;
	}

	public @Nullable ArgType classType() {
		Class<?> c = classValue();
		return c == null ? null : TypeMap.fromClass(c);
	}

	// --- arrays -----------------------------------------------------------------------------------

	public @Nullable boolean[] booleans() {
		Object o = object();
		return o instanceof boolean[] ? (boolean[]) o : null;
	}

	public @Nullable byte[] bytes() {
		Object o = object();
		if (o instanceof byte[]) {
			return (byte[]) o;
		}
		return frame.evaluator().resolveByteArray(rawArg);
	}

	public @Nullable short[] shorts() {
		Object o = object();
		return o instanceof short[] ? (short[]) o : null;
	}

	public @Nullable char[] chars() {
		Object o = object();
		return o instanceof char[] ? (char[]) o : null;
	}

	public @Nullable int[] ints() {
		Object o = object();
		return o instanceof int[] ? (int[]) o : null;
	}

	public @Nullable long[] longs() {
		Object o = object();
		return o instanceof long[] ? (long[]) o : null;
	}

	public @Nullable float[] floats() {
		Object o = object();
		return o instanceof float[] ? (float[]) o : null;
	}

	public @Nullable double[] doubles() {
		Object o = object();
		return o instanceof double[] ? (double[]) o : null;
	}

	public @Nullable String[] strings() {
		Object o = object();
		return o instanceof String[] ? (String[]) o : null;
	}

	public @Nullable Class<?>[] classes() {
		Object o = object();
		return o instanceof Class<?>[] ? (Class<?>[]) o : null;
	}

	public @Nullable Object[] objects() {
		Object o = object();
		return o instanceof Object[] ? (Object[]) o : null;
	}

	// --- raw escape hatches -----------------------------------------------------------------------

	/** The instruction that produces this value (the wrapped insn, or the SSA assign of a register). */
	public @Nullable InsnNode producer() {
		if (rawArg.isInsnWrap()) {
			return ((jadx.core.dex.instructions.args.InsnWrapArg) rawArg).getWrapInsn();
		}
		if (rawArg instanceof RegisterArg) {
			return ((RegisterArg) rawArg).getAssignInsn();
		}
		return null;
	}

	public @Nullable RegisterArg register() {
		return rawArg instanceof RegisterArg ? (RegisterArg) rawArg : null;
	}

	@Override
	public String toString() {
		return "PipelineValue{" + (receiver ? "receiver" : "arg" + userIndex) + " type=" + type() + '}';
	}
}
