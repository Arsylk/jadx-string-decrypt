package jadx.plugins.stringdecrypt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import jadx.core.dex.info.MethodInfo;
import jadx.core.dex.instructions.InvokeNode;
import jadx.core.dex.instructions.args.ArgType;
import jadx.core.dex.instructions.mods.ConstructorInsn;
import jadx.core.dex.nodes.ClassNode;
import jadx.core.dex.nodes.InsnNode;
import jadx.core.dex.nodes.MethodNode;

/**
 * A script's typed, read-only view over one candidate call site. Argument indexing is designed to
 * remove the most common scripting footgun: {@link #arg(int) arg(0)} is the first <em>declared</em>
 * method argument (never the receiver), {@link #receiver()} is the instance object, and
 * {@link #rawArg(int)} exposes the raw jadx invoke layout for the rare case a script needs it.
 *
 * <p>
 * Instances are created by the pass per candidate and confined to a single decompile thread. Scripts
 * receive frames but cannot forge them (the constructor is package-private).
 */
public final class PipelineFrame {

	private final MethodNode mth;
	private final Evaluator ev;
	private final ObjectEvaluator oev;
	private final InsnNode insn;
	private final @Nullable InvokeNode invoke;
	private final int firstArgOffset;

	private final PipelineValue[] userArgs;
	private @Nullable PipelineValue receiverValue;
	private boolean receiverComputed;
	private final List<String> notes = new ArrayList<>(0);

	PipelineFrame(MethodNode mth, Evaluator ev, ObjectEvaluator oev, InsnNode insn) {
		this.mth = mth;
		this.ev = ev;
		this.oev = oev;
		this.insn = insn;
		this.invoke = insn instanceof InvokeNode ? (InvokeNode) insn : null;
		this.firstArgOffset = invoke != null ? invoke.getFirstArgOffset() : 0;
		int userCount = invoke != null ? Math.max(0, invoke.getArgsCount() - firstArgOffset) : 0;
		this.userArgs = new PipelineValue[userCount];
	}

	// --- identity ---------------------------------------------------------------------------------

	public MethodNode method() {
		return mth;
	}

	public ClassNode declaringClass() {
		return mth.getParentClass();
	}

	/** The candidate as an {@link InvokeNode}, or {@code null} if it is some other instruction type. */
	public @Nullable InvokeNode invoke() {
		return invoke;
	}

	public InsnNode instruction() {
		return insn;
	}

	/** The called method, or {@code null} for a non-call candidate. */
	public @Nullable MethodInfo call() {
		if (invoke != null) {
			return invoke.getCallMth();
		}
		if (insn instanceof ConstructorInsn) {
			return ((ConstructorInsn) insn).getCallMth();
		}
		return null;
	}

	/** The raw full method id ({@code pkg.Cls.m(args)ret}), or {@code null} for a non-call candidate. */
	public @Nullable String rawFullId() {
		MethodInfo c = call();
		return c == null ? null : c.getRawFullId();
	}

	public boolean isStaticCall() {
		return invoke != null && invoke.isStaticCall();
	}

	public boolean hasReceiver() {
		return invoke != null && !invoke.isStaticCall() && firstArgOffset >= 1 && invoke.getArgsCount() >= 1;
	}

	public int userArgCount() {
		return userArgs.length;
	}

	public int rawArgCount() {
		return insn.getArgsCount();
	}

	// --- values -----------------------------------------------------------------------------------

	/** The instance receiver for an instance call, or {@code null} for a static / non-call candidate. */
	public @Nullable PipelineValue receiver() {
		if (!receiverComputed) {
			if (hasReceiver()) {
				receiverValue = new PipelineValue(this, insn.getArg(0), -1, 0, true);
			}
			receiverComputed = true;
		}
		return receiverValue;
	}

	/** The {@code userIndex}-th declared argument (receiver excluded), or {@code null} if out of range. */
	public @Nullable PipelineValue arg(int userIndex) {
		if (userIndex < 0 || userIndex >= userArgs.length) {
			return null;
		}
		PipelineValue v = userArgs[userIndex];
		if (v == null) {
			int rawIndex = firstArgOffset + userIndex;
			v = new PipelineValue(this, insn.getArg(rawIndex), userIndex, rawIndex, false);
			userArgs[userIndex] = v;
		}
		return v;
	}

	/** The raw jadx invoke argument at {@code rawIndex} (index 0 is the receiver for instance calls). */
	public @Nullable PipelineValue rawArg(int rawIndex) {
		if (rawIndex < 0 || rawIndex >= insn.getArgsCount()) {
			return null;
		}
		boolean isReceiver = invoke != null && !invoke.isStaticCall() && rawIndex == 0;
		int userIndex = isReceiver ? -1 : rawIndex - firstArgOffset;
		return new PipelineValue(this, insn.getArg(rawIndex), userIndex, rawIndex, isReceiver);
	}

	public List<PipelineValue> args() {
		List<PipelineValue> out = new ArrayList<>(userArgs.length);
		for (int i = 0; i < userArgs.length; i++) {
			out.add(arg(i));
		}
		return out;
	}

	public List<PipelineValue> rawArgs() {
		int n = insn.getArgsCount();
		List<PipelineValue> out = new ArrayList<>(n);
		for (int i = 0; i < n; i++) {
			out.add(rawArg(i));
		}
		return out;
	}

	// --- types ------------------------------------------------------------------------------------

	public @Nullable ArgType returnType() {
		if (invoke != null) {
			return invoke.getCallMth().getReturnType();
		}
		MethodInfo c = call();
		return c == null ? null : c.getReturnType();
	}

	/** The type the replacement must remain assignable to (the result-register type, else the return type). */
	public @Nullable ArgType targetType() {
		if (insn.getResult() != null) {
			return insn.getResult().getType();
		}
		if (insn instanceof ConstructorInsn) {
			return ((ConstructorInsn) insn).getCallMth().getDeclClass().getType();
		}
		return returnType();
	}

	public int offset() {
		return insn.getOffset();
	}

	// --- diagnostics ------------------------------------------------------------------------------

	/** Attach a concise, user-facing note that is surfaced in the method's decrypt-summary comment. */
	public void note(String message) {
		notes.add(message);
	}

	// --- package-private internals ----------------------------------------------------------------

	Evaluator evaluator() {
		return ev;
	}

	ObjectEvaluator objectEvaluator() {
		return oev;
	}

	List<String> notes() {
		return Collections.unmodifiableList(notes);
	}
}
