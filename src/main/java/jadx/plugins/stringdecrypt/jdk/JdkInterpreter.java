package jadx.plugins.stringdecrypt.jdk;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import jadx.core.dex.info.MethodInfo;

/**
 * Dispatcher for "fold a pure JDK call to its constant result". Holds a registry of
 * {@link JdkClassHandler}s keyed by declaring class name; routes each incoming call to the matching
 * handler (or returns null when no handler is registered, meaning the call is left alone).
 *
 * <p>
 * Extension point: register a new {@link JdkClassHandler} (with its target class and allow-listed
 * methods) and any call to that class becomes foldable. No changes to {@code Evaluator},
 * {@code ObjectEvaluator}, or the pass itself.
 */
public final class JdkInterpreter {

	private final Map<String, JdkClassHandler> handlers = new LinkedHashMap<>();

	public JdkInterpreter() {
		registerDefaults();
	}

	private void registerDefaults() {
		register(new BigIntegerHandler());
		register(new BigDecimalHandler());
		register(new StringHandler());
		register(new StringBuilderHandler("java.lang.StringBuilder"));
		register(new StringBuilderHandler("java.lang.StringBuffer"));
		register(new IntegerBoxHandler("java.lang.Integer"));
		register(new IntegerBoxHandler("java.lang.Long"));
		register(new IntegerBoxHandler("java.lang.Short"));
		register(new IntegerBoxHandler("java.lang.Byte"));
		register(new IntegerBoxHandler("java.lang.Boolean"));
		register(new CharacterHandler());
		register(new MathHandler());
		register(new ArraysHandler());
		register(new CharsetHandler());
		register(new StandardCharsetsHandler()); // no methods, just enables SGET on UTF_8 / ISO_8859_1 / ...
		register(new ObjectHandler()); // virtual dispatch for toString/equals/hashCode/getClass
		// Reflection chain (Class/Method/Constructor/Field/AccessibleObject) — folds reflective
		// bridges back to direct calls when every link resolves to a JDK whitelist member.
		register(new ClassHandler(this));
		register(new MethodReflHandler(this));
		register(new ConstructorReflHandler(this));
		register(new FieldReflHandler(this));
		register(new AccessibleObjectHandler());
	}

	/** Look up the registered handler for a declaring class, or {@code null} if none. */
	@Nullable
	public JdkClassHandler getHandler(String declClass) {
		return handlers.get(declClass);
	}

	public void register(JdkClassHandler h) {
		handlers.put(h.targetClass(), h);
	}

	/**
	 * Drop the handler for {@code declClass}. Used to turn off entire fold categories at runtime
	 * (e.g. disabling reflective-bridge folding by unregistering Class/Method/Field/Constructor
	 * handlers) without rebuilding the interpreter.
	 */
	public void unregister(String declClass) {
		handlers.remove(declClass);
	}

	/** @return the call's constant result, or null if no handler exists / the handler refused. */
	@Nullable
	public Object invoke(MethodInfo call, @Nullable Object instance, Object[] args) {
		JdkClassHandler h = handlers.get(call.getDeclClass().getFullName());
		return h == null ? null : h.invoke(call, instance, args);
	}

	/** Does any registered handler claim this declaring class? */
	public boolean handles(String declClass) {
		return handlers.containsKey(declClass);
	}

	/**
	 * Resolve {@code declClass#fieldName} as a static-final JDK constant, gated on the declaring
	 * class being in the whitelist. Used by both {@code ObjectEvaluator} (for SGET inside the
	 * caller's IR) and {@code PureFold} (for SGET inside a snapshotted helper body). Returns the
	 * actual constant value (e.g. {@code int.class} for {@code Integer.TYPE},
	 * {@code Charset} instance for {@code StandardCharsets.UTF_8}) or {@code null} if the class
	 * isn't whitelisted, the field isn't static-final, or reflection failed.
	 */
	@Nullable
	public Object resolveStaticFinal(String declClass, String fieldName) {
		if (!handles(declClass)) {
			return null;
		}
		try {
			Class<?> cls = Class.forName(declClass, false, ClassLoader.getSystemClassLoader());
			java.lang.reflect.Field f = cls.getField(fieldName);
			int mods = f.getModifiers();
			if (!java.lang.reflect.Modifier.isStatic(mods) || !java.lang.reflect.Modifier.isFinal(mods)) {
				return null;
			}
			f.setAccessible(true);
			return f.get(null);
		} catch (Throwable t) {
			return null;
		}
	}
}
