package jadx.plugins.stringdecrypt.jdk;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import jadx.core.dex.info.MethodInfo;
import jadx.plugins.stringdecrypt.eval.TypeMap;

/**
 * Base {@link JdkClassHandler} that resolves and invokes methods via {@link java.lang.reflect}
 * against a single target {@link Class}, gated by an allow-list of method names (constructors are
 * always allowed for a whitelisted class).
 *
 * <p>
 * Subclasses populate the allow-list in their constructors; everything outside it is refused. This
 * keeps each handler a single declaration of "what's pure on this class", which is both auditable
 * and trivial to extend.
 *
 * <p>
 * All thrown exceptions are swallowed: any reflective failure (missing overload, illegal access,
 * a callee that threw) is treated as "cannot fold", which is sound — the call is simply left as-is.
 */
public abstract class ReflectiveJdkHandler implements JdkClassHandler {

	private final String targetClassName;
	private final Class<?> clazz;
	private final Set<String> allowedMethods = new HashSet<>();

	protected ReflectiveJdkHandler(String targetClassName) {
		this.targetClassName = targetClassName;
		try {
			this.clazz = Class.forName(targetClassName);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("JDK class not found: " + targetClassName, e);
		}
		registerMethods(allowedMethods);
	}

	/** Subclass hook: populate the set with every method name that is safe to fold. */
	protected abstract void registerMethods(Set<String> allow);

	/** True iff {@code methodName} is on this handler's allow-list (or is a constructor). */
	public final boolean isAllowed(String methodName) {
		return "<init>".equals(methodName) || allowedMethods.contains(methodName);
	}

	@Override
	public final String targetClass() {
		return targetClassName;
	}

	@Override
	public final @Nullable Object invoke(MethodInfo call, @Nullable Object instance, Object[] args) {
		String name = call.getName();
		boolean isCtor = "<init>".equals(name);
		if (!isCtor && !allowedMethods.contains(name)) {
			return null; // not on the per-class allow-list -> refuse
		}
		Class<?>[] paramTypes = TypeMap.toJavaClasses(call.getArgumentsTypes());
		if (paramTypes == null) {
			return null; // a parameter type references an unloadable class
		}
		Object[] coerced = TypeMap.coerceArgs(paramTypes, args);
		if (coerced == null) {
			return null;
		}
		try {
			if (isCtor) {
				Constructor<?> ctor = clazz.getDeclaredConstructor(paramTypes);
				ctor.setAccessible(true);
				return ctor.newInstance(coerced);
			}
			Method m = resolveMethod(name, paramTypes);
			if (m == null) {
				return null;
			}
			m.setAccessible(true);
			return m.invoke(instance, coerced);
		} catch (InvocationTargetException ite) {
			return null; // a guarded throw (e.g. NumberFormatException) -> can't fold, refuse
		} catch (Throwable t) {
			return null;
		}
	}

	private @Nullable Method resolveMethod(String name, Class<?>[] paramTypes) {
		try {
			return clazz.getMethod(name, paramTypes);
		} catch (NoSuchMethodException ignore) {
			// fall through to declared (covers package-private static helpers, e.g. Arrays.copyOfRangeByte)
		}
		try {
			return clazz.getDeclaredMethod(name, paramTypes);
		} catch (NoSuchMethodException ignore) {
			return null;
		}
	}
}
