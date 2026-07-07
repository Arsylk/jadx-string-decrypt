package jadx.plugins.stringdecrypt;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jadx.api.plugins.pass.JadxPassInfo;
import jadx.api.plugins.pass.impl.OrderedJadxPassInfo;
import jadx.api.plugins.pass.types.JadxDecompilePass;
import jadx.api.data.CommentStyle;
import jadx.core.codegen.utils.CodeComment;
import jadx.core.dex.attributes.AType;
import jadx.core.dex.info.MethodInfo;
import jadx.core.dex.instructions.BaseInvokeNode;
import jadx.core.dex.instructions.ConstClassNode;
import jadx.core.dex.instructions.ConstStringNode;
import jadx.core.dex.instructions.IndexInsnNode;
import jadx.core.dex.instructions.InsnType;
import jadx.core.dex.instructions.InvokeNode;
import jadx.core.dex.instructions.InvokeType;
import jadx.core.dex.instructions.args.ArgType;
import jadx.core.dex.instructions.args.InsnArg;
import jadx.core.dex.instructions.args.InsnWrapArg;
import jadx.core.dex.instructions.args.RegisterArg;
import jadx.core.dex.instructions.args.SSAVar;
import jadx.core.dex.instructions.mods.ConstructorInsn;
import jadx.core.dex.nodes.BlockNode;
import jadx.core.dex.nodes.ClassNode;
import jadx.core.dex.nodes.InsnNode;
import jadx.core.dex.nodes.MethodNode;
import jadx.core.dex.nodes.RootNode;
import jadx.core.utils.BlockUtils;
import jadx.core.utils.InsnRemover;
import jadx.plugins.stringdecrypt.eval.TypeMap;
import jadx.plugins.stringdecrypt.jdk.JdkInterpreter;

/**
 * Finds resolvable decrypt calls (top-level or wrapped into another instruction's argument),
 * replaces them with the decrypted or folded constant value, and removes the now-dead instructions
 * that were consumed only to build the byte[] argument.
 */
public class StringDecryptPass implements JadxDecompilePass {

	private static final Logger LOG = LoggerFactory.getLogger(StringDecryptPass.class);

	private final StringDecryptOptions options;
	private final KeyData keys;
	private final PureFold pureFold;
	private final JdkInterpreter jdk;
	/** Ordered replacement strategies; the first to return non-null wins (see {@link Resolver}). */
	private final List<Resolver> resolvers;

	public StringDecryptPass(StringDecryptOptions options, KeyData keys, PipelineRegistry pipelines) {
		this.options = options;
		this.keys = keys;
		this.jdk = new JdkInterpreter();
		// Reflective-bridge fold is gated by an option: drop the reflection handlers entirely when
		// disabled, so reflective Method.invoke / Constructor.newInstance / Field.get / Class.forName
		// chains stay verbatim in source (useful when you want to study the obfuscator's reflection
		// layer rather than see through it).
		if (!options.isFoldReflectiveBridges()) {
			jdk.unregister("java.lang.Class");
			jdk.unregister("java.lang.reflect.Method");
			jdk.unregister("java.lang.reflect.Constructor");
			jdk.unregister("java.lang.reflect.Field");
			jdk.unregister("java.lang.reflect.AccessibleObject");
		}
		this.pureFold = new PureFold(keys, jdk);
		// The replacement pipeline, in priority order. Each prong keeps its original option gate, so the
		// behaviour is identical to the former hand-rolled if-chain; future strategies are inserted here
		// rather than edited into tryReplace.
		this.resolvers = List.of(
				ctx -> options.isDecryptStrings() ? tryDecrypt(ctx.mth, ctx.ev, ctx.insn, ctx.decrypted, ctx.builtArrays) : null,
				ctx -> options.isFoldHelperCalls() ? tryDecodeBase64StringHelper(ctx.oev, ctx.insn, ctx.decrypted) : null,
				ctx -> options.isFoldHelperCalls() ? tryDecodePrefixedRc4String(ctx.oev, ctx.insn, ctx.decrypted) : null,
				ctx -> options.isFoldHelperCalls() ? tryFoldConstValue(ctx.oev, ctx.insn, ctx.decrypted, ctx.consumerIsLookup) : null,
				ctx -> options.isFoldHelperCalls() ? tryFoldPureString(ctx.mth, ctx.ev, ctx.insn, ctx.decrypted) : null,
				// Reflective calls the folders left un-constant get rewritten to direct calls.
				new DeindirectionResolver(options),
				// Runs last: the built-in decryption/folding/de-indirection happen automatically first, so a
				// user .jadx.kts pipeline operates on the already-resolved values and handles the
				// app-specific calls the built-ins declined (its frame args are already decrypted/folded
				// because wrapped producers are resolved inner-first).
				new ScriptPipelineResolver(options, pipelines));
	}

	@Override
	public JadxPassInfo getInfo() {
		return new OrderedJadxPassInfo("StringDecrypt", "Fold constant expressions and inline resolvable string-decryptor calls")
				.after("ReplaceNewArray")
				.before("RegionMakerVisitor");
	}

	@Override
	public void init(RootNode root) {
		// no-op
	}

	@Override
	public boolean visit(ClassNode cls) {
		return options.isEnabled();
	}

	@Override
	public void visit(MethodNode mth) {
		if (!options.isEnabled() || mth.isNoCode() || mth.contains(AType.JADX_ERROR)) {
			return;
		}
		MethodInsnIndex insnIndex = new MethodInsnIndex(mth);
		Evaluator ev = new Evaluator(mth, keys, pureFold, insnIndex);
		ObjectEvaluator oev = new ObjectEvaluator(mth, keys, ev, pureFold, jdk, insnIndex);
		FOLD_OBJ_INVOKES.set(options.isFoldObjectReturningInvokes());
		List<String> decrypted = new ArrayList<>();
		List<RegisterArg> builtArrays = new ArrayList<>();
		boolean[] replacedAny = new boolean[1];
		for (BlockNode block : mth.getBasicBlocks()) {
			for (InsnNode insn : block.getInstructions()) {
				// Per-instruction safety net: this pass interprets adversarial obfuscated code, so an
				// unforeseen edge case in one instruction must never abort the whole class's decompile
				// (which would drop it to an error stub). A throw here means no modification was applied
				// to this insn — IR stays consistent — so we log and move on.
				try {
					replaceWrapped(mth, ev, oev, insn, decrypted, builtArrays, replacedAny);
				} catch (RuntimeException e) {
					LOG.warn("string-decrypt: skipped a wrapped fold in {} ({})", mth, e.toString());
				}
			}
			for (InsnNode insn : new ArrayList<>(block.getInstructions())) {
				try {
					InsnNode repl = tryReplace(mth, ev, oev, insn, decrypted, builtArrays, false);
					if (repl != null) {
						replacedAny[0] = true;
						BlockUtils.replaceInsn(mth, block, insn, repl);
					}
				} catch (RuntimeException e) {
					LOG.warn("string-decrypt: skipped a fold in {} ({})", mth, e.toString());
				}
			}
		}
		int folded = options.isFoldConsts() ? safeFoldIntConstants(mth, ev) : 0;
		if (options.isCleanup() && (replacedAny[0] || folded > 0 || !builtArrays.isEmpty())) {
			try {
				for (RegisterArg arrArg : builtArrays) {
					cleanupArrayBuild(mth, arrArg);
				}
				removeDeadPureInsns(mth); // drop now-unused table reads / folded-away arithmetic
				if (options.isCleanupOrphanArrays()) {
					removeDeadArrayBuilds(mth); // sweep orphan `new byte[]{...}[N] = X` statements after folds
				}
				if (options.isDeindirectReflection() && options.isCleanupReflection()) {
					removeDeadReflectiveScaffold(mth, oev); // sweep no-op reflective lookups left after de-indirection
				}
			} catch (RuntimeException e) {
				LOG.warn("string-decrypt: cleanup skipped in {} ({})", mth, e.toString());
			}
		}
		if (!decrypted.isEmpty()) {
			// Per-class/method count stays at INFO; the individual decrypted results are logged at DEBUG.
			LOG.info("string-decrypt: {} string(s) in {}", decrypted.size(), mth);
			if (LOG.isDebugEnabled()) {
				for (String s : decrypted) {
					LOG.debug("string-decrypt:   decrypted {} in {}", s, mth);
				}
			}
			if (options.isComments()) {
				mth.addInfoComment("String decrypt: " + summarizeDecrypted(decrypted));
			}
		}
		if (folded > 0) {
			LOG.debug("string-decrypt: folded {} integer constant(s) in {}", folded, mth);
		}
	}

	/** {@link #foldIntConstants} guarded so an unexpected evaluator failure can't abort the class. */
	private int safeFoldIntConstants(MethodNode mth, Evaluator ev) {
		try {
			return foldIntConstants(mth, ev);
		} catch (RuntimeException e) {
			LOG.warn("string-decrypt: integer-const folding skipped in {} ({})", mth, e.toString());
			return 0;
		}
	}

	/**
	 * Replace every compile-time-constant integer/byte/long expression with its literal value: a pure
	 * value-producing instruction whose result is constant becomes a {@code CONST} (so {@code int x =
	 * ((int) KEY[k]) ^ c} collapses to {@code int x = 1}), and any constant sub-expression nested in a
	 * non-constant context (a method argument, array index/size, {@code return}, condition, field
	 * store) is replaced in place. Soundness comes entirely from {@link Evaluator}: it only resolves
	 * genuinely compile-time values (literals, arithmetic, conversions, immutable static fields/tables,
	 * pure helper calls) and returns null for anything runtime-dependent.
	 */
	private int foldIntConstants(MethodNode mth, Evaluator ev) {
		int count = 0;
		for (BlockNode block : mth.getBasicBlocks()) {
			for (InsnNode insn : new ArrayList<>(block.getInstructions())) {
				// 1) a whole pure insn whose result is constant -> a single CONST literal
				if (isPureValueInsn(insn)) {
					RegisterArg res = insn.getResult();
					if (res != null && isFoldableIntType(res.getType())) {
						Long v = ev.evalInt(res, 0);
						if (v != null) {
							BlockUtils.replaceInsn(mth, block, insn, constInsn(insn, normalize(res.getType(), v), res.getType()));
							count++;
							continue;
						}
					}
				}
				// 2) fold any constant sub-expression in this insn's arguments (and nested ternaries)
				count += foldConstArgs(mth, ev, insn);
			}
		}
		return count;
	}

	/**
	 * Fold the maximal constant sub-expression at each argument position (recursing where not
	 * constant).
	 */
	private int foldConstArgs(MethodNode mth, Evaluator ev, InsnNode insn) {
		int count = 0;
		for (int i = 0; i < insn.getArgsCount(); i++) {
			InsnArg arg = insn.getArg(i);
			if (arg.isLiteral()) {
				continue;
			}
			ArgType type = arg.getType();
			if (isFoldableIntType(type)) {
				Long v = ev.evalInt(arg, 0);
				if (v != null) {
					InsnRemover.unbindArgUsage(mth, arg);
					insn.setArg(i, InsnArg.lit(normalize(type, v), type));
					count++;
					continue; // whole arg folded; nothing left to recurse into
				}
			}
			if (arg.isInsnWrap()) {
				count += foldConstArgs(mth, ev, ((InsnWrapArg) arg).getWrapInsn());
			}
		}
		return count;
	}

	private static InsnNode constInsn(InsnNode from, long value, ArgType type) {
		InsnNode c = new InsnNode(InsnType.CONST, 1);
		c.setResult(from.getResult());
		c.addArg(InsnArg.lit(value, type));
		c.copyAttributesFrom(from);
		return c;
	}

	/** Side-effect-free, value-producing instructions whose result can be replaced by a literal. */
	private boolean isPureValueInsn(InsnNode insn) {
		switch (insn.getType()) {
			case ARITH:
			case NEG:
			case NOT:
			case CAST:
			case SGET:
			case AGET:
				return true;
			case INVOKE:
				// only a call we proved pure (snapshotted helper) may be removed wholesale
				return keys.bodies().containsKey(((InvokeNode) insn).getCallMth().getRawFullId());
			default:
				return false;
		}
	}

	private static boolean isFoldableIntType(ArgType type) {
		return ArgType.INT.equals(type) || ArgType.BYTE.equals(type) || ArgType.SHORT.equals(type)
				|| ArgType.CHAR.equals(type) || ArgType.LONG.equals(type) || ArgType.BOOLEAN.equals(type);
	}

	/**
	 * A boolean-typed constant must render as the literal {@code false}/{@code true}, not an integer.
	 */
	private static long normalize(ArgType type, long v) {
		return ArgType.BOOLEAN.equals(type) ? (v != 0 ? 1 : 0) : v;
	}

	/**
	 * Remove side-effect-free instructions left with no uses: the key-table reads
	 * ({@code long[] jArr = TABLE;}) and the array-element/cast/arithmetic feeders that fed expressions
	 * now folded to literals. Iterates to a fixpoint so a chain (XOR ← cast ← array-get ← table-read)
	 * collapses fully.
	 */
	private static void removeDeadPureInsns(MethodNode mth) {
		boolean removedAny = true;
		while (removedAny) {
			removedAny = false;
			List<InsnNode> dead = new ArrayList<>();
			for (BlockNode block : mth.getBasicBlocks()) {
				for (InsnNode insn : block.getInstructions()) {
					if (!isDeadCandidate(insn.getType())) {
						continue;
					}
					RegisterArg result = insn.getResult();
					SSAVar sVar = result != null ? result.getSVar() : null;
					if (sVar != null && sVar.getUseList().isEmpty()) {
						dead.add(insn);
					}
				}
			}
			if (!dead.isEmpty()) {
				InsnRemover.removeAllAndUnbind(mth, dead);
				removedAny = true;
			}
		}
	}

	/**
	 * Generic post-fold sweep: any {@code new-array} / {@code filled-new-array} whose result is only
	 * written to (via {@code aput}/{@code fill-array}) and never read becomes orphaned after the
	 * folds consume the byte[] values. Remove the build along with its feeder writes, iterated to a
	 * fixpoint. Complements {@link #cleanupArrayBuild}, which only sweeps arrays explicitly tracked
	 * by the decrypt path.
	 */
	private static void removeDeadArrayBuilds(MethodNode mth) {
		boolean removedAny = true;
		while (removedAny) {
			removedAny = false;
			List<InsnNode> dead = new ArrayList<>();
			for (BlockNode block : mth.getBasicBlocks()) {
				for (InsnNode insn : block.getInstructions()) {
					InsnType t = insn.getType();
					if (t != InsnType.NEW_ARRAY && t != InsnType.FILLED_NEW_ARRAY) {
						continue;
					}
					RegisterArg result = insn.getResult();
					SSAVar sVar = result != null ? result.getSVar() : null;
					if (sVar == null) {
						continue;
					}
					List<InsnNode> feeders = new ArrayList<>();
					boolean hasReader = false;
					for (RegisterArg use : sVar.getUseList()) {
						InsnNode p = use.getParentInsn();
						if (p == null) {
							continue;
						}
						if (p.getType() == InsnType.APUT || p.getType() == InsnType.FILL_ARRAY) {
							feeders.add(p);
						} else {
							hasReader = true;
							break;
						}
					}
					if (!hasReader) {
						dead.addAll(feeders);
						dead.add(insn);
					}
				}
			}
			if (!dead.isEmpty()) {
				InsnRemover.removeAllAndUnbind(mth, dead);
				removedAny = true;
			}
		}
	}

	/**
	 * After de-indirection rewrites the reflective calls to direct ones, the obfuscator's reflective
	 * <i>scaffolding</i> is left behind as dead no-ops: statements of the form
	 * {@code SomeClass.class.getMethod("m", types).setAccessible(true);} (look a member up and discard
	 * it), and {@code Method m = SomeClass.class.getMethod(...)} handle definitions whose only consumer
	 * (the reflective invoke) is now gone. This sweeps them, iterated to a fixpoint.
	 *
	 * <p>
	 * <b>Soundness — only provably-no-op statements are removed:</b>
	 * <ul>
	 * <li>The lookup chain must be rooted at a {@code .class} literal ({@link ConstClassNode}), never
	 * {@code Class.forName(...)} — a class literal does not trigger class initialization, so the lookup
	 * has no side effect, whereas {@code forName} can run a static initializer.</li>
	 * <li>Only the pure member-lookup methods ({@code getMethod}/{@code getField}/{@code getConstructor}
	 * and {@code -Declared} variants) are folded away.</li>
	 * <li>The lookup is re-resolved through the {@link ObjectEvaluator}; removal happens only when it
	 * resolves to a real member, which proves it cannot throw {@code NoSuchMethodException} at runtime.</li>
	 * <li>Reflective {@code Method.invoke(...)} calls are <b>never</b> touched: they are the only
	 * statements that can throw {@code InvocationTargetException}, and removing one could leave a
	 * {@code catch (InvocationTargetException)} with no corresponding thrower.</li>
	 * <li>A {@code setAccessible} is removed only when its target is the inlined throwaway lookup (the
	 * handle is discarded); a {@code setAccessible} on a stored handle that is still used elsewhere is
	 * kept, since that handle's accessibility may matter for a surviving reflective use.</li>
	 * </ul>
	 */
	private void removeDeadReflectiveScaffold(MethodNode mth, ObjectEvaluator oev) {
		boolean removedAny = true;
		while (removedAny) {
			removedAny = false;
			List<InsnNode> dead = new ArrayList<>();
			for (BlockNode block : mth.getBasicBlocks()) {
				for (InsnNode insn : block.getInstructions()) {
					collectDeadLookupCluster(insn, oev, dead);
				}
			}
			if (!dead.isEmpty()) {
				InsnRemover.removeAllAndUnbind(mth, dead);
				removedAny = true;
			}
		}
	}

	/**
	 * If {@code insn} is a pure class-literal member lookup ({@code X.class.getMethod(...)}) whose result
	 * is consumed only by no-op {@code setAccessible} calls (or by nothing at all), add the lookup and
	 * those {@code setAccessible} statements to {@code dead}. The pass runs before code-shrink inlines
	 * the lookup into its consumer, so the lookup is still a distinct instruction with the
	 * {@code setAccessible} as a SSA use. See {@link #removeDeadReflectiveScaffold} for the soundness
	 * argument; the key gates are: the lookup re-resolves (proves no {@code NoSuchMethodException}); no
	 * consumer is a reflective {@code Method.invoke} (those can throw {@code InvocationTargetException}
	 * and are never touched).
	 */
	private static void collectDeadLookupCluster(InsnNode insn, ObjectEvaluator oev, List<InsnNode> dead) {
		if (!isPureClassLiteralLookup(insn) || oev.evalProducerDirect(insn, 0) == null) {
			return;
		}
		RegisterArg result = insn.getResult();
		SSAVar sVar = result != null ? result.getSVar() : null;
		if (sVar == null) {
			return;
		}
		List<InsnNode> consumers = new ArrayList<>();
		for (RegisterArg use : sVar.getUseList()) {
			InsnNode p = use.getParentInsn();
			// the handle must flow only into the receiver slot of a void, direct setAccessible call
			if (p == null || !isDirectSetAccessible(p) || use != p.getArg(0) || hasLiveResult(p)) {
				return; // any other consumer (e.g. a surviving reflective invoke) -> keep the whole cluster
			}
			consumers.add(p);
		}
		dead.add(insn);
		dead.addAll(consumers); // empty when the lookup result is already fully unused
	}

	/** True iff {@code insn} is a direct (non-reflective) {@code handle.setAccessible(bool)} call. */
	private static boolean isDirectSetAccessible(InsnNode insn) {
		if (!(insn instanceof InvokeNode)) {
			return false;
		}
		InvokeNode inv = (InvokeNode) insn;
		return inv.getInvokeType() != InvokeType.STATIC && "setAccessible".equals(inv.getCallMth().getName());
	}

	/** True iff {@code insn}'s result SSA var has at least one remaining use. */
	private static boolean hasLiveResult(InsnNode insn) {
		RegisterArg result = insn.getResult();
		SSAVar sVar = result != null ? result.getSVar() : null;
		return sVar != null && !sVar.getUseList().isEmpty();
	}

	/**
	 * True iff {@code insn} is {@code SomeClass.class.getMethod/getField/getConstructor(...)} (or a
	 * {@code -Declared} variant) — a pure, side-effect-free reflective member lookup rooted at a class
	 * literal (so no {@code Class.forName} class-initialization side effect).
	 */
	private static boolean isPureClassLiteralLookup(InsnNode insn) {
		if (!(insn instanceof InvokeNode)) {
			return false;
		}
		InvokeNode inv = (InvokeNode) insn;
		if (!"java.lang.Class".equals(inv.getCallMth().getDeclClass().getFullName())) {
			return false;
		}
		switch (inv.getCallMth().getName()) {
			case "getMethod":
			case "getDeclaredMethod":
			case "getField":
			case "getDeclaredField":
			case "getConstructor":
			case "getDeclaredConstructor":
				break;
			default:
				return false;
		}
		InsnArg clsArg = inv.getInstanceArg();
		if (clsArg == null) {
			return false;
		}
		if (clsArg.isInsnWrap()) {
			return ((InsnWrapArg) clsArg).getWrapInsn() instanceof ConstClassNode;
		}
		if (clsArg instanceof RegisterArg) {
			return ((RegisterArg) clsArg).getAssignInsn() instanceof ConstClassNode;
		}
		return false;
	}

	/**
	 * Cap the "String decrypt: …" comment at a few unique entries so heavily-obfuscated methods
	 * (hundreds of folded strings) don't produce wall-of-text comments that drown out the actual
	 * code. The full list lives in the debug log; the comment is for quick orientation.
	 */
	private static String summarizeDecrypted(List<String> decrypted) {
		final int MAX_ENTRIES = 12;
		final int MAX_LEN = 600;
		List<String> uniq = new ArrayList<>();
		for (String e : decrypted) {
			if (!uniq.contains(e)) {
				uniq.add(e);
				if (uniq.size() >= MAX_ENTRIES) {
					break;
				}
			}
		}
		String joined = String.join("; ", uniq);
		if (joined.length() > MAX_LEN) {
			joined = joined.substring(0, MAX_LEN) + "…";
		}
		int extra = decrypted.size() - uniq.size();
		if (extra > 0) {
			joined += " (+" + extra + " more)";
		}
		return joined;
	}

	/** Side-effect-free reads/arithmetic that are safe to drop once their result is unused. */
	private static boolean isDeadCandidate(InsnType type) {
		switch (type) {
			case SGET:
			case AGET:
			case CAST:
			case NEG:
			case NOT:
			case ARITH:
				return true;
			default:
				return false;
		}
	}

	private void replaceWrapped(MethodNode mth, Evaluator ev, ObjectEvaluator oev, InsnNode insn,
			List<String> decrypted, List<RegisterArg> builtArrays, boolean[] replacedAny) {
		boolean outerIsLookup = isReflectiveNameLookupInvoke(insn);
		for (int i = 0; i < insn.getArgsCount(); i++) {
			InsnArg arg = insn.getArg(i);
			if (arg.isInsnWrap()) {
				InsnNode wrapInsn = ((InsnWrapArg) arg).getWrapInsn();
				replaceWrapped(mth, ev, oev, wrapInsn, decrypted, builtArrays, replacedAny);
				InsnNode repl = tryReplace(mth, ev, oev, wrapInsn, decrypted, builtArrays, outerIsLookup);
				if (repl != null) {
					replacedAny[0] = true;
					InsnRemover.unbindArgUsage(mth, arg);
					// A wrapped (inlined) value flows through the wrap, not a result register: the consumer
					// owns it. Drop any result the resolver copied from the original insn so the following
					// rebind doesn't re-point that SSA var's assignment onto a node that has no slot in the
					// block. (`ReplacementFactory.copyReplacementMetadata` sets a result for the top-level
					// install path; here it must not survive.)
					repl.setResult(null);
					insn.setArg(i, InsnArg.wrapInsnIntoArg(repl));
					// Bind the replacement's register args into their SSAVar use-lists — the symmetric step
					// to BlockUtils.replaceInsn's rebindArgs() on the top-level path. Without this a
					// replacement carrying RegisterArgs (e.g. the de-indirected direct call synthesized by
					// DeindirectionResolver) leaves stale use-lists, corrupting later type inference.
					// Recurses into nested InsnWrapArgs, so a boxed call (Integer.valueOf(parseInt(s))) binds
					// fully in one pass. Harmless for the const folders, whose nodes carry no register args.
					repl.rebindArgs();
				}
			}
		}
	}

	private static boolean isReflectiveNameLookupInvoke(InsnNode insn) {
		if (!(insn instanceof InvokeNode)) {
			return false;
		}
		return isReflectiveNameLookup(((InvokeNode) insn).getCallMth());
	}

	/**
	 * Run the {@link #resolvers} pipeline against one candidate instruction and take the first non-null
	 * replacement. The built-in resolvers, in order, are: (1) string-decrypt of an AES-style decryptor
	 * call, (2) folding any representable constant value via the {@link ObjectEvaluator} (JDK chains,
	 * arrays, {@code Class<?>}, boxed scalars, pure app helpers), (3) the legacy integer-args-only
	 * {@link PureFold} string path. Additional strategies plug in as further {@link Resolver}s.
	 */
	private @Nullable InsnNode tryReplace(MethodNode mth, Evaluator ev, ObjectEvaluator oev, InsnNode insn,
			List<String> decrypted, List<RegisterArg> builtArrays, boolean consumerIsLookup) {
		ResolveContext ctx = new ResolveContext(mth, ev, oev, insn, decrypted, builtArrays, consumerIsLookup);
		for (Resolver resolver : resolvers) {
			InsnNode repl = resolver.resolve(ctx);
			if (repl != null) {
				return repl;
			}
		}
		return null;
	}

	private @Nullable InsnNode tryDecodeBase64StringHelper(ObjectEvaluator oev, InsnNode insn, List<String> decrypted) {
		if (!(insn instanceof InvokeNode)) {
			return null;
		}
		InvokeNode inv = (InvokeNode) insn;
		if (!keys.base64StringHelpers().contains(inv.getCallMth().getRawFullId())) {
			return null;
		}
		int argOffset = inv.getFirstArgOffset();
		if (inv.getArgsCount() <= argOffset) {
			return null;
		}
		Object arg = oev.evalObject(inv.getArg(argOffset), 0);
		if (!(arg instanceof String)) {
			return null;
		}
		String decoded = Eval.tryDecodeBase64String((String) arg);
		if (decoded == null || decoded.isEmpty()) {
			return null;
		}
		decrypted.add('"' + decoded.replace("\\", "\\\\").replace("\"", "\\\"") + '"');
		ConstStringNode node = new ConstStringNode(decoded);
		RegisterArg result = insn.getResult();
		if (result != null) {
			node.setResult(result.duplicate());
		}
		return node;
	}

	/**
	 * Fold the RC4-like helper used by some dumped dynamic dex payloads:
	 * {@code b(prefix12 + base64(hexRc4Ciphertext))}. The helper body constructs
	 * {@code new sadness...a.a(prefix.getBytes()).a(hexBytes)}; modelling arbitrary app objects with
	 * mutable cipher state would be too broad, but this exact carrier is deterministic and common.
	 */
	private @Nullable InsnNode tryDecodePrefixedRc4String(ObjectEvaluator oev, InsnNode insn, List<String> decrypted) {
		if (!(insn instanceof InvokeNode)) {
			return null;
		}
		InvokeNode inv = (InvokeNode) insn;
		ArgType ret = inv.getCallMth().getReturnType();
		if (ret == null || !ret.isObject() || !"java.lang.String".equals(ret.getObject()) || inv.getArgsCount() < 1) {
			return null;
		}
		if (!"b".equals(inv.getCallMth().getName())) {
			return null;
		}
		Object arg = oev.evalObject(inv.getArg(inv.getFirstArgOffset()), 0);
		if (!(arg instanceof String)) {
			return null;
		}
		String decoded = Eval.tryDecodePrefixedRc4((String) arg);
		if (decoded == null || !Eval.isPrintable(decoded)) {
			return null;
		}
		decrypted.add('"' + decoded.replace("\\", "\\\\").replace("\"", "\\\"") + '"');
		ConstStringNode node = new ConstStringNode(decoded);
		RegisterArg result = insn.getResult();
		if (result != null) {
			node.setResult(result.duplicate());
		}
		return node;
	}

	/**
	 * Replace any instruction whose result is a representable compile-time constant. Covers:
	 * strings, primitive/boxed scalars, primitive/object arrays (including {@code char[]} and
	 * {@code Class<?>[]}), {@code Class<?>} constants, and {@code CHECK_CAST}/{@code CAST} wrappers
	 * around reflective bridges. The {@link ObjectEvaluator} carries the actual value through the
	 * producer graph; this method is the type-safe boundary that converts that value back into jadx
	 * IR only when the value is compatible with the original result type.
	 */
	private @Nullable InsnNode tryFoldConstValue(ObjectEvaluator oev, InsnNode insn,
			List<String> decrypted, boolean consumerIsLookup) {
		if (insn instanceof BaseInvokeNode) {
			BaseInvokeNode inv = (BaseInvokeNode) insn;
			MethodInfo callMth = inv.getCallMth();
			if (callMth != null && keys.decryptors().contains(callMth.getRawFullId())) {
				return null; // an AES decryptor: owned by the decrypt path
			}
		}
		ArgType targetType = replacementTargetType(insn);
		if (!canYieldReplaceableConst(insn, targetType)) {
			return null;
		}
		Object folded = oev.evalProducerDirect(insn, 0);
		if (folded == null) {
			return null;
		}
		boolean suppressLookup = options.isSuppressDecoyLookups()
				&& (consumerIsLookup || isConsumedByReflectiveNameLookup(insn));
		InsnNode repl = ReplacementFactory.makeReplacementInsn(insn, folded, targetType, decrypted, suppressLookup);
		if (repl != null && folded instanceof byte[] && options.isComments()) {
			// A folded byte[] (a decoded key, a decryptor's intermediate, ...) renders as an opaque
			// `new byte[]{...}` literal; when those bytes are printable text, surface the string form so
			// the meaning is visible without decoding the bytes by hand.
			annotateBytesAsString(repl, (byte[]) folded, decrypted);
		}
		return repl;
	}

	/**
	 * Surface the string form of a folded {@code byte[]} when its bytes decode to printable UTF-8 text.
	 * Adds an inline {@code as string: "…"} comment on the folded node (renders when the fold is a
	 * statement-level {@code byte[] x = …}) and records it in {@code decrypted} so it always appears in
	 * the method's "String decrypt:" summary comment and the DEBUG log — even when the byte[] is an
	 * inlined sub-expression (a {@code return}/argument) where an instruction comment wouldn't render.
	 * Nothing is added for binary (non-text) byte arrays, where a string form would be noise.
	 */
	private static void annotateBytesAsString(InsnNode node, byte[] bytes, List<String> decrypted) {
		if (bytes.length == 0) {
			return;
		}
		String text = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
		if (!Eval.isPrintable(text)) {
			return;
		}
		String escaped = text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
		node.addAttr(AType.CODE_COMMENTS, new CodeComment("as string: \"" + escaped + "\"", CommentStyle.BLOCK_CONDENSED));
		decrypted.add("byte[] \"" + escaped + "\"");
	}

	/**
	 * Inspect the SSA uses of {@code insn}'s result: if every use is an argument to one of the
	 * reflective name-lookup calls ({@code Class.forName}, {@code getMethod}, {@code getField},
	 * {@code getConstructor}, and {@code -Declared} variants), return true. A folded non-printable
	 * string flowing into one of these calls is dead at runtime (no Java identifier contains
	 * control characters or U+FFFD), so suppressing the fold leaves the more readable un-folded
	 * expression in source instead of emitting an obviously-broken literal.
	 */
	private static boolean isConsumedByReflectiveNameLookup(InsnNode insn) {
		RegisterArg result = insn.getResult();
		SSAVar sVar = result != null ? result.getSVar() : null;
		if (sVar == null) {
			// no SSA var: the producer is inline-wrapped into its consumer's arg. The consumer is
			// the parent of this insn in the wrap tree — walk it via getParentInsn on the result-less
			// insn. If absent we can't tell; default to "not a lookup" so the fold still fires.
			return false;
		}
		if (sVar.getUseList().isEmpty()) {
			return false;
		}
		for (RegisterArg use : sVar.getUseList()) {
			InsnNode parent = use.getParentInsn();
			if (parent == null) {
				return false;
			}
			parent = unwrapToTopInvoke(parent);
			if (!(parent instanceof InvokeNode)) {
				return false;
			}
			MethodInfo m = ((InvokeNode) parent).getCallMth();
			if (!isReflectiveNameLookup(m)) {
				return false;
			}
		}
		return true;
	}

	/** A consumer wrap chain may bury the actual invoke node a few levels deep; walk up to it. */
	private static InsnNode unwrapToTopInvoke(InsnNode insn) {
		InsnNode cur = insn;
		// CHECK_CAST / CAST / MOVE wrap their producer; their parent is the real consumer. But here
		// we're walking the OUTER direction from a use — `use.getParentInsn()` is already the consumer
		// of our result. If the consumer is itself wrapped (rare for invokes), there's no plain
		// up-walk in jadx IR. Treat the consumer as-is.
		return cur;
	}

	private static boolean isReflectiveNameLookup(MethodInfo m) {
		if (m == null) {
			return false;
		}
		String cls = m.getDeclClass().getFullName();
		String name = m.getName();
		if ("java.lang.Class".equals(cls)) {
			switch (name) {
				case "forName":
				case "getMethod":
				case "getDeclaredMethod":
				case "getField":
				case "getDeclaredField":
				case "getConstructor":
				case "getDeclaredConstructor":
					return true;
				default:
					return false;
			}
		}
		return false;
	}

	/** Thread-locally captures the current option value so static candidate checks can read it. */
	private static final ThreadLocal<Boolean> FOLD_OBJ_INVOKES = ThreadLocal.withInitial(() -> Boolean.TRUE);

	private static boolean foldObjectReturningInvokesAllowed() {
		return FOLD_OBJ_INVOKES.get();
	}

	/** Result type that the replacement must remain assignable to. */
	private static @Nullable ArgType replacementTargetType(InsnNode insn) {
		RegisterArg result = insn.getResult();
		if (result != null) {
			return result.getType();
		}
		if (insn instanceof ConstructorInsn) {
			return ((ConstructorInsn) insn).getCallMth().getDeclClass().getType();
		}
		if (insn instanceof InvokeNode) {
			return ((InvokeNode) insn).getCallMth().getReturnType();
		}
		return null;
	}

	/**
	 * True iff this instruction is a value producer worth attempting through the object evaluator.
	 * The actual replacement is still gated by {@link ReplacementFactory#makeReplacementInsn}, so broad candidates here
	 * are cheap: non-constant or unrepresentable values simply return null.
	 */
	private static boolean canYieldReplaceableConst(InsnNode insn, @Nullable ArgType targetType) {
		if (targetType != null && ArgType.VOID.equals(targetType)) {
			return false;
		}
		switch (insn.getType()) {
			case INVOKE: {
				ArgType ret = ((InvokeNode) insn).getCallMth().getReturnType();
				if (ret == null || ArgType.VOID.equals(ret)) {
					return false;
				}
				if (ret.isPrimitive() || ret.isArray()) {
					return true;
				}
				if (!ret.isObject()) {
					return false;
				}
				String typeName = ret.getObject();
				if ("java.lang.Object".equals(typeName) || "java.lang.CharSequence".equals(typeName)) {
					return foldObjectReturningInvokesAllowed();
				}
				return true;
			}
			case CONSTRUCTOR:
			case CHECK_CAST:
			case CAST:
			case MOVE:
				return true;
			default:
				return false;
		}
	}


	/**
	 * Inline a call to a pure, fully-interpretable {@code String}-returning static helper
	 * with constant arguments (e.g. a single-char {@code String.valueOf((char) i)} helper). Non-pure
	 * helpers (Random, I/O, loops) are not interpretable, so {@link PureFold} returns null and the
	 * call is left unchanged.
	 */
	private @Nullable InsnNode tryFoldPureString(MethodNode mth, Evaluator ev, InsnNode insn,
			List<String> decrypted) {
		if (!(insn instanceof InvokeNode)) {
			return null;
		}
		InvokeNode inv = (InvokeNode) insn;
		ArgType ret = inv.getCallMth().getReturnType();
		if (ret == null || !ret.isObject() || !"java.lang.String".equals(ret.getObject())) {
			return null;
		}
		if (keys.decryptors().contains(inv.getCallMth().getRawFullId())) {
			return null; // an AES decryptor: handled by the decrypt path
		}
		int argc = inv.getArgsCount();
		Object[] args = new Object[argc];
		for (int i = 0; i < argc; i++) {
			Long a = ev.evalInt(inv.getArg(i), 0);
			if (a == null) {
				return null; // non-constant (or non-integral) arg
			}
			args[i] = a;
		}
		Object folded = pureFold.foldCall(inv, args, 0);
		if (!(folded instanceof String) || !Eval.isPrintable((String) folded)) {
			return null;
		}
		String text = (String) folded;
		// each decrypted value is logged centrally at DEBUG in visit(); just record it here
		decrypted.add('"' + text.replace("\\", "\\\\").replace("\"", "\\\"") + '"');
		ConstStringNode node = new ConstStringNode(text);
		RegisterArg result = insn.getResult();
		if (result != null) {
			node.setResult(result.duplicate());
		}
		return node;
	}

	/**
	 * @return a replacement const-string instruction if {@code insn} is a resolvable decrypt call,
	 *         else null
	 */
	private @Nullable InsnNode tryDecrypt(MethodNode mth, Evaluator ev, InsnNode insn,
			List<String> decrypted, List<RegisterArg> builtArrays) {
		if (!(insn instanceof InvokeNode)) {
			return null;
		}
		InvokeNode inv = (InvokeNode) insn;
		if (!isDecryptCall(inv.getCallMth().getRawFullId())) {
			return null;
		}
		int argOffset = inv.getFirstArgOffset();
		if (inv.getArgsCount() <= argOffset) {
			return null;
		}
		InsnArg blobArg = inv.getArg(argOffset);
		byte[] bytes = ev.resolveByteArray(blobArg);
		if (bytes == null) {
			return null;
		}
		String text = Eval.tryDecrypt(bytes, options.getKeyTailLen(), options.getCipher());
		if (text == null || !Eval.isPrintable(text)) {
			return null;
		}
		decrypted.add('"' + text.replace("\\", "\\\\").replace("\"", "\\\"") + '"');
		if (blobArg instanceof RegisterArg) {
			builtArrays.add((RegisterArg) blobArg); // remember the array var to clean up afterwards
		}
		ConstStringNode node = new ConstStringNode(text);
		RegisterArg result = insn.getResult();
		if (result != null) {
			node.setResult(result.duplicate());
		}
		return node;
	}

	/**
	 * Remove the instructions consumed only to build a decrypted byte[]: its {@code new-array},
	 * every {@code aput} into it, and any {@code fill-array-data}. Bails if the array is still read
	 * elsewhere. The now-dead constant/table-read feeders are swept by the later CodeShrink pass.
	 */
	private static void cleanupArrayBuild(MethodNode mth, RegisterArg arrArg) {
		SSAVar sVar = arrArg.getSVar();
		if (sVar == null) {
			return;
		}
		InsnNode assign = arrArg.getAssignInsn();
		if (assign == null) {
			return; // already removed (e.g. array reused by two replaced calls)
		}
		InsnType at = assign.getType();
		if (at != InsnType.NEW_ARRAY && at != InsnType.FILLED_NEW_ARRAY) {
			return;
		}
		List<InsnNode> toRemove = new ArrayList<>();
		for (RegisterArg use : sVar.getUseList()) {
			InsnNode p = use.getParentInsn();
			if (p == null) {
				continue;
			}
			if (p.getType() == InsnType.APUT || p.getType() == InsnType.FILL_ARRAY) {
				toRemove.add(p); // store into this array: a consumed build instruction
			} else {
				return; // array is still read somewhere -> keep the whole build
			}
		}
		toRemove.add(assign);
		InsnRemover.removeAllAndUnbind(mth, toRemove);
	}

	private boolean isDecryptCall(String id) {
		if (keys.decryptors().contains(id)) {
			return true; // auto-detected string decryptor (precise, name-agnostic)
		}
		String cls = options.getDecryptorClass();
		if (cls.isEmpty()) {
			return false; // no explicit class configured -> rely on auto-detection only
		}
		return id.endsWith(options.getDecryptorDesc())
				&& (id.contains('.' + cls + '.') || id.startsWith(cls + '.'));
	}
}
