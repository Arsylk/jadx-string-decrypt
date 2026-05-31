package jadx.plugins.stringdecrypt.jdk;

import java.lang.reflect.Constructor;

import org.jetbrains.annotations.Nullable;

import jadx.core.dex.info.MethodInfo;

/**
 * {@link java.lang.reflect.Constructor}: {@code newInstance(args)} folds the same way that the
 * direct {@code new X(...)} would, provided {@code X} is in the handler whitelist. Metadata
 * lookups (name, declaring class, parameter types) are direct.
 */
public final class ConstructorReflHandler implements JdkClassHandler {

	private final JdkInterpreter interpreter;

	public ConstructorReflHandler(JdkInterpreter interpreter) {
		this.interpreter = interpreter;
	}

	@Override
	public String targetClass() {
		return "java.lang.reflect.Constructor";
	}

	@Override
	public @Nullable Object invoke(MethodInfo call, @Nullable Object instance, Object[] args) {
		if (!(instance instanceof Constructor<?>)) {
			return null;
		}
		Constructor<?> c = (Constructor<?>) instance;
		String name = call.getName();
		try {
			switch (name) {
				case "newInstance":
					return safeNewInstance(c, args);
				case "getName":
					return c.getName();
				case "getParameterTypes":
					return c.getParameterTypes();
				case "getParameterCount":
					return c.getParameterCount();
				case "getDeclaringClass":
					return c.getDeclaringClass();
				case "getModifiers":
					return c.getModifiers();
				case "isVarArgs":
					return c.isVarArgs();
				case "toString":
					return c.toString();
				case "hashCode":
					return c.hashCode();
				case "equals":
					return c.equals(args[0]);
				default:
					return null;
			}
		} catch (Throwable t) {
			return null;
		}
	}

	@Nullable
	private Object safeNewInstance(Constructor<?> c, Object[] args) {
		String declClass = c.getDeclaringClass().getName();
		if (interpreter.getHandler(declClass) == null) {
			return null; // app class (or a JDK class we deliberately don't handle)
		}
		Object[] callArgs;
		if (args.length >= 1 && args[0] instanceof Object[]) {
			callArgs = (Object[]) args[0];
		} else {
			callArgs = new Object[0];
		}
		try {
			c.setAccessible(true);
			return c.newInstance(callArgs);
		} catch (Throwable t) {
			return null;
		}
	}
}
