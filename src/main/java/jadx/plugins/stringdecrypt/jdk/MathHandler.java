package jadx.plugins.stringdecrypt.jdk;

import java.util.Set;

/**
 * Pure {@link Math} operations. Excludes {@code random()} (PRNG state) and Java 11+
 * {@code nextDown}/{@code nextUp} sign-sensitivities only if the result is downstream — for
 * correctness we just include the deterministic surface.
 */
public final class MathHandler extends ReflectiveJdkHandler {

	public MathHandler() {
		super("java.lang.Math");
	}

	@Override
	protected void registerMethods(Set<String> allow) {
		allow.add("abs");
		allow.add("ceil");
		allow.add("floor");
		allow.add("round");
		allow.add("rint");
		allow.add("max");
		allow.add("min");
		allow.add("pow");
		allow.add("sqrt");
		allow.add("cbrt");
		allow.add("log");
		allow.add("log10");
		allow.add("log1p");
		allow.add("exp");
		allow.add("expm1");
		allow.add("sin");
		allow.add("cos");
		allow.add("tan");
		allow.add("asin");
		allow.add("acos");
		allow.add("atan");
		allow.add("atan2");
		allow.add("sinh");
		allow.add("cosh");
		allow.add("tanh");
		allow.add("hypot");
		allow.add("signum");
		allow.add("addExact");
		allow.add("subtractExact");
		allow.add("multiplyExact");
		allow.add("negateExact");
		allow.add("floorDiv");
		allow.add("floorMod");
		allow.add("scalb");
		allow.add("copySign");
		allow.add("ulp");
		allow.add("getExponent");
		allow.add("IEEEremainder");
		allow.add("toRadians");
		allow.add("toDegrees");
		allow.add("nextUp");
		allow.add("nextDown");
		allow.add("nextAfter");
		allow.add("incrementExact");
		allow.add("decrementExact");
		allow.add("toIntExact");
		allow.add("multiplyHigh");
		allow.add("multiplyFull");
	}
}
