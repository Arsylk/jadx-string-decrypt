package jadx.plugins.stringdecrypt;

import jadx.api.plugins.options.impl.BasePluginOptionsBuilder;

/**
 * User-facing settings. All defaults aim at <b>general</b> APK deobfuscation (the constant folding
 * is on by default); the string-decryption parameters describe a common "key appended to the
 * ciphertext, ECB block cipher" scheme and can be retargeted without code changes.
 *
 * <p>
 * NOTE: defaults are passed to the builders as literals, not via these fields — {@code
 * registerOptions()} runs from the super constructor, before these field initializers, so the
 * fields are still null/0 at that point.
 */
public class StringDecryptOptions extends BasePluginOptionsBuilder {

	static final String DEFAULT_DECRYPTOR_DESC = "([B)Ljava/lang/String;"; // one byte[] -> String
	static final String DEFAULT_CIPHER = "AES/ECB/PKCS5Padding";
	static final int DEFAULT_KEY_TAIL_LEN = 16;
	static final int DEFAULT_MAX_TABLE_SIZE = 4_000_000;

	private boolean enabled = true;
	private boolean foldConsts = true;
	private boolean foldHelperCalls = true;
	private boolean decryptStrings = true;
	private boolean cleanup = true;
	private boolean comments = true;
	private String decryptorClass = "";
	private String decryptorDesc = DEFAULT_DECRYPTOR_DESC;
	private String cipher = DEFAULT_CIPHER;
	private int keyTailLen = DEFAULT_KEY_TAIL_LEN;
	private int maxTableSize = DEFAULT_MAX_TABLE_SIZE;

	@Override
	public void registerOptions() {
		boolOption(StringDecryptPlugin.PLUGIN_ID + ".enabled")
				.description("Master switch for the deobfuscator")
				.defaultValue(true)
				.setter(v -> enabled = v);

		boolOption(StringDecryptPlugin.PLUGIN_ID + ".fold-consts")
				.description("Replace compile-time-constant numeric/boolean expressions (e.g. the opaque"
						+ " table-based `((int) KEY[k]) ^ c`) with their literal value everywhere")
				.defaultValue(true)
				.setter(v -> foldConsts = v);

		boolOption(StringDecryptPlugin.PLUGIN_ID + ".fold-helper-calls")
				.description("Also fold calls to pure, fully-interpretable helper methods with constant"
						+ " args (interprocedural constant folding)")
				.defaultValue(true)
				.setter(v -> foldHelperCalls = v);

		boolOption(StringDecryptPlugin.PLUGIN_ID + ".decrypt-strings")
				.description("Decrypt resolvable block-cipher string-decryptor calls (auto-detected) whose"
						+ " byte[] argument is a compile-time constant")
				.defaultValue(true)
				.setter(v -> decryptStrings = v);

		boolOption(StringDecryptPlugin.PLUGIN_ID + ".cleanup")
				.description("Remove instructions left dead after folding/decryption (table reads, the"
						+ " consumed byte-array build, folded-away arithmetic)")
				.defaultValue(true)
				.setter(v -> cleanup = v);

		boolOption(StringDecryptPlugin.PLUGIN_ID + ".comments")
				.description("Add a method comment listing the strings decrypted in it")
				.defaultValue(true)
				.setter(v -> comments = v);

		strOption(StringDecryptPlugin.PLUGIN_ID + ".decryptor-class")
				.description("Restrict the string decryptor to this raw class name; empty = rely on"
						+ " auto-detection only (any static String(byte[]) calling Cipher.doFinal)")
				.defaultValue("")
				.setter(v -> decryptorClass = v);

		strOption(StringDecryptPlugin.PLUGIN_ID + ".decryptor-desc")
				.description("Method descriptor used to recognise a string decryptor (args + return)")
				.defaultValue(DEFAULT_DECRYPTOR_DESC)
				.setter(v -> decryptorDesc = v);

		strOption(StringDecryptPlugin.PLUGIN_ID + ".cipher")
				.description("JCE transformation used to decrypt strings (the key is the trailing"
						+ " key-tail-len bytes of each blob, the rest is the ciphertext)")
				.defaultValue(DEFAULT_CIPHER)
				.setter(v -> cipher = v);

		intOption(StringDecryptPlugin.PLUGIN_ID + ".key-tail-len")
				.description("Number of key bytes appended at the end of each ciphertext blob")
				.defaultValue(DEFAULT_KEY_TAIL_LEN)
				.setter(v -> keyTailLen = v);

		intOption(StringDecryptPlugin.PLUGIN_ID + ".max-table-size")
				.description("Maximum reconstructed static-array length (guards against hostile/huge sizes)")
				.defaultValue(DEFAULT_MAX_TABLE_SIZE)
				.setter(v -> maxTableSize = v);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isFoldConsts() {
		return foldConsts;
	}

	public boolean isFoldHelperCalls() {
		return foldHelperCalls;
	}

	public boolean isDecryptStrings() {
		return decryptStrings;
	}

	public boolean isCleanup() {
		return cleanup;
	}

	public boolean isComments() {
		return comments;
	}

	public String getDecryptorClass() {
		return decryptorClass;
	}

	public String getDecryptorDesc() {
		return decryptorDesc;
	}

	public String getCipher() {
		return cipher;
	}

	public int getKeyTailLen() {
		return keyTailLen;
	}

	public int getMaxTableSize() {
		return maxTableSize;
	}
}
