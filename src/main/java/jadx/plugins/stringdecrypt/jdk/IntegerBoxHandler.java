package jadx.plugins.stringdecrypt.jdk;

import java.util.Set;

/**
 * Boxed numeric / boolean classes: pure factory / formatting / parsing methods. The same allow-list
 * is reused across {@link Integer}/{@link Long}/{@link Short}/{@link Byte}/{@link Boolean} because
 * the surface is nearly identical (a method that doesn't exist on a given class simply fails the
 * reflective lookup and is refused).
 */
public final class IntegerBoxHandler extends ReflectiveJdkHandler {

	public IntegerBoxHandler(String targetClass) {
		super(targetClass);
	}

	@Override
	protected void registerMethods(Set<String> allow) {
		allow.add("valueOf");
		allow.add("parseInt");
		allow.add("parseLong");
		allow.add("parseShort");
		allow.add("parseByte");
		allow.add("parseBoolean");
		allow.add("parseUnsignedInt");
		allow.add("parseUnsignedLong");
		allow.add("toString");
		allow.add("toUnsignedString");
		allow.add("toBinaryString");
		allow.add("toHexString");
		allow.add("toOctalString");
		allow.add("intValue");
		allow.add("longValue");
		allow.add("shortValue");
		allow.add("byteValue");
		allow.add("doubleValue");
		allow.add("floatValue");
		allow.add("booleanValue");
		allow.add("equals");
		allow.add("hashCode");
		allow.add("compareTo");
		allow.add("compare");
		allow.add("compareUnsigned");
		allow.add("min");
		allow.add("max");
		allow.add("sum");
		allow.add("signum");
		allow.add("bitCount");
		allow.add("reverse");
		allow.add("reverseBytes");
		allow.add("numberOfLeadingZeros");
		allow.add("numberOfTrailingZeros");
		allow.add("highestOneBit");
		allow.add("lowestOneBit");
		allow.add("rotateLeft");
		allow.add("rotateRight");
		allow.add("getNumericValue");
		allow.add("decode");
		allow.add("divideUnsigned");
		allow.add("remainderUnsigned");
		allow.add("logicalAnd");
		allow.add("logicalOr");
		allow.add("logicalXor");
	}
}
