package jadx.plugins.stringdecrypt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jadx.api.plugins.pass.JadxPassInfo;
import jadx.api.plugins.pass.impl.OrderedJadxPassInfo;
import jadx.api.plugins.pass.types.JadxPreparePass;
import jadx.core.dex.nodes.ClassNode;
import jadx.core.dex.nodes.RootNode;

/**
 * Reconstructs every static {@code long[]} key table from each class's {@code <clinit>} before
 * decompilation, so the decompile pass can fold the opaque {@code (int)KEY[k]^c} constants. Running
 * here (pre-decompile, single pass over raw instructions) means the tables are complete and
 * read-only by the time the threaded decompile pass runs.
 */
public class KeyTablesPass implements JadxPreparePass {

	private static final Logger LOG = LoggerFactory.getLogger(KeyTablesPass.class);

	private final StringDecryptOptions options;
	private final KeyData keys;

	public KeyTablesPass(StringDecryptOptions options, KeyData keys) {
		this.options = options;
		this.keys = keys;
	}

	@Override
	public JadxPassInfo getInfo() {
		return new OrderedJadxPassInfo("StringDecryptKeyTables", "Reconstruct static key tables and constants");
	}

	@Override
	public void init(RootNode root) {
		if (!options.isEnabled()) {
			return;
		}
		keys.setMaxArraySize(options.getMaxTableSize());
		keys.setDecryptorDesc(options.getDecryptorDesc());
		boolean detectDecryptors = options.isDecryptStrings();
		for (ClassNode cls : root.getClasses()) {
			Eval.buildKeyData(cls, keys, detectDecryptors);
		}
		if (keys.size() != 0 || !keys.instanceStrings().isEmpty()) {
			Eval.scanMutableFields(root, keys);
		}
		LOG.info("string-decrypt: reconstructed {} constant(s) ({} arrays, {} scalars), detected {} decryptor(s),"
				+ " snapshotted {} pure helper body(ies), {} mutable static field(s) excluded from folding",
				keys.size(), keys.arrays().size(), keys.scalars().size(), keys.decryptors().size(),
				keys.bodies().size(), keys.mutableFields().size());
	}
}
