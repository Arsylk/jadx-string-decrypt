package jadx.plugins.stringdecrypt.jdk;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.jetbrains.annotations.Nullable;

import jadx.core.dex.info.MethodInfo;

/**
 * {@link java.lang.reflect.Field}: pure metadata + {@code get*} for {@code static final} fields on
 * a JDK class we handle (typical examples: {@code Integer.TYPE}, {@code Long.MAX_VALUE},
 * {@code StandardCharsets.UTF_8}). Non-final / non-static / non-JDK fields are refused — their
 * values are not compile-time-constant.
 */
public final class FieldReflHandler implements JdkClassHandler {

	private final JdkInterpreter interpreter;

	public FieldReflHandler(JdkInterpreter interpreter) {
		this.interpreter = interpreter;
	}

	@Override
	public String targetClass() {
		return "java.lang.reflect.Field";
	}

	@Override
	public @Nullable Object invoke(MethodInfo call, @Nullable Object instance, Object[] args) {
		if (!(instance instanceof Field)) {
			return null;
		}
		Field f = (Field) instance;
		String name = call.getName();
		try {
			switch (name) {
				case "get":
				case "getInt":
				case "getLong":
				case "getShort":
				case "getByte":
				case "getChar":
				case "getBoolean":
				case "getFloat":
				case "getDouble":
					return safeGet(f, name);
				case "getName":
					return f.getName();
				case "getType":
					return f.getType();
				case "getDeclaringClass":
					return f.getDeclaringClass();
				case "getModifiers":
					return f.getModifiers();
				case "toString":
					return f.toString();
				case "hashCode":
					return f.hashCode();
				case "equals":
					return f.equals(args[0]);
				default:
					return null;
			}
		} catch (Throwable t) {
			return null;
		}
	}

	@Nullable
	private Object safeGet(Field f, String getter) throws Exception {
		int mods = f.getModifiers();
		if (!Modifier.isStatic(mods) || !Modifier.isFinal(mods)) {
			return null; // only static-final fields are compile-time-constant
		}
		String declClass = f.getDeclaringClass().getName();
		if (interpreter.getHandler(declClass) == null) {
			return null;
		}
		f.setAccessible(true);
		switch (getter) {
			case "getInt":
				return f.getInt(null);
			case "getLong":
				return f.getLong(null);
			case "getShort":
				return f.getShort(null);
			case "getByte":
				return f.getByte(null);
			case "getChar":
				return f.getChar(null);
			case "getBoolean":
				return f.getBoolean(null);
			case "getFloat":
				return f.getFloat(null);
			case "getDouble":
				return f.getDouble(null);
			default:
				return f.get(null);
		}
	}
}
