package jadx.plugins.stringdecrypt.jdk;

import java.util.Set;

/**
 * Folds construction of the JCE key/parameter spec value objects an obfuscated decryptor builds from
 * constant bytes — {@link javax.crypto.spec.SecretKeySpec},
 * {@link javax.crypto.spec.IvParameterSpec} and {@link javax.crypto.spec.GCMParameterSpec}. These are
 * plain immutable carriers available on the host JDK, so {@link ReflectiveJdkHandler} builds the real
 * instance through their public constructors (e.g. {@code new SecretKeySpec(byte[], "DESede")},
 * {@code new IvParameterSpec(byte[])}); the result threads straight into {@link CipherHandler}'s
 * {@code init}. Only the constructors are needed — no instance methods are whitelisted (an empty
 * allow-list; {@code <init>} is always permitted by the base handler).
 */
public final class CryptoSpecHandler extends ReflectiveJdkHandler {

	public CryptoSpecHandler(String targetClass) {
		super(targetClass);
	}

	@Override
	protected void registerMethods(Set<String> allow) {
		// constructors only — SecretKeySpec/IvParameterSpec/GCMParameterSpec are pure value carriers
		// whose getEncoded()/getIV() are rarely on the fold path; <init> is always allowed.
	}
}
