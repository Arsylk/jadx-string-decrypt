package jadx.plugins.stringdecrypt.jdk;

import java.util.Set;

public final class CharsetHandler extends ReflectiveJdkHandler {

	public CharsetHandler() {
		super("java.nio.charset.Charset");
	}

	@Override
	protected void registerMethods(Set<String> allow) {
		allow.add("forName");
		// NOTE: Charset.defaultCharset() is deliberately NOT folded — it returns the *host* JVM's
		// default charset (environment-dependent), so its value would differ from the target device.
		allow.add("name");
		allow.add("displayName");
		allow.add("aliases");
		allow.add("canEncode");
		allow.add("contains");
		allow.add("equals");
		allow.add("hashCode");
		allow.add("toString");
	}
}
