package jadx.plugins.stringdecrypt.jdk;

import org.jetbrains.annotations.Nullable;

import jadx.core.dex.info.MethodInfo;

/**
 * Handler for a single JDK class. Knows how to interpret a small set of pure methods on that class
 * (and its constructors) and refuse anything else.
 *
 * <p>
 * The interpreter ({@link JdkInterpreter}) dispatches an incoming call to the handler whose
 * {@link #targetClass()} matches the call's declaring class. Adding support for a new class is
 * implementing this interface and registering an instance — no changes to the dispatcher.
 */
public interface JdkClassHandler {

	/** Fully-qualified declaring class name this handler is responsible for. */
	String targetClass();

	/**
	 * Invoke the call described by {@code call} with the given {@code instance} (null for static /
	 * constructor) and constant {@code args}. Return the computed value (which may itself be a
	 * String, byte[], char[], BigInteger, etc.) — or {@code null} to refuse, leaving the call
	 * unchanged in the output.
	 *
	 * <p>
	 * Handlers must be deterministic and side-effect-free: refuse anything that depends on time, the
	 * default locale, or any shared mutable state.
	 */
	@Nullable
	Object invoke(MethodInfo call, @Nullable Object instance, Object[] args);
}
