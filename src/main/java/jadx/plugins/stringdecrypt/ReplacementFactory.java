package jadx.plugins.stringdecrypt;

import java.lang.reflect.Array;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import jadx.core.Consts;
import jadx.core.dex.instructions.ConstClassNode;
import jadx.core.dex.instructions.ConstStringNode;
import jadx.core.dex.instructions.FilledNewArrayNode;
import jadx.core.dex.instructions.InsnType;
import jadx.core.dex.instructions.args.ArgType;
import jadx.core.dex.instructions.args.InsnArg;
import jadx.core.dex.instructions.args.RegisterArg;
import jadx.core.dex.nodes.InsnNode;
import jadx.plugins.stringdecrypt.eval.TypeMap;

/**
 * The single boundary between "a resolver computed this Java value" and "valid replacement jadx IR".
 * Converts a {@code String} / primitive-or-boxed scalar / primitive-or-object array / {@code Class<?>}
 * / typed {@code null} into a const-bearing {@link InsnNode}, gated by strict target-type compatibility
 * and (for the built-in fold path) a decoy-suppression filter.
 *
 * <p>
 * Extracted from {@code StringDecryptPass} so every replacement source — the built-in folders, the
 * reflective de-indirection rewriter, and user {@code .jadx.kts} script pipelines — shares one tested
 * value-to-IR conversion rather than each re-deriving it.
 */
final class ReplacementFactory {

	private ReplacementFactory() {
	}

	/**
	 * Build the const IR node that replaces {@code from}'s value, or {@code null} if {@code value} can't
	 * be represented for {@code targetType}.
	 *
	 * @param decrypted accumulates a quoted form of each emitted string (for the method summary comment)
	 * @param suppressNonPrintableLookup when true, a non-printable {@code String} is refused because it
	 *        flows into a reflective name lookup ({@code Class.forName} / {@code getMethod} / ...) where
	 *        no class/member could have such a name — leaving the call un-folded reads better than a
	 *        garbage literal. Resolvers emitting an explicit user value pass {@code false}.
	 */
	static @Nullable InsnNode makeReplacementInsn(InsnNode from, Object value,
			@Nullable ArgType targetType, List<String> decrypted, boolean suppressNonPrintableLookup) {
		if (!isCompatibleReplacementValue(value, targetType)) {
			return null;
		}
		InsnNode node;
		if (value == TypeMap.NULL_REF) {
			node = constScalarInsn(0, targetType != null ? targetType : ArgType.OBJECT);
		} else if (value instanceof String) {
			String text = (String) value;
			if (suppressNonPrintableLookup && !Eval.isPrintable(text)) {
				return null;
			}
			decrypted.add('"' + text.replace("\\", "\\\\").replace("\"", "\\\"") + '"');
			node = new ConstStringNode(text);
		} else if (value instanceof Class<?>) {
			ArgType clsType = TypeMap.fromClass((Class<?>) value);
			if (clsType == null || ArgType.VOID.equals(clsType)) {
				return null;
			}
			node = new ConstClassNode(clsType);
		} else if (value.getClass().isArray()) {
			node = makeArrayInsn(value, targetType);
			if (node == null) {
				return null;
			}
		} else {
			node = makeScalarInsn(value, targetType);
			if (node == null) {
				return null;
			}
		}
		copyReplacementMetadata(from, node);
		return node;
	}

	static void copyReplacementMetadata(InsnNode from, InsnNode node) {
		RegisterArg result = from.getResult();
		if (result != null) {
			node.setResult(result.duplicate());
		}
		node.copyAttributesFrom(from);
		node.inheritMetadata(from);
		node.setOffset(from.getOffset());
	}

	private static @Nullable InsnNode makeScalarInsn(Object value, @Nullable ArgType targetType) {
		ArgType literalType = scalarLiteralType(value, targetType);
		if (literalType == null) {
			return null;
		}
		Long literal = scalarLiteralBits(value, literalType);
		return literal != null ? constScalarInsn(literal, literalType) : null;
	}

	private static InsnNode constScalarInsn(long literal, ArgType literalType) {
		InsnNode node = new InsnNode(InsnType.CONST, 1);
		node.addArg(InsnArg.lit(literal, literalType));
		return node;
	}

	private static @Nullable FilledNewArrayNode makeArrayInsn(Object array, @Nullable ArgType targetType) {
		ArgType elemType = arrayElementType(array, targetType);
		if (elemType == null || ArgType.VOID.equals(elemType)) {
			return null;
		}
		int len = Array.getLength(array);
		FilledNewArrayNode node = new FilledNewArrayNode(elemType, len);
		for (int i = 0; i < len; i++) {
			InsnArg arg = valueToArrayArg(Array.get(array, i), elemType);
			if (arg == null) {
				return null;
			}
			node.addArg(arg);
		}
		return node;
	}

	private static @Nullable ArgType arrayElementType(Object array, @Nullable ArgType targetType) {
		if (targetType != null && targetType.isArray()) {
			return targetType.getArrayElement();
		}
		Class<?> component = array.getClass().getComponentType();
		return TypeMap.fromClass(component);
	}

	private static @Nullable InsnArg valueToArrayArg(@Nullable Object value, ArgType elemType) {
		if (value == null || value == TypeMap.NULL_REF) {
			return (elemType.isObject() || elemType.isArray()) ? InsnArg.lit(0, elemType) : null;
		}
		if (value instanceof String) {
			return InsnArg.wrapInsnIntoArg(new ConstStringNode((String) value));
		}
		if (value instanceof Class<?>) {
			ArgType clsType = TypeMap.fromClass((Class<?>) value);
			return clsType != null && !ArgType.VOID.equals(clsType)
					? InsnArg.wrapInsnIntoArg(new ConstClassNode(clsType))
					: null;
		}
		if (value.getClass().isArray()) {
			FilledNewArrayNode nested = makeArrayInsn(value, elemType);
			return nested != null ? InsnArg.wrapInsnIntoArg(nested) : null;
		}
		ArgType literalType = elemType.isPrimitive() ? elemType : scalarLiteralType(value, elemType);
		Long literal = literalType != null ? scalarLiteralBits(value, literalType) : null;
		return literal != null ? InsnArg.lit(literal, literalType) : null;
	}

	private static @Nullable ArgType scalarLiteralType(Object value, @Nullable ArgType targetType) {
		if (targetType != null && targetType.isPrimitive()) {
			return targetType;
		}
		if (value instanceof Boolean) {
			return ArgType.BOOLEAN;
		}
		if (value instanceof Character) {
			return ArgType.CHAR;
		}
		if (value instanceof Byte) {
			return ArgType.BYTE;
		}
		if (value instanceof Short) {
			return ArgType.SHORT;
		}
		if (value instanceof Integer) {
			return ArgType.INT;
		}
		if (value instanceof Long) {
			return ArgType.LONG;
		}
		if (value instanceof Float) {
			return ArgType.FLOAT;
		}
		if (value instanceof Double) {
			return ArgType.DOUBLE;
		}
		return null;
	}

	private static @Nullable Long scalarLiteralBits(Object value, ArgType literalType) {
		if (ArgType.BOOLEAN.equals(literalType)) {
			if (value instanceof Boolean) {
				return (Boolean) value ? 1L : 0L;
			}
			if (value instanceof Number) {
				return ((Number) value).longValue() == 0L ? 0L : 1L;
			}
			return null;
		}
		if (ArgType.CHAR.equals(literalType)) {
			if (value instanceof Character) {
				return (long) (Character) value;
			}
			return value instanceof Number ? (long) (char) ((Number) value).intValue() : null;
		}
		if (ArgType.BYTE.equals(literalType)) {
			return value instanceof Number ? (long) ((Number) value).byteValue() : null;
		}
		if (ArgType.SHORT.equals(literalType)) {
			return value instanceof Number ? (long) ((Number) value).shortValue() : null;
		}
		if (ArgType.INT.equals(literalType)) {
			return value instanceof Number ? (long) ((Number) value).intValue() : null;
		}
		if (ArgType.LONG.equals(literalType)) {
			return value instanceof Number ? ((Number) value).longValue() : null;
		}
		if (ArgType.FLOAT.equals(literalType)) {
			return value instanceof Number ? (long) Float.floatToIntBits(((Number) value).floatValue()) : null;
		}
		if (ArgType.DOUBLE.equals(literalType)) {
			return value instanceof Number ? Double.doubleToLongBits(((Number) value).doubleValue()) : null;
		}
		return null;
	}

	static boolean isCompatibleReplacementValue(Object value, @Nullable ArgType targetType) {
		if (targetType == null || !targetType.isTypeKnown()) {
			return true;
		}
		if (value == TypeMap.NULL_REF) {
			return targetType.isObject() || targetType.isArray();
		}
		if (targetType.isPrimitive()) {
			return scalarLiteralBits(value, targetType) != null;
		}
		Class<?> targetClass = TypeMap.toClass(erasedClassType(targetType));
		if (targetClass == null) {
			return false;
		}
		Class<?> valueClass = replacementValueClass(value);
		if (valueClass == null) {
			return false;
		}
		if (targetClass.isPrimitive()) {
			return false;
		}
		if (targetClass.isAssignableFrom(valueClass)) {
			return true;
		}
		Class<?> boxed = boxedScalarClass(value);
		return boxed != null && targetClass.isAssignableFrom(boxed);
	}

	private static ArgType erasedClassType(ArgType targetType) {
		if (targetType.isObject() && Consts.CLASS_CLASS.equals(targetType.getObject())) {
			return ArgType.CLASS;
		}
		return targetType;
	}

	private static @Nullable Class<?> replacementValueClass(Object value) {
		if (value instanceof String) {
			return String.class;
		}
		if (value instanceof Class<?>) {
			return Class.class;
		}
		if (value != null && value.getClass().isArray()) {
			return value.getClass();
		}
		Class<?> boxed = boxedScalarClass(value);
		return boxed != null ? boxed : value.getClass();
	}

	private static @Nullable Class<?> boxedScalarClass(Object value) {
		if (value instanceof Boolean) {
			return Boolean.class;
		}
		if (value instanceof Character) {
			return Character.class;
		}
		if (value instanceof Byte) {
			return Byte.class;
		}
		if (value instanceof Short) {
			return Short.class;
		}
		if (value instanceof Integer) {
			return Integer.class;
		}
		if (value instanceof Long) {
			return Long.class;
		}
		if (value instanceof Float) {
			return Float.class;
		}
		if (value instanceof Double) {
			return Double.class;
		}
		return null;
	}
}
