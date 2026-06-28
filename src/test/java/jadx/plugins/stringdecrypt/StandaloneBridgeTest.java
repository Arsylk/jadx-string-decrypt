package jadx.plugins.stringdecrypt;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import jadx.api.JadxArgs;
import jadx.api.JadxDecompiler;
import jadx.api.JavaClass;
import jadx.api.plugins.JadxPlugin;
import jadx.core.dex.instructions.args.ArgType;
import jadx.core.plugins.PluginContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Proves the <b>standalone-jar</b> registration path: a {@code .jadx.kts} script that cannot reference any
 * plugin type (because an installed plugin lives in its own classloader, absent from the script's compile
 * classpath) can still register a replacement pipeline through {@link StringDecryptPlugin#registerPipeline}
 * using only shared types — JDK {@link Function}/{@link Map} and jadx-core {@link ArgType}.
 *
 * <p>
 * The end-to-end test reaches the plugin only as a bare {@link JadxPlugin} and invokes the entry point
 * <em>reflectively</em>, and the callback it passes references zero plugin-defined types — exactly the code
 * a script emits. If this works, the classloader split is irrelevant: nothing the script touches needs to
 * be loaded by the plugin's classloader. {@link #interpretContract()} locks the return-value mapping the
 * bridge applies plugin-side.
 */
class StandaloneBridgeTest {

	private static final String APK = "ali-crackme-1.apk";
	private static final String CLASS = "k2015.a1.Check";
	private static final String FOR_NAME_ID = "java.lang.Class.forName(Ljava/lang/String;)Ljava/lang/Class;";

	@Test
	void reflectiveSharedTypeRegistration_replacesEndToEnd() throws Exception {
		AtomicInteger hits = new AtomicInteger();
		AtomicReference<Map<String, Object>> seenFrame = new AtomicReference<>();

		// This callback is what a standalone script supplies: only JDK + jadx-core types, no plugin types.
		Function<Map<String, Object>, Object> callback = frame -> {
			hits.incrementAndGet();
			seenFrame.compareAndSet(null, frame);
			return java.util.UUID.class; // a JDK Class (shared) -> replaces forName(name) with UUID.class
		};

		String code = decompile(plugin -> registerReflectively(plugin, "forname-bridge", FOR_NAME_ID, callback));

		assertThat(hits.get()).as("bridge callback was consulted for Class.forName leftovers").isGreaterThan(0);
		assertThat(code).contains("UUID");
		assertThat(code).doesNotContain("JADX ERROR");

		// the frame map handed to the script carries only shared types
		Map<String, Object> frame = seenFrame.get();
		assertThat(frame).isNotNull();
		assertThat(frame.get("id")).isEqualTo(FOR_NAME_ID);
		assertThat(frame.get("name")).isEqualTo("forName");
		assertThat(frame.get("targetType")).isInstanceOf(ArgType.class);
		assertThat(frame.get("args")).isInstanceOf(Object[].class);
		assertThat(frame.get("argTypes")).isInstanceOf(ArgType[].class);
	}

	@Test
	void reflectivePredicateRegistration_canDecline() throws Exception {
		AtomicInteger matcherHits = new AtomicInteger();
		AtomicInteger callbackHits = new AtomicInteger();

		Function<Map<String, Object>, Object> matchAll = frame -> {
			matcherHits.incrementAndGet();
			return Boolean.TRUE;
		};
		Function<Map<String, Object>, Object> declineAll = frame -> {
			callbackHits.incrementAndGet();
			return null; // null == keep: the built-in deobfuscation stays intact
		};

		String code = decompile(plugin -> registerReflectivelyWithPredicate(plugin, "count-bridge", matchAll, declineAll));

		assertThat(matcherHits.get()).isGreaterThan(0);
		assertThat(callbackHits.get()).isGreaterThan(0);
		assertThat(code).contains("BigDecimal.valueOf("); // decline left the built-in result in place
		assertThat(code).doesNotContain("JADX ERROR");
	}

	/** The plugin-side mapping from a callback's plain return value to a {@link PipelineResult}. */
	@Test
	void interpretContract() {
		assertThat(ScriptBridge.interpret(null).kind()).isEqualTo(PipelineResult.Kind.KEEP);

		assertThat(ScriptBridge.interpret("hello").kind()).isEqualTo(PipelineResult.Kind.REPLACE);
		assertThat(ScriptBridge.interpret("hello").value()).isEqualTo("hello");

		assertThat(ScriptBridge.interpret(42).value()).isEqualTo(42);
		assertThat(ScriptBridge.interpret(true).value()).isEqualTo(true);
		assertThat(ScriptBridge.interpret(new char[] { 'a', 'b' }).value()).isInstanceOf(char[].class);
		assertThat(ScriptBridge.interpret(new byte[] { 1, 2 }).value()).isInstanceOf(byte[].class);

		assertThat(ScriptBridge.interpret(String.class).value()).isEqualTo(String.class);

		// jadx-core ArgType -> class-literal replacement (the app-type path, no host Class needed)
		ArgType appType = ArgType.object("a.b.Foo");
		PipelineResult byType = ScriptBridge.interpret(appType);
		assertThat(byType.kind()).isEqualTo(PipelineResult.Kind.REPLACE);
		assertThat(byType.classType()).isEqualTo(appType);
	}

	@Test
	void interpretMapActions() {
		PipelineResult fail = ScriptBridge.interpret(mapOf("fail", "nope"));
		assertThat(fail.kind()).isEqualTo(PipelineResult.Kind.FAIL);
		assertThat(fail.message()).isEqualTo("nope");

		PipelineResult keep = ScriptBridge.interpret(mapOf("keep", Boolean.TRUE, "comment", "c"));
		assertThat(keep.kind()).isEqualTo(PipelineResult.Kind.KEEP);

		Map<String, Object> spec = mapOf("value", "v", "comment", "noted", "cleanupArgs", new int[] { 0, 1 });
		PipelineResult replace = ScriptBridge.interpret(spec);
		assertThat(replace.kind()).isEqualTo(PipelineResult.Kind.REPLACE);
		assertThat(replace.value()).isEqualTo("v");
		assertThat(replace.message()).isEqualTo("noted");
		assertThat(replace.cleanupUserArgs()).containsExactly(0, 1);

		ArgType nullType = ArgType.object("a.b.Bar");
		PipelineResult typedNull = ScriptBridge.interpret(mapOf("nullType", nullType));
		assertThat(typedNull.kind()).isEqualTo(PipelineResult.Kind.REPLACE);
		assertThat(typedNull.targetTypeOverride()).isEqualTo(nullType);

		PipelineResult commentOnly = ScriptBridge.interpret(mapOf("comment", "just a note"));
		assertThat(commentOnly.kind()).isEqualTo(PipelineResult.Kind.COMMENT_ONLY);
		assertThat(commentOnly.message()).isEqualTo("just a note");
	}

	// --- helpers: invoke the entry point exactly as a script would (no plugin-type references) ---------

	private static void registerReflectively(JadxPlugin plugin, String name, String id,
			Function<Map<String, Object>, Object> cb) {
		try {
			Method m = plugin.getClass().getMethod("registerPipeline", String.class, String.class, Function.class);
			m.invoke(plugin, name, id, cb);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void registerReflectivelyWithPredicate(JadxPlugin plugin, String name,
			Function<Map<String, Object>, Object> matcher, Function<Map<String, Object>, Object> cb) {
		try {
			// the predicate is a java.util.function.Predicate; build it from the shared Function for the test
			java.util.function.Predicate<Map<String, Object>> pred = m -> Boolean.TRUE.equals(matcher.apply(m));
			Method m = plugin.getClass().getMethod("registerPipeline", String.class,
					java.util.function.Predicate.class, Function.class);
			m.invoke(plugin, name, pred, cb);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Map<String, Object> mapOf(Object... kv) {
		Map<String, Object> m = new HashMap<>();
		for (int i = 0; i < kv.length; i += 2) {
			m.put((String) kv[i], kv[i + 1]);
		}
		return m;
	}

	private static String decompile(Consumer<JadxPlugin> setup) {
		File apk = sampleFile(APK);
		JadxArgs args = new JadxArgs();
		args.getInputFiles().add(apk);
		args.setSkipResources(true);
		try (JadxDecompiler jadx = new JadxDecompiler(args)) {
			jadx.load();
			setup.accept(loadedPlugin(jadx));
			JavaClass cls = jadx.searchJavaClassByOrigFullName(CLASS);
			assertThat(cls).as("class %s not found", CLASS).isNotNull();
			return cls.getCode();
		}
	}

	/** Returns the plugin as a bare {@link JadxPlugin} — the only handle a standalone script ever gets. */
	private static JadxPlugin loadedPlugin(JadxDecompiler jadx) {
		for (PluginContext ctx : jadx.getPluginManager().getResolvedPluginContexts()) {
			JadxPlugin inst = ctx.getPluginInstance();
			if (StringDecryptPlugin.PLUGIN_ID.equals(inst.getPluginInfo().getPluginId())) {
				return inst;
			}
		}
		throw new AssertionError("string-decrypt plugin not loaded");
	}

	private static File sampleFile(String name) {
		URL res = StandaloneBridgeTest.class.getClassLoader().getResource("test-samples/" + name);
		assertThat(res).as("missing test sample %s", name).isNotNull();
		return new File(res.getFile());
	}
}
