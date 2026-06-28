package jadx.plugins.stringdecrypt;

import jadx.core.dex.instructions.args.ArgType;

/**
 * Decides whether a {@link ScriptPipeline} should run against a given {@link PipelineFrame}. Exact-id
 * registration ({@link StringDecryptPlugin#pipeline(String, String, ScriptPipeline)}) is the fast path;
 * a matcher enables broader matching (by owner class, name, return type, …) at the cost of being checked
 * against every candidate.
 */
@FunctionalInterface
public interface PipelineMatcher {

	boolean matches(PipelineFrame frame) throws Exception;

	/** Matches a single exact raw full method id, e.g. {@code pkg.Cls.m(ILjava/lang/String;)Ljava/lang/String;}. */
	static PipelineMatcher exact(String rawFullId) {
		return frame -> rawFullId.equals(frame.rawFullId());
	}

	/** Matches any call whose declaring class raw name equals {@code rawClassName}. */
	static PipelineMatcher owner(String rawClassName) {
		return frame -> {
			ArgType decl = frame.call() == null ? null : frame.call().getDeclClass().getType();
			return decl != null && rawClassName.equals(decl.getObject());
		};
	}

	/** Matches any call whose method name equals {@code methodName}. */
	static PipelineMatcher name(String methodName) {
		return frame -> frame.call() != null && methodName.equals(frame.call().getName());
	}

	/** Matches any call whose return type equals {@code type}. */
	static PipelineMatcher returns(ArgType type) {
		return frame -> frame.call() != null && type.equals(frame.call().getReturnType());
	}

	/** Matches any call whose short method descriptor ({@code name(args)ret}) ends with {@code descriptor}. */
	static PipelineMatcher descriptor(String descriptor) {
		return frame -> frame.call() != null && frame.call().getShortId().endsWith(descriptor);
	}

	static PipelineMatcher anyOf(PipelineMatcher... matchers) {
		return frame -> {
			for (PipelineMatcher m : matchers) {
				if (m.matches(frame)) {
					return true;
				}
			}
			return false;
		};
	}

	static PipelineMatcher allOf(PipelineMatcher... matchers) {
		return frame -> {
			for (PipelineMatcher m : matchers) {
				if (!m.matches(frame)) {
					return false;
				}
			}
			return true;
		};
	}
}
