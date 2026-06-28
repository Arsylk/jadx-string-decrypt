package jadx.plugins.stringdecrypt;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import jadx.api.JadxArgs;
import jadx.api.JadxDecompiler;
import jadx.api.JavaClass;
import jadx.core.plugins.PluginContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end pass integration for script pipelines, run against the real {@code ali-crackme-1.apk}: it
 * proves the {@link ScriptPipelineResolver} is actually consulted during decompilation and that a
 * registered pipeline's {@link PipelineResult} is applied to the IR. Pipelines run <b>last</b> (after
 * the built-in decryption/folding/de-indirection), so a pipeline handles the calls the built-ins left;
 * decline ({@code keep()}) leaves the built-in deobfuscation untouched.
 */
class PipelinePassTest {

	private static final String APK = "ali-crackme-1.apk";
	private static final String CLASS = "k2015.a1.Check";

	@Test
	void pipelinesAreConsultedDuringDecompilation_andKeepIsNeutral() {
		AtomicInteger callbackHits = new AtomicInteger();
		String code = decompileWith(plugin -> plugin.pipeline("count-all", frame -> true, frame -> {
			callbackHits.incrementAndGet();
			return PipelineResult.keep();
		}));
		// the resolver ran against many invoke candidates...
		assertThat(callbackHits.get()).isGreaterThan(0);
		// ...and keep() left the normal deobfuscation intact (a known de-indirected call still appears).
		assertThat(code).contains("BigDecimal.valueOf(");
	}

	@Test
	void exactPipelineReplacesLeftoverCall_andResolvesArg() {
		// The AliCrackme invokes everything reflectively, so after the built-ins run (decryption, folding,
		// de-indirection) the pipeline — last in the chain — sees the JDK calls they could not fold.
		// Target reflective Class.forName(runtimeName) and rewrite it to a fixed class literal: a Class
		// replaces a Class, so downstream stays type-correct. Also exercises arg(0).string() resolution.
		String forNameId = "java.lang.Class.forName(Ljava/lang/String;)Ljava/lang/Class;";
		AtomicInteger replaced = new AtomicInteger();
		String code = decompileWith(plugin -> plugin.pipeline("forname-rewriter", forNameId, frame -> {
			frame.arg(0); // exercise user-arg mapping for an instance/static call
			replaced.incrementAndGet();
			return PipelineResult.replaceClass(java.util.UUID.class).comment("forName -> UUID.class");
		}));
		assertThat(replaced.get()).as("Class.forName leftovers matched by exact id").isGreaterThan(0);
		assertThat(code).contains("UUID"); // the replacement is rendered in the decompiled output
		assertThat(code).doesNotContain("JADX ERROR");
	}

	@Test
	void callbackExceptionIsLoggedAndDoesNotAbortDecompilation() {
		AtomicInteger hits = new AtomicInteger();
		String code = decompileWith(plugin -> plugin.pipeline("boom", frame -> true, frame -> {
			hits.incrementAndGet();
			throw new IllegalStateException("intentional pipeline failure");
		}));
		// the throwing callback ran, was caught + declined, and the built-in deobfuscation still completed
		assertThat(hits.get()).isGreaterThan(0);
		assertThat(code).contains("BigDecimal.valueOf(");
		assertThat(code).doesNotContain("JADX ERROR");
	}

	@Test
	void commentOnlyAnnotatesWithoutReplacing() {
		String marker = "PIPELINE_NOTE_MARKER";
		String forNameId = "java.lang.Class.forName(Ljava/lang/String;)Ljava/lang/Class;";
		String code = decompileWith(plugin -> plugin.pipeline("annotator", forNameId,
				frame -> PipelineResult.commentOnly(marker)));
		// the note surfaces in the method's decrypt-summary comment, and the call itself is left in place
		assertThat(code).contains(marker);
		assertThat(code).contains("Class.forName(");
	}

	@Test
	void disabledPipelineDoesNotRun() {
		AtomicInteger hits = new AtomicInteger();
		String code = decompileWith(plugin -> plugin
				.pipeline("disabled", frame -> true, frame -> {
					hits.incrementAndGet();
					return PipelineResult.keep();
				})
				.disable());
		assertThat(hits.get()).isZero();
		assertThat(code).contains("BigDecimal.valueOf(");
	}

	private static String decompileWith(Consumer<StringDecryptPlugin> setup) {
		return decompileWith(opts -> {
		}, setup);
	}

	/**
	 * Decompile the target class, registering pipelines on jadx's <em>own</em> loaded plugin instance.
	 * This mirrors a real {@code .jadx.kts} flow (get the plugin instance, register, then read code) and
	 * relies on the registry being a live reference the pass reads at method-visit time — registering
	 * after {@code load()} but before the lazy {@code getCode()} is enough. (A {@code registerPlugin}
	 * instance would be discarded: {@code load()} clears and re-resolves plugins from the loader.)
	 */
	private static String decompileWith(Consumer<Map<String, String>> options, Consumer<StringDecryptPlugin> setup) {
		File apk = sampleFile(APK);
		JadxArgs args = new JadxArgs();
		args.getInputFiles().add(apk);
		args.setSkipResources(true);
		options.accept(args.getPluginOptions());
		try (JadxDecompiler jadx = new JadxDecompiler(args)) {
			jadx.load();
			setup.accept(loadedPlugin(jadx));
			JavaClass cls = jadx.searchJavaClassByOrigFullName(CLASS);
			assertThat(cls).as("class %s not found", CLASS).isNotNull();
			return cls.getCode();
		}
	}

	private static StringDecryptPlugin loadedPlugin(JadxDecompiler jadx) {
		for (PluginContext ctx : jadx.getPluginManager().getResolvedPluginContexts()) {
			if (ctx.getPluginInstance() instanceof StringDecryptPlugin) {
				return (StringDecryptPlugin) ctx.getPluginInstance();
			}
		}
		throw new AssertionError("string-decrypt plugin not loaded");
	}

	private static File sampleFile(String name) {
		URL res = PipelinePassTest.class.getClassLoader().getResource("test-samples/" + name);
		assertThat(res).as("missing test sample %s", name).isNotNull();
		return new File(res.getFile());
	}
}
