package jadx.plugins.stringdecrypt.jdk;

import java.util.Set;

public final class ArraysHandler extends ReflectiveJdkHandler {

	public ArraysHandler() {
		super("java.util.Arrays");
	}

	@Override
	protected void registerMethods(Set<String> allow) {
		allow.add("copyOf");
		allow.add("copyOfRange");
		allow.add("equals");
		allow.add("hashCode");
		allow.add("deepHashCode");
		allow.add("deepEquals");
		allow.add("toString");
		allow.add("deepToString");
		allow.add("asList");
		allow.add("stream");
	}
}
