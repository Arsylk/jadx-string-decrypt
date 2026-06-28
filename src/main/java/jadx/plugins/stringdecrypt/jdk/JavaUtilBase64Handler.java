package jadx.plugins.stringdecrypt.jdk;

import java.util.Set;

/**
 * Folds {@link java.util.Base64} usage in obfuscated decryptors — the standard-library counterpart to
 * {@link AndroidBase64Handler}. The factory ({@code Base64.getDecoder()}, {@code getUrlDecoder()},
 * {@code getMimeDecoder()}, and the encoder variants) returns the JDK singleton {@code Decoder} /
 * {@code Encoder}, which {@link Base64DecoderHandler} / {@link Base64EncoderHandler} then drive
 * reflectively. All three classes are on the host JDK and stateless, so the fold is exact and
 * deterministic.
 *
 * <p>
 * The nested {@code Decoder}/{@code Encoder} need the dotted dispatch name jadx reports
 * ({@code java.util.Base64.Decoder}) paired with the binary {@code $} name {@link Class#forName}
 * wants ({@code java.util.Base64$Decoder}) — see the two-arg {@link ReflectiveJdkHandler} constructor.
 */
public final class JavaUtilBase64Handler extends ReflectiveJdkHandler {

	public JavaUtilBase64Handler() {
		super("java.util.Base64");
	}

	@Override
	protected void registerMethods(Set<String> allow) {
		allow.add("getDecoder");
		allow.add("getUrlDecoder");
		allow.add("getMimeDecoder");
		allow.add("getEncoder");
		allow.add("getUrlEncoder");
		allow.add("getMimeEncoder");
	}
}

/** {@code java.util.Base64.Decoder.decode(...)} → folded constant byte[]. */
final class Base64DecoderHandler extends ReflectiveJdkHandler {

	Base64DecoderHandler() {
		super("java.util.Base64.Decoder", "java.util.Base64$Decoder");
	}

	@Override
	protected void registerMethods(Set<String> allow) {
		allow.add("decode");
	}
}

/** {@code java.util.Base64.Encoder.encode/encodeToString/withoutPadding(...)} → folded constant. */
final class Base64EncoderHandler extends ReflectiveJdkHandler {

	Base64EncoderHandler() {
		super("java.util.Base64.Encoder", "java.util.Base64$Encoder");
	}

	@Override
	protected void registerMethods(Set<String> allow) {
		allow.add("encode");
		allow.add("encodeToString");
		allow.add("withoutPadding");
	}
}
