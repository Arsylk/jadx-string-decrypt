package jadx.plugins.stringdecrypt.jdk;

import java.util.Set;

public final class CharacterHandler extends ReflectiveJdkHandler {

	public CharacterHandler() {
		super("java.lang.Character");
	}

	@Override
	protected void registerMethods(Set<String> allow) {
		allow.add("valueOf");
		allow.add("charValue");
		allow.add("toString");
		allow.add("isDigit");
		allow.add("isLetter");
		allow.add("isLetterOrDigit");
		allow.add("isWhitespace");
		allow.add("isSpaceChar");
		allow.add("isUpperCase");
		allow.add("isLowerCase");
		allow.add("isTitleCase");
		allow.add("isAlphabetic");
		allow.add("isISOControl");
		allow.add("isHighSurrogate");
		allow.add("isLowSurrogate");
		allow.add("isSurrogatePair");
		allow.add("isValidCodePoint");
		allow.add("isSupplementaryCodePoint");
		allow.add("toUpperCase");
		allow.add("toLowerCase");
		allow.add("toTitleCase");
		allow.add("getNumericValue");
		allow.add("digit");
		allow.add("forDigit");
		allow.add("codePointAt");
		allow.add("toCodePoint");
		allow.add("getType");
		allow.add("getDirectionality");
		allow.add("equals");
		allow.add("hashCode");
		allow.add("compareTo");
		allow.add("compare");
		allow.add("reverseBytes");
	}
}
