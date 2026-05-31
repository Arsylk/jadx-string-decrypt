package jadx.plugins.stringdecrypt.jdk;

import java.util.Set;

/**
 * {@link Object}-declared methods (virtual dispatch). Calls like {@code myStringBuilder.toString()}
 * are encoded by the compiler as {@code invoke-virtual Ljava/lang/Object;.toString}, so the
 * dispatcher sees {@code declClass = java.lang.Object} regardless of the runtime receiver type.
 * Reflection dispatches virtually for us — invoking {@code Object.class.getMethod("toString")} on a
 * {@code StringBuilder} instance correctly calls {@code StringBuilder.toString()}.
 *
 * <p>
 * {@code wait}/{@code notify}/{@code notifyAll} are deliberately omitted (they need monitor state
 * and have no pure semantics here).
 */
public final class ObjectHandler extends ReflectiveJdkHandler {

	public ObjectHandler() {
		super("java.lang.Object");
	}

	@Override
	protected void registerMethods(Set<String> allow) {
		allow.add("toString");
		allow.add("hashCode");
		allow.add("equals");
		allow.add("getClass");
	}
}
