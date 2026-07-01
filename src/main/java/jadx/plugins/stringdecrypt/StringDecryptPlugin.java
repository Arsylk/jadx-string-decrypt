package jadx.plugins.stringdecrypt;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jadx.api.plugins.JadxPlugin;
import jadx.api.plugins.JadxPluginContext;
import jadx.api.plugins.JadxPluginInfo;
import jadx.api.plugins.gui.JadxGuiContext;

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
 * <li>{@link StringDecryptPass} (decompile) &mdash; folds every representable compile-time-constant
 * value to its literal IR form, optionally decrypts resolvable block-cipher string-decryptor calls,
 * and removes the now-dead feeder instructions.</li>
 * </ul>
 *
 * <p>
 * Sound by construction: only genuinely compile-time values are folded (literals, arithmetic,
 * conversions, <i>immutable</i> static tables, pure helper calls), and a wrong string decryption is
 * filtered out by a printable-result check. All behaviour is controlled by
 * {@link StringDecryptOptions}; nothing is hard-coded to a particular app.
 */
public class StringDecryptPlugin implements JadxPlugin {

	private static final Logger LOG = LoggerFactory.getLogger(StringDecryptPlugin.class);

	public static final String PLUGIN_ID = "string-decrypt";

	/**
	 * Bump on any change that affects output or options (see AGENT.md). Kept in sync with
	 * {@link BuildInfo#VERSION} (which gradle writes at build time from {@code project.version});
	 * the two values must match — if they ever drift it means the source and the jar are out of sync.
	 */
	public static final String VERSION = "1.12.1";

	/** Minimum jadx version this plugin is built/tested against (surfaced via {@link JadxPluginInfo}). */
	public static final String REQUIRED_JADX_VERSION = "1.5.2, r0";

	private static final String HOMEPAGE = "https://github.com/Arsylk/jadx-string-decrypt";

	private final StringDecryptOptions options = new StringDecryptOptions();

	/** compile-time constants reconstructed in prepare, read-only in decompile. */
	private final KeyData keys = new KeyData();

	/**
	 * User-registered {@code .jadx.kts} replacement pipelines. The pass holds a live reference (not a
	 * snapshot), so a script may register before or after {@link #init} as long as it happens before the
	 * methods it targets are visited. See {@code PIPELINE_SCRIPTING_PLAN.md}.
	 */
	private final PipelineRegistry pipelines = new PipelineRegistry();

	@Override
	public JadxPluginInfo getPluginInfo() {
		// Suffix the description with the UTC build timestamp from BuildInfo so the plugin list in
		// jadx-gui (and `jadx plugins --list`) makes the exact jar provenance visible at a glance.
		JadxPluginInfo info = new JadxPluginInfo(PLUGIN_ID, "Constant Deobfuscator v" + VERSION,
				"Fold compile-time-constant opaque values to literals/arrays/classes,"
						+ " and decrypt resolvable block-cipher string calls"
						+ " — built " + BuildInfo.BUILD_TIME);
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
		LOG.info("string-decrypt: Constant Deobfuscator v{} (built {})", BuildInfo.VERSION, BuildInfo.BUILD_TIME);
		context.addPass(new KeyTablesPass(options, keys));
		context.addPass(new StringDecryptPass(options, keys, pipelines));

		// jadx-gui quality-of-life: a code-area context-menu action to copy a script-pipeline skeleton for
		// the clicked class/method/field. getGuiContext() is null on the CLI, so this is a no-op there.
		JadxGuiContext gui = context.getGuiContext();
		if (gui != null) {
			StringDecryptGuiActions.register(gui);
		}
	}

	// --- script pipeline registration (see PIPELINE_SCRIPTING_PLAN.md) --------------------------------

	/**
	 * Register a replacement pipeline for an exact method, matched by its raw full id
	 * ({@code pkg.Cls.m(ILjava/lang/String;)Ljava/lang/String;}). This is the fast path. The returned
	 * {@link PipelineRegistration} can disable/remove the pipeline later.
	 */
	public PipelineRegistration pipeline(String name, String methodId, ScriptPipeline pipeline) {
		return pipelines.register(new PipelineRegistration(name, methodId, PipelineMatcher.exact(methodId), pipeline, pipelines));
	}

	/** Register a replacement pipeline matched by a {@link PipelineMatcher} predicate (slower; checked per candidate). */
	public PipelineRegistration pipeline(String name, PipelineMatcher matcher, ScriptPipeline pipeline) {
		return pipelines.register(new PipelineRegistration(name, null, matcher, pipeline, pipelines));
	}

	// --- classloader-safe registration (standalone .jadx.kts scripts) ---------------------------------

	/**
	 * Register a replacement pipeline using <b>only shared types</b> (JDK {@link java.util.function.Function}
	 * / {@link java.util.Map} and jadx-core {@link jadx.core.dex.instructions.args.ArgType} /
	 * {@link jadx.core.dex.nodes.InsnNode}). Unlike {@link #pipeline}, this works when the plugin is an
	 * <em>installed jar</em> loaded in its own classloader: a {@code .jadx.kts} script never references a
	 * plugin-defined type, so there is no class-identity mismatch. Call it reflectively from a script:
	 *
	 * <pre>{@code
	 * val plugin = jadx.pluginContext.plugins().getById("string-decrypt").pluginInstance
	 * plugin.javaClass.getMethod("registerPipeline", String::class.java, String::class.java,
	 *         java.util.function.Function::class.java)
	 *     .invoke(plugin, "my-rule", "a.b.C.dec(Ljava/lang/String;)Ljava/lang/String;",
	 *         java.util.function.Function<Map<String, Any?>, Any?> { frame ->
	 *             val key = (frame["args"] as Array<*>).getOrNull(0) as? String ?: return@Function null
	 *             decode(key) // a String -> replaces the call with that literal
	 *         })
	 * }</pre>
	 *
	 * See {@link ScriptBridge} for the full frame-map and return-value contract.
	 */
	public PipelineRegistration registerPipeline(String name, String methodId,
			java.util.function.Function<java.util.Map<String, Object>, Object> callback) {
		return pipeline(name, methodId, ScriptBridge.asPipeline(callback));
	}

	/** As {@link #registerPipeline(String, String, java.util.function.Function)} but matched by a shared-type predicate. */
	public PipelineRegistration registerPipeline(String name,
			java.util.function.Predicate<java.util.Map<String, Object>> matcher,
			java.util.function.Function<java.util.Map<String, Object>, Object> callback) {
		return pipeline(name, ScriptBridge.asMatcher(matcher), ScriptBridge.asPipeline(callback));
	}

	/** Remove every pipeline registered under {@code name} (exact or predicate). Returns how many were removed. */
	public int removePipeline(String name) {
		int removed = 0;
		for (PipelineRegistration reg : pipelines.all()) {
			if (reg.name().equals(name)) {
				reg.remove();
				removed++;
			}
		}
		return removed;
	}

	/** Remove every registered pipeline. */
	public void clearPipelines() {
		pipelines.clear();
	}

	/** A snapshot of the currently registered pipelines, in registration order. */
	public List<PipelineRegistration> getPipelines() {
		return pipelines.all();
	}

	/** The classloader that defines this plugin's pipeline API types — for script-side mismatch diagnostics. */
	public ClassLoader apiClassLoader() {
		return PipelineResult.class.getClassLoader();
	}
}
