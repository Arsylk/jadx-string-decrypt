/**
 * Script pipeline example: a decoder that returns a char[] (not a String).
 *
 * Some obfuscators decode into a `char[]` that is later wrapped in `new String(...)`. A pipeline can
 * return the decoded array with replaceChars(...) and the plugin emits a proper `new char[]{...}`
 * literal — one of several typed replacement shapes (replaceBytes / replaceInts / replaceClass / ...).
 *
 * Predicate matching is used here instead of an exact id: match by owner class + descriptor.
 */
import jadx.plugins.stringdecrypt.PipelineMatcher
import jadx.plugins.stringdecrypt.PipelineResult
import jadx.plugins.stringdecrypt.StringDecryptPlugin

val jadx = getJadxInstance()
val stringDecrypt = jadx.pluginContext.plugins()
    .getInstance(StringDecryptPlugin::class.java)

// Predicate registration: any method on a.b.Codec whose descriptor decodes a byte[] to a char[].
stringDecrypt.pipeline(
    "char-array-decoder",
    PipelineMatcher.allOf(
        PipelineMatcher.owner("a.b.Codec"),
        PipelineMatcher.descriptor("([B)[C"),
    ),
) { frame ->
    val data = frame.arg(0)?.bytes() ?: return@pipeline PipelineResult.keep()
    val out = CharArray(data.size) { i -> (data[i].toInt() xor 0x5A).toChar() }
    PipelineResult.replaceChars(out).cleanupArg(0)
}
