package jadx.plugins.stringdecrypt;

import org.jetbrains.annotations.Nullable;

/**
 * A live handle to one registered {@link ScriptPipeline}, returned from
 * {@link StringDecryptPlugin#pipeline}. A script can {@link #disable()}, re-{@link #enable()}, or
 * {@link #remove()} its own registration. Also carries the package-private matcher/callback the pass
 * consults.
 */
public final class PipelineRegistration {

	private final String name;
	private final @Nullable String exactId;
	private final PipelineMatcher matcher;
	private final ScriptPipeline pipeline;
	private final PipelineRegistry registry;
	private volatile boolean enabled = true;

	PipelineRegistration(String name, @Nullable String exactId, PipelineMatcher matcher,
			ScriptPipeline pipeline, PipelineRegistry registry) {
		this.name = name;
		this.exactId = exactId;
		this.matcher = matcher;
		this.pipeline = pipeline;
		this.registry = registry;
	}

	public String name() {
		return name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public PipelineRegistration enable() {
		enabled = true;
		return this;
	}

	public PipelineRegistration disable() {
		enabled = false;
		return this;
	}

	/** Remove this pipeline from the plugin's registry. */
	public void remove() {
		registry.remove(this);
	}

	// --- package-private accessors ----------------------------------------------------------------

	@Nullable
	String exactId() {
		return exactId;
	}

	PipelineMatcher matcher() {
		return matcher;
	}

	ScriptPipeline pipeline() {
		return pipeline;
	}

	@Override
	public String toString() {
		return "PipelineRegistration{name='" + name + "'" + (exactId != null ? ", id=" + exactId : ", predicate") + '}';
	}
}
