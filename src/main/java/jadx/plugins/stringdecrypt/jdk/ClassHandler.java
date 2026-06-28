package jadx.plugins.stringdecrypt.jdk;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import jadx.core.dex.info.MethodInfo;

/**
 * {@link Class}-level reflection: {@code Class.forName(...)}, {@code getMethod(...)},
 * {@code getField(...)}, {@code getConstructor(...)}, and friends. The safety gate is in
 * {@link #invoke}: {@code forName} only resolves names that the {@link JdkInterpreter} already
 * handles (so we never load user app classes), plus array names like {@code "[B"} / {@code "[C"}
 * and the primitive descriptor names. Method / field / constructor lookups are then delegated to
 * the loaded {@link Class} — these are pure metadata operations on JDK types and safe.
 */
public final class ClassHandler implements JdkClassHandler {

	private final JdkInterpreter interpreter;

	public ClassHandler(JdkInterpreter interpreter) {
		this.interpreter = interpreter;
	}

	@Override
	public String targetClass() {
		return "java.lang.Class";
	}

	private static final Set<String> ALLOWED = Set.of(
			"forName",
			"getMethod",
			"getDeclaredMethod",
			"getMethods",
			"getDeclaredMethods",
			"getField",
			"getDeclaredField",
			"getFields",
			"getDeclaredFields",
			"getConstructor",
			"getDeclaredConstructor",
			"getConstructors",
			"getDeclaredConstructors",
			"getName",
			"getCanonicalName",
			"getSimpleName",
			"getPackageName",
			"getTypeName",
			"isPrimitive",
			"isArray",
			"isInterface",
			"isEnum",
			"isAnnotation",
			"isInstance",
			"isAssignableFrom",
			"getComponentType",
			"getSuperclass",
			"getInterfaces",
			"getModifiers",
			"hashCode",
			"equals",
			"toString",
			"asSubclass");

	/**
	 * The pure, side-effect-free member-lookup methods that are safe to invoke <i>reflectively</i> (i.e.
	 * via {@code someHandle.invoke(SomeClass.class, "name", paramTypes)}). They only return reflection
	 * objects and never load or initialize a class, so {@link MethodReflHandler} may execute them on the
	 * host to resolve a {@code Method}/{@code Field}/{@code Constructor} handle the obfuscator built
	 * through a reflective {@code getMethod}. Deliberately excludes {@code forName} (which would trigger
	 * class initialization).
	 */
	private static final Set<String> REFLECTIVE_LOOKUPS = Set.of(
			"getMethod", "getDeclaredMethod", "getField", "getDeclaredField",
			"getConstructor", "getDeclaredConstructor");

	/** @see #REFLECTIVE_LOOKUPS */
	public boolean isReflectiveLookupAllowed(String name) {
		return REFLECTIVE_LOOKUPS.contains(name);
	}

	@Override
	public @Nullable Object invoke(MethodInfo call, @Nullable Object instance, Object[] args) {
		String name = call.getName();
		if (!ALLOWED.contains(name)) {
			return null;
		}
		try {
			if ("forName".equals(name) && args.length >= 1 && args[0] instanceof String) {
				String className = (String) args[0];
				if (!isSafeToLoad(className)) {
					return null; // refuse user app classes - we never load them
				}
				// Use the system class loader so we don't pull in any plugin-classpath surprises.
				return Class.forName(className, false, ClassLoader.getSystemClassLoader());
			}
			if (instance instanceof Class<?>) {
				Class<?> cls = (Class<?>) instance;
				switch (name) {
					case "getMethod":
						return cls.getMethod((String) args[0], asClassArr(args, 1));
					case "getDeclaredMethod":
						return cls.getDeclaredMethod((String) args[0], asClassArr(args, 1));
					case "getField":
						return cls.getField((String) args[0]);
					case "getDeclaredField":
						return cls.getDeclaredField((String) args[0]);
					case "getConstructor":
						return cls.getConstructor(asClassArr(args, 0));
					case "getDeclaredConstructor":
						return cls.getDeclaredConstructor(asClassArr(args, 0));
					case "getMethods":
						return cls.getMethods();
					case "getDeclaredMethods":
						return cls.getDeclaredMethods();
					case "getFields":
						return cls.getFields();
					case "getDeclaredFields":
						return cls.getDeclaredFields();
					case "getConstructors":
						return cls.getConstructors();
					case "getDeclaredConstructors":
						return cls.getDeclaredConstructors();
					case "getName":
						return cls.getName();
					case "getCanonicalName":
						return cls.getCanonicalName();
					case "getSimpleName":
						return cls.getSimpleName();
					case "getPackageName":
						return cls.getPackageName();
					case "getTypeName":
						return cls.getTypeName();
					case "isPrimitive":
						return cls.isPrimitive();
					case "isArray":
						return cls.isArray();
					case "isInterface":
						return cls.isInterface();
					case "isEnum":
						return cls.isEnum();
					case "isAnnotation":
						return cls.isAnnotation();
					case "isInstance":
						return cls.isInstance(args[0]);
					case "isAssignableFrom":
						return args[0] instanceof Class<?> && cls.isAssignableFrom((Class<?>) args[0]);
					case "getComponentType":
						return cls.getComponentType();
					case "getSuperclass":
						return cls.getSuperclass();
					case "getInterfaces":
						return cls.getInterfaces();
					case "getModifiers":
						return cls.getModifiers();
					case "hashCode":
						return cls.hashCode();
					case "equals":
						return cls.equals(args[0]);
					case "toString":
						return cls.toString();
					default:
						return null;
				}
			}
		} catch (Throwable t) {
			return null;
		}
		return null;
	}

	private static @Nullable Class<?>[] asClassArr(Object[] args, int from) {
		// getMethod/getConstructor varargs collapse to a single trailing Object[] (or Class[]) in
		// the dex IR. Handle every shape the evaluator may produce:
		//  - args = [..., Class[]{...}]            -> use directly
		//  - args = [..., Object[]{Class, Class}]  -> coerce element-wise
		//  - args = [..., null]                    -> empty array
		//  - args = [Class, Class, ...] flat       -> coerce from `from` onward
		if (args.length == from + 1) {
			Object tail = args[from];
			if (tail == null) {
				return new Class<?>[0];
			}
			if (tail instanceof Class<?>[]) {
				return (Class<?>[]) tail;
			}
			if (tail instanceof Object[]) {
				Object[] arr = (Object[]) tail;
				Class<?>[] out = new Class<?>[arr.length];
				for (int i = 0; i < arr.length; i++) {
					if (arr[i] != null && !(arr[i] instanceof Class<?>)) {
						return null;
					}
					out[i] = (Class<?>) arr[i];
				}
				return out;
			}
		}
		Class<?>[] out = new Class<?>[args.length - from];
		for (int i = 0; i < out.length; i++) {
			if (args[from + i] != null && !(args[from + i] instanceof Class<?>)) {
				return null;
			}
			out[i] = (Class<?>) args[from + i];
		}
		return out;
	}

	/**
	 * A class is safe to load when {@link JdkInterpreter} already has a handler for it, when it is a
	 * primitive (boxed via the matching {@code TYPE} field), or when it is an array descriptor
	 * (e.g. {@code [B}, {@code [Ljava.lang.String;}) whose element type is itself safe.
	 */
	private boolean isSafeToLoad(String className) {
		// Array descriptor form: peel off all leading '[' then check the element class.
		int dim = 0;
		while (dim < className.length() && className.charAt(dim) == '[') {
			dim++;
		}
		if (dim > 0) {
			String el = className.substring(dim);
			if (el.length() == 1) {
				return "ZBCSIJFD".indexOf(el.charAt(0)) >= 0; // primitive descriptor letter
			}
			if (el.length() >= 3 && el.charAt(0) == 'L' && el.charAt(el.length() - 1) == ';') {
				return isSafeToLoad(el.substring(1, el.length() - 1).replace('/', '.'));
			}
			return false;
		}
		// A class with a registered handler, or any standard JDK API class. The latter is loaded with
		// initialize=false (see invoke), so it has no side effects — it only yields a Class constant or a
		// reflection param/lookup type (e.g. java.math.MathContext as a getMethod parameter). Method
		// *invocation* on an unhandled class is still refused by MethodReflHandler, so this stays sound.
		return interpreter.handles(className) || isStandardJdkClass(className);
	}

	private static boolean isStandardJdkClass(String className) {
		return className.startsWith("java.") || className.startsWith("javax.");
	}
}
