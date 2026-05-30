package jadx.plugins.stringdecrypt;

import jadx.api.plugins.JadxPlugin;
import jadx.api.plugins.JadxPluginContext;
import jadx.api.plugins.JadxPluginInfo;

/**
 * Compile-time deobfuscator for Android apps that hide constants behind opaque, table-based
 * expressions (the {@code ((int) KEY[k]) ^ c} pattern emitted by several commercial obfuscators).
 *
 * <p>
 * Two passes:
 * <ul>
 * <li>{@link KeyTablesPass} (prepare) &mdash; before decompilation, reconstructs every static
 * integral "key table" / scalar constant from each class's {@code <clinit>}, snapshots pure helper
 * method bodies, and scans the whole program for runtime-mutated statics (so their reads are never
 * folded).</li>
 * <li>{@link StringDecryptPass} (decompile) &mdash; folds every compile-time-constant
 * numeric/boolean expression to its literal value, optionally decrypts resolvable block-cipher
 * string-decryptor calls, and removes the now-dead feeder instructions.</li>
 * </ul>
 *
 * <p>
 * Sound by construction: only genuinely compile-time values are folded (literals, arithmetic,
 * conversions, <i>immutable</i> static tables, pure helper calls), and a wrong string decryption is
 * filtered out by a printable-result check. All behaviour is controlled by
 * {@link StringDecryptOptions}; nothing is hard-coded to a particular app.
 */
public class StringDecryptPlugin implements JadxPlugin {

	public static final String PLUGIN_ID = "string-decrypt";

	/** Bump on any change that affects output or options (see AGENT.md). */
	public static final String VERSION = "1.0.1";

	/** Minimum jadx version this plugin is built/tested against (surfaced via {@link JadxPluginInfo}). */
	public static final String REQUIRED_JADX_VERSION = "1.5.2, r0";

	private static final String HOMEPAGE = "https://github.com/Arsylk/jadx-string-decrypt";

	private final StringDecryptOptions options = new StringDecryptOptions();

	/** compile-time constants reconstructed in prepare, read-only in decompile. */
	private final KeyData keys = new KeyData();

	@Override
	public JadxPluginInfo getPluginInfo() {
		JadxPluginInfo info = new JadxPluginInfo(PLUGIN_ID, "Constant Deobfuscator v" + VERSION,
				"Fold compile-time-constant (opaque table-based) numeric/boolean expressions to literals,"
						+ " and decrypt resolvable block-cipher string calls");
		info.setHomepage(HOMEPAGE);
		info.setRequiredJadxVersion(REQUIRED_JADX_VERSION);
		return info;
	}

	@Override
	public void init(JadxPluginContext context) {
		context.registerOptions(options);
		if (!options.isEnabled()) {
			return;
		}
		context.addPass(new KeyTablesPass(options, keys));
		context.addPass(new StringDecryptPass(options, keys));
	}
}
