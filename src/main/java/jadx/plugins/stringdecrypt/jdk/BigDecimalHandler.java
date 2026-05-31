package jadx.plugins.stringdecrypt.jdk;

import java.util.Set;

public final class BigDecimalHandler extends ReflectiveJdkHandler {

	public BigDecimalHandler() {
		super("java.math.BigDecimal");
	}

	@Override
	protected void registerMethods(Set<String> allow) {
		allow.add("valueOf");
		allow.add("toString");
		allow.add("toPlainString");
		allow.add("toEngineeringString");
		allow.add("intValue");
		allow.add("longValue");
		allow.add("shortValue");
		allow.add("byteValue");
		allow.add("intValueExact");
		allow.add("longValueExact");
		allow.add("doubleValue");
		allow.add("floatValue");
		allow.add("toBigInteger");
		allow.add("toBigIntegerExact");
		allow.add("scale");
		allow.add("precision");
		allow.add("unscaledValue");
		allow.add("signum");
		allow.add("abs");
		allow.add("negate");
		allow.add("plus");
		allow.add("add");
		allow.add("subtract");
		allow.add("multiply");
		allow.add("divide");
		allow.add("divideToIntegralValue");
		allow.add("remainder");
		allow.add("pow");
		allow.add("min");
		allow.add("max");
		allow.add("compareTo");
		allow.add("equals");
		allow.add("hashCode");
		allow.add("setScale");
		allow.add("movePointLeft");
		allow.add("movePointRight");
		allow.add("stripTrailingZeros");
		allow.add("ulp");
	}
}
