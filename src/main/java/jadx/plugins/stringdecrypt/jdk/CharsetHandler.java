package jadx.plugins.stringdecrypt.jdk;

import java.util.Set;

public final class CharsetHandler extends ReflectiveJdkHandler {

	public CharsetHandler() {
		super("java.nio.charset.Charset");
	}

	@Override
	protected void registerMethods(Set<String> allow) {
		allow.add("forName");
		allow.add("defaultCharset");
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
