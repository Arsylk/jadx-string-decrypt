package jadx.plugins.stringdecrypt.jdk;

import java.util.Collections;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import jadx.core.dex.info.MethodInfo;

/**
 * {@link java.nio.charset.StandardCharsets} — has no methods, only static-final {@link
 * java.nio.charset.Charset} fields. Registered purely so {@code SGET} on
 * {@code StandardCharsets.UTF_8} (and friends) passes the "is this class whitelisted?" gate
 * inside {@link JdkInterpreter#resolveStaticFinal}.
 */
public final class StandardCharsetsHandler implements JdkClassHandler {

	@Override
	public String targetClass() {
		return "java.nio.charset.StandardCharsets";
	}

	@Override
	public @Nullable Object invoke(MethodInfo call, @Nullable Object instance, Object[] args) {
		return null; // no methods on this class; SGET handles the static-final fields directly
	}

	/** Allow-set is empty — SGET handles the constants. */
	public Set<String> allowed() {
		return Collections.emptySet();
	}
}
