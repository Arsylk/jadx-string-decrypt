package jadx.plugins.stringdecrypt.jdk;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
	private final ConcurrentMap<MemberKey, Method> methodCache = new ConcurrentHashMap<>();
	private final ConcurrentMap<MemberKey, Constructor<?>> ctorCache = new ConcurrentHashMap<>();

	protected ReflectiveJdkHandler(String targetClassName) {
		this(targetClassName, targetClassName);
	}

	/**
	 * @param dispatchClassName the name the interpreter dispatches on, i.e. jadx's dotted
	 *        {@code getFullName()} (for a nested class this is {@code java.util.Base64.Decoder}).
	 * @param reflectClassName the name handed to {@link Class#forName(String)} (for a nested class
	 *        the binary {@code $} form, e.g. {@code java.util.Base64$Decoder}). For a top-level class
	 *        the two are identical.
	 */
	protected ReflectiveJdkHandler(String dispatchClassName, String reflectClassName) {
		this.targetClassName = dispatchClassName;
		try {
			this.clazz = Class.forName(reflectClassName);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("JDK class not found: " + reflectClassName, e);
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
				Constructor<?> ctor = resolveConstructor(paramTypes);
				return ctor != null ? ctor.newInstance(coerced) : null;
			}
			Method m = resolveMethod(name, paramTypes);
			if (m == null) {
				return null;
			}
			if (isIdentityNondeterministic(instance, name)) {
				return null;
			}
			return m.invoke(instance, coerced);
		} catch (InvocationTargetException ite) {
			return null; // a guarded throw (e.g. NumberFormatException) -> can't fold, refuse
		} catch (Throwable t) {
			return null;
		}
	}

	/**
	 * True iff invoking {@code name} on {@code instance} would dispatch to {@link Object}'s own
	 * implementation (the runtime class doesn't override it). Folding {@code hashCode()} /
	 * {@code toString()} in that case is unsound: both derive from the per-run identity hash, so the
	 * "constant" would change on every execution and never match the target device. Classes that
	 * override them (String, BigInteger, StringBuilder.toString, ...) fold normally.
	 */
	public static boolean isIdentityNondeterministic(@Nullable Object instance, String name) {
		if (instance == null || (!"hashCode".equals(name) && !"toString".equals(name))) {
			return false;
		}
		try {
			return instance.getClass().getMethod(name).getDeclaringClass() == Object.class;
		} catch (NoSuchMethodException e) {
			return true; // can't prove it's overridden -> refuse, stay sound
		}
	}

	private @Nullable Constructor<?> resolveConstructor(Class<?>[] paramTypes) {
		MemberKey key = new MemberKey("<init>", paramTypes);
		Constructor<?> cached = ctorCache.get(key);
		if (cached != null) {
			return cached;
		}
		try {
			Constructor<?> ctor = clazz.getDeclaredConstructor(paramTypes);
			ctor.setAccessible(true);
			Constructor<?> prev = ctorCache.putIfAbsent(key, ctor);
			return prev != null ? prev : ctor;
		} catch (NoSuchMethodException ignore) {
			return null;
		}
	}

	private @Nullable Method resolveMethod(String name, Class<?>[] paramTypes) {
		MemberKey key = new MemberKey(name, paramTypes);
		Method cached = methodCache.get(key);
		if (cached != null) {
			return cached;
		}
		Method method;
		try {
			method = clazz.getMethod(name, paramTypes);
		} catch (NoSuchMethodException ignore) {
			try {
				method = clazz.getDeclaredMethod(name, paramTypes);
			} catch (NoSuchMethodException ignore2) {
				return null;
			}
		}
		method.setAccessible(true);
		Method prev = methodCache.putIfAbsent(key, method);
		return prev != null ? prev : method;
	}

	private static final class MemberKey {
		private final String name;
		private final Class<?>[] params;
		private final int hash;

		MemberKey(String name, Class<?>[] params) {
			this.name = name;
			this.params = params.clone();
			this.hash = 31 * name.hashCode() + Arrays.hashCode(this.params);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof MemberKey)) {
				return false;
			}
			MemberKey other = (MemberKey) obj;
			return name.equals(other.name) && Arrays.equals(params, other.params);
		}

		@Override
		public int hashCode() {
			return hash;
		}
	}
}
