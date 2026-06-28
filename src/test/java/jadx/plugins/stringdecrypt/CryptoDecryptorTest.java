package jadx.plugins.stringdecrypt;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end regression for the JCE-cipher / Base64 decryptor fold (the {@code com.telegram…Jbaz.a}
 * family). The plugin interprets a pure {@code static String dec(String)} helper that composes
 * {@code Base64.decode → new SecretKeySpec → Cipher.getInstance/init/doFinal → new String(...)}
 * entirely from the constants in its own body, then inlines the plaintext at every constant-argument
 * call site — with no per-app key configuration.
 *
 * <p>
 * Fixture {@code crypto-decryptor.dex} (kept alongside its sources under
 * {@code test-samples/crypto-decryptor-src/}) defines:
 * <ul>
 * <li>{@code Dec.a(String)} — DESede/ECB + {@code android.util.Base64} (the exact Jbaz.a shape;
 * {@code android.util.Base64} is re-implemented by {@link jadx.plugins.stringdecrypt.jdk.AndroidBase64Handler}
 * because it is not on the host JDK).</li>
 * <li>{@code Dec.b(String)} — AES/CBC/PKCS5 + {@code java.util.Base64} + an {@code IvParameterSpec}
 * (exercises {@link jadx.plugins.stringdecrypt.jdk.JavaUtilBase64Handler}, the IV spec ctor, and a
 * mode that threads an {@code AlgorithmParameterSpec} through {@code Cipher.init}).</li>
 * </ul>
 * Regenerate with: {@code javac --release 11 -cp android.jar} the two sources, then
 * {@code d8 --min-api 21} the resulting class files.
 */
class CryptoDecryptorTest extends RealApkDeobfTestBase {

	@Test
	void cipherHelpers_foldToPlaintextAtCallSites() {
		String code = decompile("crypto-decryptor.dex", "crypto.fixture.Use");

		// DESede + android.util.Base64
		assertThat(code).contains("\"DECRYPTED_BY_PLUGIN\"");
		assertThat(code).contains("\"android.intent.action.VIEW\"");
		// AES/CBC + java.util.Base64 + IvParameterSpec
		assertThat(code).contains("\"aes-cbc secret value\"");

		// the opaque decryptor calls are gone — every site was inlined to its constant
		assertThat(code)
				.as("decryptor calls must be folded away, not left in source")
				.doesNotContain("Dec.a(")
				.doesNotContain("Dec.b(");
	}
}
