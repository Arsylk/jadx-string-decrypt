package jadx.plugins.stringdecrypt.jdk;

import java.util.Set;

/**
 * {@link StringBuilder} / {@link StringBuffer}: pure as long as the caller owns the instance.
 * Mutators are allowed because the folder operates on a fresh instance the constructor returned —
 * folding {@code new StringBuilder("X").reverse().toString()} requires the reverse() mutator.
 */
public final class StringBuilderHandler extends ReflectiveJdkHandler {

	public StringBuilderHandler(String className) {
		super(className);
	}

	@Override
	protected void registerMethods(Set<String> allow) {
		allow.add("append");
		allow.add("appendCodePoint");
		allow.add("reverse");
		allow.add("toString");
		allow.add("length");
		allow.add("capacity");
		allow.add("charAt");
		allow.add("codePointAt");
		allow.add("indexOf");
		allow.add("lastIndexOf");
		allow.add("substring");
		allow.add("subSequence");
		allow.add("getChars");
		allow.add("insert");
		allow.add("delete");
		allow.add("deleteCharAt");
		allow.add("replace");
		allow.add("setCharAt");
		allow.add("setLength");
		allow.add("trimToSize");
		allow.add("compareTo");
		allow.add("equals");
		allow.add("hashCode");
		allow.add("chars");
		allow.add("codePoints");
	}
}
