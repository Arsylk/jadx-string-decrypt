/**
 * Script pipeline example: an app-specific XOR string decoder.
 *
 * The obfuscator hides strings behind a helper `s(char key, String input)` that XORs each char of
 * `input` with `key`. This registers a replacement pipeline so jadx renders the decoded literal
 * directly in the decompiled output — without the script touching raw jadx IR.
 *
 * Run with: jadx --use-scripts ...  (or drop into the GUI scripts panel). The string-decrypt plugin
 * must be installed/bundled; this script reuses its API, so do NOT `@DependsOn` a second copy.
 */
import jadx.plugins.stringdecrypt.PipelineResult
import jadx.plugins.stringdecrypt.StringDecryptPlugin

val jadx = getJadxInstance()

// Get the loaded plugin instance (bundled mode: same classloader, so the API types match).
val stringDecrypt = jadx.pluginContext.plugins()
    .getInstance(StringDecryptPlugin::class.java)

// Exact raw method id: pkg.Class.method(argDescriptors)returnDescriptor
stringDecrypt.pipeline(
    "sample-xor-char-string",
    "da.ba.aa.fa.s(CLjava/lang/String;)Ljava/lang/String;",
) { frame ->
    // arg(0) is the first DECLARED argument (the receiver, if any, is frame.receiver()).
    val key = frame.arg(0)?.charValue() ?: return@pipeline PipelineResult.keep()
    val input = frame.arg(1)?.string() ?: return@pipeline PipelineResult.keep()

    val out = input.map { ch -> (ch.code xor key.code).toChar() }.joinToString("")

    // Return a typed replacement; the plugin builds the IR (ConstStringNode) and copies metadata.
    // cleanupArg(1) lets the pass sweep the now-dead input array builder (use-list validated).
    PipelineResult.replaceString(out)
        .cleanupArg(1)
        .comment("\"$input\" -> \"$out\"")
}
