package jadx.plugins.stringdecrypt.jdk;

import java.lang.reflect.Method;

import org.jetbrains.annotations.Nullable;

import jadx.core.dex.info.MethodInfo;

/**
 * {@link java.lang.reflect.Method}: most importantly {@code invoke}. The safety re-check makes the
 * reflective indirection equivalent to the direct call — we only invoke a {@link Method} whose
 * declaring class AND method name appear in some registered {@link ReflectiveJdkHandler}'s
 * allow-list. So an obfuscator's chain
 * {@code Class.forName("java.lang.StringBuilder").getMethod("reverse").invoke(sb)} folds the same
 * way that a direct {@code sb.reverse()} would.
 *
 * <p>
 * Pure metadata operations ({@code getName}, {@code getReturnType}, etc.) are routed directly to
 * the {@link Method}.
 */
public final class MethodReflHandler implements JdkClassHandler {

	private final JdkInterpreter interpreter;

	public MethodReflHandler(JdkInterpreter interpreter) {
		this.interpreter = interpreter;
	}

	@Override
	public String targetClass() {
		return "java.lang.reflect.Method";
	}

	@Override
	public @Nullable Object invoke(MethodInfo call, @Nullable Object instance, Object[] args) {
		if (!(instance instanceof Method)) {
			return null;
		}
		Method m = (Method) instance;
		String name = call.getName();
		try {
			switch (name) {
				case "invoke":
					return safeInvoke(m, args);
				case "getName":
					return m.getName();
				case "getReturnType":
					return m.getReturnType();
				case "getParameterTypes":
					return m.getParameterTypes();
				case "getParameterCount":
					return m.getParameterCount();
				case "getDeclaringClass":
					return m.getDeclaringClass();
				case "getModifiers":
					return m.getModifiers();
				case "isVarArgs":
					return m.isVarArgs();
				case "toString":
					return m.toString();
				case "hashCode":
					return m.hashCode();
				case "equals":
					return m.equals(args[0]);
				default:
					return null;
			}
		} catch (Throwable t) {
			return null;
		}
	}

	@Nullable
	private Object safeInvoke(Method m, Object[] args) {
		String declClass = m.getDeclaringClass().getName();
		JdkClassHandler h = interpreter.getHandler(declClass);
		boolean allowed = (h instanceof ReflectiveJdkHandler && ((ReflectiveJdkHandler) h).isAllowed(m.getName()))
				// pure Class member lookups (getMethod/getField/getConstructor): the obfuscator obtains a
				// handle through a reflective getMethod, so resolving it needs to run that lookup on the host.
				|| (h instanceof ClassHandler && ((ClassHandler) h).isReflectiveLookupAllowed(m.getName()));
		if (!allowed) {
			// Either declaring class is unhandled (user app code) or this method is not allow-listed
			// (e.g. String.intern()). Refuse so the reflective call stays in source.
			return null;
		}
		Object target = args.length >= 1 ? args[0] : null;
		if (ReflectiveJdkHandler.isIdentityNondeterministic(target, m.getName())) {
			return null; // Object-identity hashCode/toString on a non-overriding receiver -> not constant
		}
		Object[] callArgs;
		if (args.length >= 2 && args[1] instanceof Object[]) {
			callArgs = (Object[]) args[1];
		} else {
			callArgs = new Object[0];
		}
		try {
			m.setAccessible(true);
			return m.invoke(target, callArgs);
		} catch (Throwable t) {
			return null; // user method threw or args mismatched: stay sound by refusing the fold
		}
	}
}
