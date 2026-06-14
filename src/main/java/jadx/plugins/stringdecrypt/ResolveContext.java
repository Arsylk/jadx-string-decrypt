package jadx.plugins.stringdecrypt;

import java.util.List;

import jadx.core.dex.instructions.args.RegisterArg;
import jadx.core.dex.nodes.InsnNode;
import jadx.core.dex.nodes.MethodNode;

/**
 * Per-candidate state handed to each {@link Resolver}: the candidate instruction plus the current
 * method's evaluators and the pass-owned accumulators a resolver writes back into (the decrypted-string
 * summary and the array-build cleanup list). Created fresh for every candidate, so it is confined to a
 * single decompile thread.
 *
 * <p>
 * This is the precursor to the script-pipeline {@code PipelineFrame}: a typed view over one call site
 * that exposes the evaluators without leaking the pass internals.
 */
final class ResolveContext {

	final MethodNode mth;
	final Evaluator ev;
	final ObjectEvaluator oev;
	final InsnNode insn;
	/** Quoted forms of every string a resolver emits — surfaced in the method's decrypt summary comment. */
	final List<String> decrypted;
	/** Array-build registers a resolver consumed, swept by the pass's post-fold cleanup. */
	final List<RegisterArg> builtArrays;
	/** True when {@code insn}'s value flows into a reflective name lookup (gates non-printable folds). */
	final boolean consumerIsLookup;

	ResolveContext(MethodNode mth, Evaluator ev, ObjectEvaluator oev, InsnNode insn,
			List<String> decrypted, List<RegisterArg> builtArrays, boolean consumerIsLookup) {
		this.mth = mth;
		this.ev = ev;
		this.oev = oev;
		this.insn = insn;
		this.decrypted = decrypted;
		this.builtArrays = builtArrays;
		this.consumerIsLookup = consumerIsLookup;
	}
}
