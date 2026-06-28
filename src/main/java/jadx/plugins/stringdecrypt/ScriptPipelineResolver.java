package jadx.plugins.stringdecrypt;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jadx.core.dex.instructions.ConstClassNode;
import jadx.core.dex.instructions.InsnType;
import jadx.core.dex.instructions.InvokeNode;
import jadx.core.dex.instructions.args.ArgType;
import jadx.core.dex.instructions.args.RegisterArg;
import jadx.core.dex.nodes.InsnNode;

/**
 * Runs user-registered {@link ScriptPipeline}s against a candidate, <b>last</b> in the
 * {@link StringDecryptPass} resolver pipeline: the built-in decryption, constant folding and
 * de-indirection happen automatically first, so a pipeline operates on the already-resolved values and
 * handles the app-specific calls the built-ins declined. Builds a {@link PipelineFrame}, consults the
 * {@link PipelineRegistry} (exact-id fast path, then predicates), runs each enabled pipeline in order,
 * and converts the first {@link PipelineResult.Kind#REPLACE} into jadx IR through the shared
 * {@link ReplacementFactory}. {@code keep()} declines, {@code commentOnly()} annotates and continues,
 * {@code fail()} is logged and continues.
 *
 * <p>
 * A callback exception never aborts decompilation (default policy): it is logged with pipeline/target
 * context and treated as a decline.
 */
final class ScriptPipelineResolver implements Resolver {

	private static final Logger LOG = LoggerFactory.getLogger(ScriptPipelineResolver.class);

	private final StringDecryptOptions options;
	private final PipelineRegistry registry;

	ScriptPipelineResolver(StringDecryptOptions options, PipelineRegistry registry) {
		this.options = options;
		this.registry = registry;
	}

	@Override
	public @Nullable InsnNode resolve(ResolveContext ctx) {
		if (!options.isScriptPipelines() || registry.isEmpty()) {
			return null;
		}
		// First implementation targets call candidates; preserve the API shape for future InsnNode kinds.
		if (!(ctx.insn instanceof InvokeNode)) {
			return null;
		}
		PipelineFrame frame = new PipelineFrame(ctx.mth, ctx.ev, ctx.oev, ctx.insn);
		List<PipelineRegistration> candidates = registry.candidatesFor(frame.rawFullId());
		if (candidates == null) {
			return null;
		}
		for (PipelineRegistration reg : candidates) {
			if (!reg.isEnabled()) {
				continue;
			}
			if (!safeMatch(reg, frame)) {
				continue;
			}
			PipelineResult result = safeEvaluate(reg, frame);
			if (result == null) {
				continue; // callback threw -> logged, declined
			}
			if (!sameApiClassLoader(result)) {
				LOG.error("Pipeline '{}' returned a PipelineResult loaded by {}, but string-decrypt expects {}."
						+ " Do not load a second copy of jadx-string-decrypt via @DependsOn; use the bundled plugin API.",
						reg.name(), result.getClass().getClassLoader(), PipelineResult.class.getClassLoader());
				continue;
			}
			switch (result.kind()) {
				case KEEP:
					continue;
				case COMMENT_ONLY:
					addComment(ctx, reg, frame, result);
					continue;
				case FAIL:
					LOG.warn("Pipeline '{}' failed on {}: {}", reg.name(), frame.rawFullId(), result.message(),
							result.cause());
					continue;
				case REPLACE:
					InsnNode repl = convert(ctx, frame, result);
					if (repl == null) {
						LOG.warn("Pipeline '{}' could not represent its result for {} (target type {}); keeping original",
								reg.name(), frame.rawFullId(), frame.targetType());
						continue;
					}
					applyCleanup(ctx, frame, result);
					addComment(ctx, reg, frame, result);
					LOG.debug("Pipeline '{}' replaced {} in {}", reg.name(), frame.rawFullId(), ctx.mth);
					return repl;
				default:
					continue;
			}
		}
		return null;
	}

	private boolean safeMatch(PipelineRegistration reg, PipelineFrame frame) {
		try {
			return reg.matcher().matches(frame);
		} catch (Throwable t) {
			LOG.warn("Pipeline '{}' matcher threw on {}", reg.name(), frame.rawFullId(), t);
			return false;
		}
	}

	private @Nullable PipelineResult safeEvaluate(PipelineRegistration reg, PipelineFrame frame) {
		try {
			return reg.pipeline().evaluate(frame);
		} catch (Throwable t) {
			LOG.warn("Pipeline '{}' callback threw on {}", reg.name(), frame.rawFullId(), t);
			return null;
		}
	}

	/** Convert a REPLACE result into a replacement IR node, or {@code null} when it cannot be represented. */
	private @Nullable InsnNode convert(ResolveContext ctx, PipelineFrame frame, PipelineResult result) {
		InsnNode from = ctx.insn;
		if (result.rawInsn() != null) {
			InsnNode raw = result.rawInsn();
			if (raw.getResult() == null) {
				ReplacementFactory.copyReplacementMetadata(from, raw); // adopt result reg/attrs/offset
			}
			return raw;
		}
		if (result.classType() != null) {
			ConstClassNode node = new ConstClassNode(result.classType());
			ReplacementFactory.copyReplacementMetadata(from, node);
			return node;
		}
		Object value = result.value();
		if (value == null) {
			return null;
		}
		ArgType targetType = result.targetTypeOverride() != null ? result.targetTypeOverride() : frame.targetType();
		return ReplacementFactory.makeReplacementInsn(from, value, targetType, ctx.decrypted, false);
	}

	/** Mark requested arguments' array builders for the pass's existing (use-list-validated) cleanup. */
	private void applyCleanup(ResolveContext ctx, PipelineFrame frame, PipelineResult result) {
		for (int userIndex : result.cleanupUserArgs()) {
			PipelineValue v = frame.arg(userIndex);
			if (v != null) {
				markArrayCleanup(ctx, v);
			}
		}
		for (int rawIndex : result.cleanupRawArgs()) {
			PipelineValue v = frame.rawArg(rawIndex);
			if (v != null) {
				markArrayCleanup(ctx, v);
			}
		}
		if (result.cleanupReceiverRequested()) {
			PipelineValue v = frame.receiver();
			if (v != null) {
				markArrayCleanup(ctx, v);
			}
		}
	}

	/**
	 * Add a value's register to the pass's array-cleanup list <em>only</em> when it is a register
	 * assigned by an array build. The pass's {@code cleanupArrayBuild} still refuses to remove it if the
	 * array is read by anything other than its build-time stores, so a script can never delete a producer
	 * that has real runtime uses.
	 */
	private void markArrayCleanup(ResolveContext ctx, PipelineValue value) {
		RegisterArg reg = value.register();
		if (reg == null) {
			return;
		}
		InsnNode assign = reg.getAssignInsn();
		if (assign != null && (assign.getType() == InsnType.NEW_ARRAY || assign.getType() == InsnType.FILLED_NEW_ARRAY)) {
			ctx.builtArrays.add(reg);
		}
	}

	private void addComment(ResolveContext ctx, PipelineRegistration reg, PipelineFrame frame, PipelineResult result) {
		if (result.message() != null) {
			ctx.decrypted.add("Pipeline " + reg.name() + ": " + result.message());
		}
		for (String note : frame.notes()) {
			ctx.decrypted.add("Pipeline " + reg.name() + ": " + note);
		}
	}

	private static boolean sameApiClassLoader(PipelineResult result) {
		return result.getClass().getClassLoader() == PipelineResult.class.getClassLoader();
	}
}
