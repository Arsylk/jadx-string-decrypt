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

		boolOption(StringDecryptPlugin.PLUGIN_ID + ".cleanup-orphan-arrays")
				.description("Also sweep orphan `new byte[]{...}[N] = X;` expression statements that are"
						+ " written-only after folding consumed their contents. Subsumed by cleanup.")
				.defaultValue(true)
				.setter(v -> cleanupOrphanArrays = v);

		boolOption(StringDecryptPlugin.PLUGIN_ID + ".fold-reflective-bridges")
				.description("Fold reflective chains (Class.forName(s).getMethod(...).invoke(...),"
						+ " Constructor.newInstance, Field.get) back to direct values when every link"
						+ " resolves to a JDK-whitelisted method. Turn off to keep all reflection in source.")
				.defaultValue(true)
				.setter(v -> foldReflectiveBridges = v);

		boolOption(StringDecryptPlugin.PLUGIN_ID + ".fold-object-invokes")
				.description("Also attempt to fold Object/CharSequence-returning invokes (lets us recover"
						+ " a String value from a reflective Method.invoke even when the IR drops the"
						+ " (String) cast). Off = only fold INVOKEs whose declared return is String.")
				.defaultValue(true)
				.setter(v -> foldObjectReturningInvokes = v);

		boolOption(StringDecryptPlugin.PLUGIN_ID + ".suppress-decoy-lookups")
				.description("When the folded String flows into Class.forName / getMethod / getField /"
						+ " getConstructor, refuse to emit a non-printable literal (the obfuscator's"
						+ " decoy chains compute to garbage that would never resolve). Off = always fold.")
				.defaultValue(true)
				.setter(v -> suppressDecoyLookups = v);

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

		// Read-only "About" entries — show provenance in the GUI settings panel so you can tell
		// exactly which build is loaded without leaving jadx. NOT_CHANGING_CODE keeps the code
		// cache from invalidating when these display values change between rebuilds.
		strOption(StringDecryptPlugin.PLUGIN_ID + ".version")
				.description("Plugin version (read-only)")
				.defaultValue(BuildInfo.VERSION)
				.flags(OptionFlag.DISABLE_IN_GUI, OptionFlag.NOT_CHANGING_CODE)
				.setter(v -> { /* read-only display */ });

		strOption(StringDecryptPlugin.PLUGIN_ID + ".build-time")
				.description("UTC timestamp when this plugin jar was built (read-only)")
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
