package jadx.plugins.stringdecrypt.jdk;

import java.util.Set;

/**
 * Pure {@link java.math.BigInteger} operations the folder is allowed to evaluate. Excludes
 * non-deterministic generators ({@code probablePrime}, {@code nextProbablePrime}) and anything that
 * touches a {@code Random}.
 */
public final class BigIntegerHandler extends ReflectiveJdkHandler {

	public BigIntegerHandler() {
		super("java.math.BigInteger");
	}

	@Override
	protected void registerMethods(Set<String> allow) {
		allow.add("valueOf");
		allow.add("toByteArray");
		allow.add("toString");
		allow.add("intValue");
		allow.add("longValue");
		allow.add("shortValue");
		allow.add("byteValue");
		allow.add("intValueExact");
		allow.add("longValueExact");
		allow.add("shortValueExact");
		allow.add("byteValueExact");
		allow.add("doubleValue");
		allow.add("floatValue");
		allow.add("add");
		allow.add("subtract");
		allow.add("multiply");
		allow.add("divide");
		allow.add("divideAndRemainder");
		allow.add("remainder");
		allow.add("mod");
		allow.add("modPow");
		allow.add("modInverse");
		allow.add("pow");
		allow.add("gcd");
		allow.add("abs");
		allow.add("negate");
		allow.add("signum");
		allow.add("shiftLeft");
		allow.add("shiftRight");
		allow.add("and");
		allow.add("or");
		allow.add("xor");
		allow.add("not");
		allow.add("andNot");
		allow.add("bitCount");
		allow.add("bitLength");
		allow.add("getLowestSetBit");
		allow.add("setBit");
		allow.add("clearBit");
		allow.add("flipBit");
		allow.add("testBit");
		allow.add("min");
		allow.add("max");
		allow.add("compareTo");
		allow.add("equals");
		allow.add("hashCode");
		allow.add("sqrt");
	}
}
