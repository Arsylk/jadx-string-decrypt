package jadx.plugins.stringdecrypt;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import jadx.core.dex.instructions.args.ArgType;
import jadx.core.dex.nodes.InsnNode;
import jadx.plugins.stringdecrypt.eval.TypeMap;

/**
 * The stable, typed contract a {@link ScriptPipeline} returns. The plugin — not the script — turns it
 * into valid jadx IR (through the shared {@link ReplacementFactory}), copying the original
 * instruction's result register, attributes and offset, and validating target-type compatibility. A
 * script never builds IR for the common cases.
 *
 * <p>
 * Construct via the static {@code keep()/replace…()} factories, then optionally chain fluent metadata
 * ({@link #comment}, {@link #cleanupArg}, {@link #targetType}).
 */
public final class PipelineResult {

	public enum Kind {
		/** Decline; the next pipeline (then the built-in folders) get a turn. */
		KEEP,
		/** Replace the candidate instruction with the carried value / node. */
		REPLACE,
		/** Add a comment but do not replace; continue to the next pipeline. */
		COMMENT_ONLY,
		/** A controlled failure; logged, then treated as a decline. */
		FAIL
	}

	private final Kind kind;
	private final @Nullable Object value; // replacement value (scalar/array/Class/String/NULL_REF), or null
	private final @Nullable ArgType classType; // set by replaceClassType: emit a class literal of this type
	private final @Nullable InsnNode rawInsn; // set by replaceInsn: caller-supplied IR
	private @Nullable ArgType targetTypeOverride;
	private @Nullable String message;
	private final @Nullable Throwable cause;
	private final List<Integer> cleanupUserArgs = new ArrayList<>(0);
	private final List<Integer> cleanupRawArgs = new ArrayList<>(0);
	private boolean cleanupReceiver;

	private PipelineResult(Kind kind, @Nullable Object value, @Nullable ArgType classType,
			@Nullable InsnNode rawInsn, @Nullable String message, @Nullable Throwable cause) {
		this.kind = kind;
		this.value = value;
		this.classType = classType;
		this.rawInsn = rawInsn;
		this.message = message;
		this.cause = cause;
	}

	// --- control ----------------------------------------------------------------------------------

	public static PipelineResult keep() {
		return new PipelineResult(Kind.KEEP, null, null, null, null, null);
	}

	public static PipelineResult commentOnly(String message) {
		return new PipelineResult(Kind.COMMENT_ONLY, null, null, null, message, null);
	}

	public static PipelineResult fail(String message) {
		return new PipelineResult(Kind.FAIL, null, null, null, message, null);
	}

	public static PipelineResult fail(String message, Throwable cause) {
		return new PipelineResult(Kind.FAIL, null, null, null, message, cause);
	}

	// --- value replacement (routed through ReplacementFactory) ------------------------------------

	private static PipelineResult replace(@Nullable Object value) {
		return new PipelineResult(Kind.REPLACE, value, null, null, null, null);
	}

	/** Replace with any value the {@link ReplacementFactory} can represent (string/scalar/array/Class/null). */
	public static PipelineResult replaceObject(Object value) {
		return replace(value);
	}

	/** Replace with a caller-supplied jadx IR node (advanced escape hatch). */
	public static PipelineResult replaceInsn(InsnNode insn) {
		return new PipelineResult(Kind.REPLACE, null, null, insn, null, null);
	}

	public static PipelineResult replaceString(String value) {
		return replace(value);
	}

	public static PipelineResult replaceCharSequence(CharSequence value) {
		return replace(value == null ? null : value.toString());
	}

	public static PipelineResult replaceBoolean(boolean value) {
		return replace(value);
	}

	public static PipelineResult replaceByte(byte value) {
		return replace(value);
	}

	public static PipelineResult replaceShort(short value) {
		return replace(value);
	}

	public static PipelineResult replaceChar(char value) {
		return replace(value);
	}

	public static PipelineResult replaceInt(int value) {
		return replace(value);
	}

	public static PipelineResult replaceLong(long value) {
		return replace(value);
	}

	public static PipelineResult replaceFloat(float value) {
		return replace(value);
	}

	public static PipelineResult replaceDouble(double value) {
		return replace(value);
	}

	public static PipelineResult replaceClass(Class<?> cls) {
		return replace(cls);
	}

	/** Replace with the {@code .class} literal of a jadx {@link ArgType} (handles app types). */
	public static PipelineResult replaceClassType(ArgType type) {
		return new PipelineResult(Kind.REPLACE, null, type, null, null, null);
	}

	public static PipelineResult replaceBooleans(boolean[] value) {
		return replace(value);
	}

	public static PipelineResult replaceBytes(byte[] value) {
		return replace(value);
	}

	public static PipelineResult replaceShorts(short[] value) {
		return replace(value);
	}

	public static PipelineResult replaceChars(char[] value) {
		return replace(value);
	}

	public static PipelineResult replaceInts(int[] value) {
		return replace(value);
	}

	public static PipelineResult replaceLongs(long[] value) {
		return replace(value);
	}

	public static PipelineResult replaceFloats(float[] value) {
		return replace(value);
	}

	public static PipelineResult replaceDoubles(double[] value) {
		return replace(value);
	}

	public static PipelineResult replaceStrings(String[] value) {
		return replace(value);
	}

	public static PipelineResult replaceClasses(Class<?>[] value) {
		return replace(value);
	}

	/** Replace with any primitive/object array the {@link ReplacementFactory} can represent. */
	public static PipelineResult replaceArray(Object array) {
		return replace(array);
	}

	/** Replace with a typed {@code null} reference (renders as {@code null} of {@code targetType}). */
	public static PipelineResult replaceNull(ArgType targetType) {
		PipelineResult r = new PipelineResult(Kind.REPLACE, TypeMap.NULL_REF, null, null, null, null);
		r.targetTypeOverride = targetType;
		return r;
	}

	// --- fluent metadata --------------------------------------------------------------------------

	public PipelineResult comment(String message) {
		this.message = message;
		return this;
	}

	public PipelineResult cleanupArg(int userIndex) {
		cleanupUserArgs.add(userIndex);
		return this;
	}

	public PipelineResult cleanupRawArg(int rawIndex) {
		cleanupRawArgs.add(rawIndex);
		return this;
	}

	public PipelineResult cleanupReceiver() {
		cleanupReceiver = true;
		return this;
	}

	public PipelineResult cleanup(PipelineValue value) {
		if (value.isReceiver()) {
			cleanupReceiver = true;
		} else if (value.userIndex() >= 0) {
			cleanupUserArgs.add(value.userIndex());
		} else {
			cleanupRawArgs.add(value.rawIndex());
		}
		return this;
	}

	/** Override the target type the replacement is validated against (must stay assignable). */
	public PipelineResult targetType(ArgType targetType) {
		this.targetTypeOverride = targetType;
		return this;
	}

	// --- package-private accessors (used by ScriptPipelineResolver) -------------------------------

	Kind kind() {
		return kind;
	}

	@Nullable
	Object value() {
		return value;
	}

	@Nullable
	ArgType classType() {
		return classType;
	}

	@Nullable
	InsnNode rawInsn() {
		return rawInsn;
	}

	@Nullable
	ArgType targetTypeOverride() {
		return targetTypeOverride;
	}

	@Nullable
	String message() {
		return message;
	}

	@Nullable
	Throwable cause() {
		return cause;
	}

	List<Integer> cleanupUserArgs() {
		return cleanupUserArgs;
	}

	List<Integer> cleanupRawArgs() {
		return cleanupRawArgs;
	}

	boolean cleanupReceiverRequested() {
		return cleanupReceiver;
	}
}
