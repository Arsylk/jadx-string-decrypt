/**
 * Script pipeline example: resolve an obfuscated class-name lookup to a `Class<?>` literal.
 *
 * The app hides class references behind a helper `cls(String name)` that returns `Class.forName(map(name))`.
 * When the script can compute the real class, replaceClass / replaceClassType make jadx render the
 * `.class` literal (e.g. `String.class`) instead of the opaque call.
 *
 * Note: pipelines run AFTER the built-in decryption/folding/de-indirection, so the argument a pipeline
 * reads (frame.arg(0).string()) is already the decrypted/folded value when the built-ins resolved it.
 */
import jadx.core.dex.instructions.args.ArgType
import jadx.plugins.stringdecrypt.PipelineResult
import jadx.plugins.stringdecrypt.StringDecryptPlugin

val jadx = getJadxInstance()
val stringDecrypt = jadx.pluginContext.plugins()
    .getInstance(StringDecryptPlugin::class.java)

// A tiny app-specific name map (the real obfuscator's lookup table).
val nameMap = mapOf("0" to "java.lang.String", "1" to "java.util.List")

stringDecrypt.pipeline(
    "class-name-resolver",
    "a.b.Refl.cls(Ljava/lang/String;)Ljava/lang/Class;",
) { frame ->
    val key = frame.arg(0)?.string() ?: return@pipeline PipelineResult.keep()
    val fqn = nameMap[key] ?: return@pipeline PipelineResult.keep()

    // replaceClassType handles app types that have no host Class; replaceClass(...) works for JDK types.
    PipelineResult.replaceClassType(ArgType.`object`(fqn)).comment("cls(\"$key\") -> $fqn")
}
