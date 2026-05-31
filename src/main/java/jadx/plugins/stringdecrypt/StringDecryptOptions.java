package jadx.plugins.stringdecrypt;

import jadx.api.plugins.options.OptionFlag;
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
	private boolean cleanupOrphanArrays = true;
	private boolean comments = true;
	private boolean foldReflectiveBridges = true;
	private boolean foldObjectReturningInvokes = true;
	private boolean suppressDecoyLookups = true;
	private String decryptorClass = "";
	private String decryptorDesc = DEFAULT_DECRYPTOR_DESC;
	private String cipher = DEFAULT_CIPHER;
	private int keyTailLen = DEFAULT_KEY_TAIL_LEN;
	private int maxTableSize = DEFAULT_MAX_TABLE_SIZE;

	@Override
	public void registerOptions() {
		boolOption(StringDecryptPlugin.PLUGIN_ID + ".enabled")
				.description("Enable")
				.defaultValue(true)
				.setter(v -> enabled = v);

		boolOption(StringDecryptPlugin.PLUGIN_ID + ".fold-consts")
				.description("Fold constants")
				.defaultValue(true)
				.setter(v -> foldConsts = v);

		boolOption(StringDecryptPlugin.PLUGIN_ID + ".fold-helper-calls")
				.description("Fold pure helper calls")
				.defaultValue(true)
				.setter(v -> foldHelperCalls = v);

		boolOption(StringDecryptPlugin.PLUGIN_ID + ".decrypt-strings")
				.description("Decrypt string calls")
				.defaultValue(true)
				.setter(v -> decryptStrings = v);

		boolOption(StringDecryptPlugin.PLUGIN_ID + ".cleanup")
				.description("Clean up dead instructions")
				.defaultValue(true)
				.setter(v -> cleanup = v);

		boolOption(StringDecryptPlugin.PLUGIN_ID + ".cleanup-orphan-arrays")
				.description("Clean up orphan byte[] statements")
				.defaultValue(true)
				.setter(v -> cleanupOrphanArrays = v);

		boolOption(StringDecryptPlugin.PLUGIN_ID + ".fold-reflective-bridges")
				.description("Fold reflective chains")
				.defaultValue(true)
				.setter(v -> foldReflectiveBridges = v);

		boolOption(StringDecryptPlugin.PLUGIN_ID + ".fold-object-invokes")
				.description("Fold Object-returning invokes")
				.defaultValue(true)
				.setter(v -> foldObjectReturningInvokes = v);

		boolOption(StringDecryptPlugin.PLUGIN_ID + ".suppress-decoy-lookups")
				.description("Skip non-printable reflective name folds")
				.defaultValue(true)
				.setter(v -> suppressDecoyLookups = v);

		boolOption(StringDecryptPlugin.PLUGIN_ID + ".comments")
				.description("Add decrypted-strings comment")
				.defaultValue(true)
				.setter(v -> comments = v);

		strOption(StringDecryptPlugin.PLUGIN_ID + ".decryptor-class")
				.description("Decryptor class (empty = auto)")
				.defaultValue("")
				.setter(v -> decryptorClass = v);

		strOption(StringDecryptPlugin.PLUGIN_ID + ".decryptor-desc")
				.description("Decryptor descriptor")
				.defaultValue(DEFAULT_DECRYPTOR_DESC)
				.setter(v -> decryptorDesc = v);

		strOption(StringDecryptPlugin.PLUGIN_ID + ".cipher")
				.description("Cipher transformation")
				.defaultValue(DEFAULT_CIPHER)
				.setter(v -> cipher = v);

		intOption(StringDecryptPlugin.PLUGIN_ID + ".key-tail-len")
				.description("Appended key length")
				.defaultValue(DEFAULT_KEY_TAIL_LEN)
				.setter(v -> keyTailLen = v);

		intOption(StringDecryptPlugin.PLUGIN_ID + ".max-table-size")
				.description("Max array length")
				.defaultValue(DEFAULT_MAX_TABLE_SIZE)
				.setter(v -> maxTableSize = v);

		// Read-only "About" entries — provenance for the loaded jar; NOT_CHANGING_CODE keeps the
		// code cache from invalidating when these display values change between rebuilds.
		strOption(StringDecryptPlugin.PLUGIN_ID + ".version")
				.description("Version")
				.defaultValue(BuildInfo.VERSION)
				.flags(OptionFlag.DISABLE_IN_GUI, OptionFlag.NOT_CHANGING_CODE)
				.setter(v -> { /* read-only display */ });

		strOption(StringDecryptPlugin.PLUGIN_ID + ".build-time")
				.description("Built")
				.defaultValue(BuildInfo.BUILD_TIME)
				.flags(OptionFlag.DISABLE_IN_GUI, OptionFlag.NOT_CHANGING_CODE)
				.setter(v -> { /* read-only display */ });
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

	public boolean isCleanupOrphanArrays() {
		return cleanupOrphanArrays;
	}

	public boolean isFoldReflectiveBridges() {
		return foldReflectiveBridges;
	}

	public boolean isFoldObjectReturningInvokes() {
		return foldObjectReturningInvokes;
	}

	public boolean isSuppressDecoyLookups() {
		return suppressDecoyLookups;
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
