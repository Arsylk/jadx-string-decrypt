package jadx.plugins.stringdecrypt.jdk;

import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Set;

import javax.crypto.Cipher;

import org.jetbrains.annotations.Nullable;

import jadx.core.dex.info.MethodInfo;

/**
 * Folds {@link javax.crypto.Cipher} decryption performed on constant inputs. A string obfuscator's
 * helper like {@code static String dec(String s)} hides a literal behind a symmetric cipher; once the
 * key, transformation and ciphertext are all compile-time constants the plaintext is constant too, so
 * we run the genuine JCE cipher on the host JDK (which ships these classes) and inline the result. The
 * same {@code Cipher} instance threads through the interpreter's registers across the
 * {@code getInstance → init → doFinal} chain, preserving the cipher's state.
 *
 * <p>
 * This is a <b>direct</b> {@link JdkClassHandler} rather than a {@link ReflectiveJdkHandler} for one
 * structural reason: {@code Cipher.init} is {@code void}, and a reflective void invoke returns real
 * {@code null}, which {@link jadx.plugins.stringdecrypt.PureFold} turns into its {@code NULL}
 * refuse-sentinel and aborts the entire fold. Here {@code init} returns the (now-initialised)
 * {@code Cipher} instance instead — harmless, because the {@code void} dex invoke has no
 * {@code move-result} to capture it — so interpretation continues. (This mirrors the existing
 * {@code setAccessible} no-op that also returns a non-sentinel to avoid aborting.)
 *
 * <p>
 * <b>Soundness.</b> Only {@code DECRYPT_MODE}/{@code UNWRAP_MODE} {@code init} is honoured — an
 * encrypt/wrap {@code init} with no explicit parameters can draw a random IV from {@code SecureRandom}
 * (non-deterministic), and encryption is not what a decryptor does. The transformation is checked
 * against a symmetric-cipher allow-list so we never trigger an unexpected provider. Any failure
 * (transformation unavailable on this host, bad padding from a wrong guess, missing IV) throws and is
 * swallowed → the call is simply left un-folded. Work is bounded: the ciphertext is a decoded constant
 * literal and {@link jadx.plugins.stringdecrypt.PureFold} caps total interpreted instructions.
 */
public final class CipherHandler implements JdkClassHandler {

	/** Base algorithms we are willing to run: symmetric block/stream ciphers only (no RSA/EC/etc.). */
	private static final Set<String> ALLOWED_ALGOS = Set.of(
			"AES", "AES_128", "AES_192", "AES_256",
			"DESEDE", "DESEDEWRAP", "TRIPLEDES", "DES",
			"BLOWFISH", "RC2", "RC4", "RC5", "ARCFOUR", "ARC4",
			"CHACHA20", "CHACHA20-POLY1305",
			"CAST5", "IDEA", "SEED", "ARIA", "CAMELLIA", "SM4");

	@Override
	public String targetClass() {
		return "javax.crypto.Cipher";
	}

	@Override
	public @Nullable Object invoke(MethodInfo call, @Nullable Object instance, Object[] args) {
		try {
			switch (call.getName()) {
				case "getInstance":
					return getInstance(args);
				case "init":
					return init(instance, args);
				case "doFinal":
					return doFinal(instance, args);
				default:
					return null; // anything else (update/wrap/unwrap/getIV/...) -> leave the call alone
			}
		} catch (Throwable t) {
			return null; // unavailable transformation, bad padding, wrong key, ... -> refuse the fold
		}
	}

	private static @Nullable Cipher getInstance(Object[] args) throws Exception {
		// Refuse the (String, Provider)/(String, String provider) overloads: pinning a provider is
		// unusual for an obfuscator and we only want the deterministic default-provider resolution.
		if (args.length != 1 || !(args[0] instanceof String)) {
			return null;
		}
		String transformation = (String) args[0];
		String base = transformation;
		int slash = base.indexOf('/');
		if (slash >= 0) {
			base = base.substring(0, slash);
		}
		if (!ALLOWED_ALGOS.contains(base.toUpperCase(java.util.Locale.ROOT))) {
			return null;
		}
		return Cipher.getInstance(transformation);
	}

	private static @Nullable Object init(@Nullable Object instance, Object[] args) throws Exception {
		if (!(instance instanceof Cipher) || args.length < 2 || !(args[0] instanceof Number) || !(args[1] instanceof Key)) {
			return null;
		}
		int opmode = ((Number) args[0]).intValue();
		if (opmode != Cipher.DECRYPT_MODE && opmode != Cipher.UNWRAP_MODE) {
			return null; // decrypt/unwrap only — encrypt/wrap may pull a random IV (non-deterministic)
		}
		Cipher cipher = (Cipher) instance;
		Key key = (Key) args[1];
		if (args.length == 2) {
			cipher.init(opmode, key);
		} else if (args[2] instanceof AlgorithmParameterSpec) {
			cipher.init(opmode, key, (AlgorithmParameterSpec) args[2]);
		} else if (args[2] instanceof AlgorithmParameters) {
			cipher.init(opmode, key, (AlgorithmParameters) args[2]);
		} else {
			return null;
		}
		// Return the initialised instance (non-null) so the void invoke does not trip PureFold's
		// NULL refuse-sentinel; the dex `init` has no move-result, so this value is just discarded.
		return cipher;
	}

	private static byte @Nullable [] doFinal(@Nullable Object instance, Object[] args) throws Exception {
		if (!(instance instanceof Cipher)) {
			return null;
		}
		Cipher cipher = (Cipher) instance;
		if (args.length == 1 && args[0] instanceof byte[]) {
			return cipher.doFinal((byte[]) args[0]);
		}
		if (args.length == 3 && args[0] instanceof byte[] && args[1] instanceof Number && args[2] instanceof Number) {
			return cipher.doFinal((byte[]) args[0], ((Number) args[1]).intValue(), ((Number) args[2]).intValue());
		}
		return null;
	}
}
