package jadx.plugins.stringdecrypt.eval;

import java.lang.reflect.Array;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import jadx.core.dex.instructions.args.ArgType;

/**
 * Bridge between jadx's IR type model ({@link ArgType}) and {@link Class} / boxed Java values.
 * Used by the JDK-call dispatcher to look up real {@code Method}/{@code Constructor} objects, and to
 * coerce IR values (which are uniformly modelled as {@link Long} for any integral type) into the
 * exact Java type each reflective parameter expects.
 *
 * <p>
 * Reflection lookups are intentionally narrow: only the JDK whitelist's declaring classes ever flow
 * through {@link #toClass(ArgType)}. Anything else returns {@code null} and the caller refuses.
 */
public final class TypeMap {

	private TypeMap() {
	}

	/** Tag value returned by {@link #coerce(Class, Object)} when no safe coercion exists. */
	public static final Object COERCE_FAILED = new Object();

	/** Sentinel passed by the evaluator for a literal {@code null} reference. */
	public static final Object NULL_REF = new Object();

	/**
	 * Sentinel for an argument the evaluator could not resolve (distinct from a real {@code null}). It
	 * may flow into a pure-helper slot the callee never reads (an obfuscator decoy) — but the moment
	 * any computation actually consumes it, the fold must refuse rather than silently treating it as a
	 * concrete value. {@link #coerce} rejects it so it can never reach a JDK call as a real argument.
	 */
	public static final Object UNRESOLVED = new Object();

	/** Integral types the evaluator's int-path can fold (everything else routes through evalObject). */
	public static boolean isIntegerLike(ArgType type) {
		return ArgType.BOOLEAN.equals(type) || ArgType.BYTE.equals(type) || ArgType.CHAR.equals(type)
				|| ArgType.SHORT.equals(type) || ArgType.INT.equals(type) || ArgType.LONG.equals(type);
	}

	@Nullable
	public static Class<?>[] toJavaClasses(List<ArgType> types) {
		Class<?>[] out = new Class<?>[types.size()];
		for (int i = 0; i < types.size(); i++) {
			Class<?> c = toClass(types.get(i));
			if (c == null) {
				return null;
			}
			out[i] = c;
		}
		return out;
	}

	/**
	 * Map an {@link ArgType} to its {@link Class}. Primitives map to {@code int.class} etc. Arrays
	 * recurse on the element type. Object types are resolved via {@link Class#forName(String)} —
	 * non-JDK classes will typically fail, which is correct (we never want to reflect into app code).
	 */
	@Nullable
	public static Class<?> toClass(ArgType t) {
		if (t == null) {
			return null;
		}
		if (ArgType.BOOLEAN.equals(t)) {
			return boolean.class;
		}
		if (ArgType.BYTE.equals(t)) {
			return byte.class;
		}
		if (ArgType.CHAR.equals(t)) {
			return char.class;
		}
		if (ArgType.SHORT.equals(t)) {
			return short.class;
		}
		if (ArgType.INT.equals(t)) {
			return int.class;
		}
		if (ArgType.LONG.equals(t)) {
			return long.class;
		}
		if (ArgType.FLOAT.equals(t)) {
			return float.class;
		}
		if (ArgType.DOUBLE.equals(t)) {
			return double.class;
		}
		if (ArgType.VOID.equals(t)) {
			return void.class;
		}
		if (t.isArray()) {
			Class<?> el = toClass(t.getArrayElement());
			if (el == null) {
				return null;
			}
			return Array.newInstance(el, 0).getClass();
		}
		if (t.isObject()) {
			try {
				return Class.forName(t.getObject(), false, ClassLoader.getSystemClassLoader());
			} catch (Throwable e) {
				return null;
			}
		}
		return null;
	}

	/**
	 * Map a real JVM {@link Class} produced by the evaluator back to jadx's IR type model. This
	 * intentionally records only the class name / descriptor shape; no {@code Class} object leaks
	 * past the evaluator boundary.
	 */
	@Nullable
	public static ArgType fromClass(Class<?> cls) {
		if (cls == null) {
			return null;
		}
		if (cls == boolean.class) {
			return ArgType.BOOLEAN;
		}
		if (cls == byte.class) {
			return ArgType.BYTE;
		}
		if (cls == char.class) {
			return ArgType.CHAR;
		}
		if (cls == short.class) {
			return ArgType.SHORT;
		}
		if (cls == int.class) {
			return ArgType.INT;
		}
		if (cls == long.class) {
			return ArgType.LONG;
		}
		if (cls == float.class) {
			return ArgType.FLOAT;
		}
		if (cls == double.class) {
			return ArgType.DOUBLE;
		}
		if (cls == void.class) {
			return ArgType.VOID;
		}
		if (cls.isArray()) {
			return ArgType.parse(cls.getName());
		}
		return ArgType.object(cls.getName());
	}

	/**
	 * Coerce {@code src} to a value assignable to {@code target}. Integral conversions widen/narrow
	 * silently (the IR uniformly carries integral values as {@link Long}); {@code char} accepts any
	 * integral value masked to 16 bits; reference parameters require instance-assignability or a
	 * literal {@link #NULL_REF}. Returns {@link #COERCE_FAILED} for anything else.
	 */
	public static Object coerce(Class<?> target, @Nullable Object src) {
		if (src == UNRESOLVED) {
			return COERCE_FAILED; // an unresolved value must never reach a real call argument
		}
		if (src == NULL_REF) {
			return target.isPrimitive() ? COERCE_FAILED : null;
		}
		if (src == null) {
			return target.isPrimitive() ? COERCE_FAILED : null;
		}
		if (target == long.class) {
			return (src instanceof Number) ? ((Number) src).longValue() : (src instanceof Character ? (long) (char) (Character) src : COERCE_FAILED);
		}
		if (target == int.class) {
			return (src instanceof Number) ? ((Number) src).intValue() : (src instanceof Character ? (int) (char) (Character) src : COERCE_FAILED);
		}
		if (target == short.class) {
			return (src instanceof Number) ? ((Number) src).shortValue() : COERCE_FAILED;
		}
		if (target == byte.class) {
			return (src instanceof Number) ? ((Number) src).byteValue() : COERCE_FAILED;
		}
		if (target == char.class) {
			if (src instanceof Number) {
				return (char) ((Number) src).intValue();
			}
			return src instanceof Character ? src : COERCE_FAILED;
		}
		if (target == boolean.class) {
			if (src instanceof Boolean) {
				return src;
			}
			return (src instanceof Number) ? (((Number) src).intValue() != 0) : COERCE_FAILED;
		}
		if (target == double.class) {
			return (src instanceof Number) ? ((Number) src).doubleValue() : COERCE_FAILED;
		}
		if (target == float.class) {
			return (src instanceof Number) ? ((Number) src).floatValue() : COERCE_FAILED;
		}
		if (target.isInstance(src)) {
			return src;
		}
		// CharSequence accepts String/StringBuilder/StringBuffer — already covered by isInstance,
		// but autoboxing is not (e.g. Long target with int src is unusual; we hit boxed-numeric path).
		if (target == Number.class && src instanceof Number) {
			return src;
		}
		if (target == Object.class) {
			return src;
		}
		if (target == CharSequence.class && (src instanceof String || src instanceof StringBuilder || src instanceof StringBuffer)) {
			return src;
		}
		return COERCE_FAILED;
	}

	/**
	 * @return a fresh array of coerced arguments, or {@code null} if any coercion failed
	 */
	@Nullable
	public static Object[] coerceArgs(Class<?>[] target, Object[] src) {
		if (target.length != src.length) {
			return null;
		}
		Object[] out = new Object[src.length];
		for (int i = 0; i < src.length; i++) {
			Object v = coerce(target[i], src[i]);
			if (v == COERCE_FAILED) {
				return null;
			}
			out[i] = v;
		}
		return out;
	}
}
