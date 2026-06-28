/**
 * Standalone script pipeline — works when string-decrypt is an INSTALLED jar.
 *
 * An installed plugin is loaded in its own classloader (jadx's `JadxExternalPluginsLoader` gives each
 * plugin jar a private `URLClassLoader`), while a `.jadx.kts` script is compiled and evaluated against the
 * app classloader's classpath. The plugin jar is therefore NOT on the script's compile classpath, so a
 * script cannot `import jadx.plugins.stringdecrypt.*` — and depending on the jar with `@file:DependsOn`
 * would load a second, incompatible copy of those types. The typed `plugin.pipeline(...)` API only works
 * when the plugin is bundled in-tree.
 *
 * The fix: the plugin exposes a classloader-safe entry point, `registerPipeline`, whose whole signature is
 * shared types — JDK `Function`/`Map` plus jadx-core `ArgType`. The script calls it reflectively (the
 * `pipeline(...)` helper below wraps that), passing a callback that references zero plugin types. Nothing
 * the script touches needs to be loaded by the plugin's classloader, so the split no longer matters.
 *
 * Callback contract — `(frame: Map<String, Any?>) -> Any?`:
 *   frame["id" | "owner" | "name" | "shortId"]  -> String      (call identity; null id for non-calls)
 *   frame["static"]                              -> Boolean
 *   frame["returnType" | "targetType"]           -> jadx ArgType
 *   frame["argTypes"]                            -> Array<ArgType?>
 *   frame["args"]                                -> Array<Any?>  resolved declared args (receiver excluded);
 *                                                   String / boxed scalar / array / java.lang.Class, or
 *                                                   null when that arg is not a compile-time constant
 *   frame["receiver" | "receiverType"]           -> resolved instance value / its ArgType (instance calls)
 *
 * Return value:
 *   null                       -> keep (decline; built-in folders/de-indirection already ran first)
 *   String / boxed scalar /
 *     array / java.lang.Class  -> replace the call with that literal
 *   jadx ArgType               -> replace with a `.class` literal of that type (handles app-only types)
 *   Map<String, Any?>          -> structured action, e.g.
 *       mapOf("value" to v, "comment" to "note", "cleanupArgs" to intArrayOf(0))
 *       mapOf("classType" to ArgType.`object`("a.b.Foo"))
 *       mapOf("nullType" to ArgType.`object`("a.b.Bar"))
 *       mapOf("fail" to "reason") / mapOf("keep" to true)
 */
import jadx.core.dex.instructions.args.ArgType
import java.util.function.Function

val jadx = getJadxInstance()

// --- paste-in helper: no plugin types referenced, safe for an installed (standalone) plugin --------
val plugin = jadx.pluginContext.plugins().getById("string-decrypt")?.pluginInstance
    ?: error("string-decrypt plugin is not installed/enabled")

fun pipeline(name: String, methodId: String, body: (Map<String, Any?>) -> Any?) {
    val fn = Function<Map<String, Any?>, Any?> { body(it) }
    plugin.javaClass
        .getMethod("registerPipeline", String::class.java, String::class.java, Function::class.java)
        .invoke(plugin, name, methodId, fn)
}

// --- example 1: resolve an app-specific name lookup to a String literal ----------------------------
// The app hides config keys behind `Cfg.flag(String)`. Pipelines run AFTER the built-in decryption,
// so the argument is already the decrypted/folded value by the time this sees it.
val flagNames = mapOf("0" to "FEATURE_ENABLED", "1" to "FEATURE_DISABLED")

pipeline("flag-name", "a.b.Cfg.flag(Ljava/lang/String;)Ljava/lang/String;") { frame ->
    val key = (frame["args"] as Array<*>).getOrNull(0) as? String ?: return@pipeline null
    flagNames[key] // String -> the call becomes the literal "FEATURE_ENABLED"
}

// --- example 2: resolve an obfuscated class lookup to a `.class` literal ----------------------------
// `Refl.cls(String)` returns Class.forName(map(name)); return a jadx ArgType to emit `Foo.class`
// even for app-only types that have no host Class on the decompiler's classpath.
val classNames = mapOf("a" to "com.app.Session", "b" to "com.app.User")

pipeline("class-name", "a.b.Refl.cls(Ljava/lang/String;)Ljava/lang/Class;") { frame ->
    val key = (frame["args"] as Array<*>).getOrNull(0) as? String ?: return@pipeline null
    val fqn = classNames[key] ?: return@pipeline null
    mapOf("classType" to ArgType.`object`(fqn), "comment" to "cls(\"$key\") -> $fqn")
}
