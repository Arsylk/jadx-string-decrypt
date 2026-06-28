package jadx.plugins.stringdecrypt;

/**
 * A user-defined replacement pipeline registered from a {@code .jadx.kts} script (or Java). Given a
 * {@link PipelineFrame} — a typed view over one candidate call site — it decides what (if anything)
 * should replace that instruction, returning a {@link PipelineResult}.
 *
 * <p>
 * Kept a plain Java {@link FunctionalInterface} on purpose: it takes a natural lambda from Kotlin, needs
 * no Kotlin-stdlib types in the plugin's public API, and can be bridged by a {@link java.lang.reflect.Proxy}
 * if a future script/classloader bridge must register without compile-time class identity.
 *
 * <p>
 * Callbacks may run concurrently (jadx visits methods on multiple threads). Prefer pure functions; if
 * you must cache, use a thread-safe structure. Returning {@link PipelineResult#keep()} (or any decline)
 * is the normal outcome for a non-constant argument — do not throw for that.
 */
@FunctionalInterface
public interface ScriptPipeline {

	PipelineResult evaluate(PipelineFrame frame) throws Exception;
}
