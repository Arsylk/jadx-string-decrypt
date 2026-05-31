package jadx.plugins.stringdecrypt.jdk;

import java.lang.reflect.AccessibleObject;
import java.util.Set;

/**
 * {@link AccessibleObject}: the superclass of {@code Method}/{@code Field}/{@code Constructor}.
 * Obfuscator chains always call {@code setAccessible(true)} immediately after lookup; we treat that
 * as a no-op (we use {@code setAccessible(true)} ourselves whenever we invoke reflectively, so the
 * effective state is identical). Pure metadata methods are passed through.
 */
public final class AccessibleObjectHandler extends ReflectiveJdkHandler {

	public AccessibleObjectHandler() {
		super("java.lang.reflect.AccessibleObject");
	}

	@Override
	protected void registerMethods(Set<String> allow) {
		allow.add("setAccessible");
		allow.add("isAccessible");
		allow.add("trySetAccessible");
		allow.add("canAccess");
		allow.add("getAnnotation");
		allow.add("getAnnotations");
		allow.add("getDeclaredAnnotation");
		allow.add("getDeclaredAnnotations");
		allow.add("isAnnotationPresent");
	}
}
