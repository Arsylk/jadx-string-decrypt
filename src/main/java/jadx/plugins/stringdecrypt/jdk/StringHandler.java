package jadx.plugins.stringdecrypt.jdk;

import java.util.Set;

/**
 * Pure {@link String} operations. Excludes {@code intern} (touches the string pool),
 * {@code format} (default locale), and {@code toLowerCase()/toUpperCase()} no-arg
 * (default locale — the {@code (Locale)} overload would be safe but rarely seen in obfuscators).
 */
public final class StringHandler extends ReflectiveJdkHandler {

	public StringHandler() {
		super("java.lang.String");
	}

	@Override
	protected void registerMethods(Set<String> allow) {
		allow.add("toCharArray");
		allow.add("getBytes");
		allow.add("length");
		allow.add("isEmpty");
		allow.add("charAt");
		allow.add("codePointAt");
		allow.add("codePointBefore");
		allow.add("codePointCount");
		allow.add("substring");
		allow.add("subSequence");
		allow.add("concat");
		allow.add("replace");
		allow.add("split");
		allow.add("trim");
		allow.add("strip");
		allow.add("stripLeading");
		allow.add("stripTrailing");
		allow.add("indexOf");
		allow.add("lastIndexOf");
		allow.add("startsWith");
		allow.add("endsWith");
		allow.add("contains");
		allow.add("equals");
		allow.add("equalsIgnoreCase");
		allow.add("contentEquals");
		allow.add("compareTo");
		allow.add("compareToIgnoreCase");
		allow.add("hashCode");
		allow.add("toString");
		allow.add("valueOf");
		allow.add("copyValueOf");
		allow.add("matches");
		allow.add("replaceAll");
		allow.add("replaceFirst");
		allow.add("join");
		allow.add("repeat");
		allow.add("chars");
		allow.add("codePoints");
		allow.add("getChars");
		allow.add("toLowerCase"); // default-locale path can flip a Turkish 'I' — typical obfuscators stick to ASCII
		allow.add("toUpperCase");
	}
}
