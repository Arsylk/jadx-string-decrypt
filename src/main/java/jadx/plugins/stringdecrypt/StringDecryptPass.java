package jadx.plugins.stringdecrypt;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jadx.api.plugins.pass.JadxPassInfo;
import jadx.api.plugins.pass.impl.OrderedJadxPassInfo;
import jadx.api.plugins.pass.types.JadxDecompilePass;
import jadx.core.dex.attributes.AType;
import jadx.core.dex.info.MethodInfo;
import jadx.core.dex.instructions.BaseInvokeNode;
import jadx.core.dex.instructions.ConstStringNode;
import jadx.core.dex.instructions.IndexInsnNode;
import jadx.core.dex.instructions.InsnType;
import jadx.core.dex.instructions.InvokeNode;
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

	public StringDecryptPass(StringDecryptOptions options, KeyData keys) {
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
		// behaviour is identical to the former hand-rolled if-chain; future strategies (reflective
		// de-indirection, script pipelines) are inserted here rather than edited into tryReplace.
		this.resolvers = List.of(
				ctx -> options.isDecryptStrings() ? tryDecrypt(ctx.mth, ctx.ev, ctx.insn, ctx.decrypted, ctx.builtArrays) : null,
				ctx -> options.isFoldHelperCalls() ? tryFoldConstValue(ctx.oev, ctx.insn, ctx.decrypted, ctx.consumerIsLookup) : null,
				ctx -> options.isFoldHelperCalls() ? tryFoldPureString(ctx.mth, ctx.ev, ctx.insn, ctx.decrypted) : null,
				// Runs last: only reflective calls the folders left un-constant get rewritten to direct calls.
				new DeindirectionResolver(options));
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
		Evaluator ev = new Evaluator(mth, keys);
		ObjectEvaluator oev = new ObjectEvaluator(mth, keys, ev, pureFold, jdk);
		FOLD_OBJ_INVOKES.set(options.isFoldObjectReturningInvokes());
		List<String> decrypted = new ArrayList<>();
		List<RegisterArg> builtArrays = new ArrayList<>();
		for (BlockNode block : mth.getBasicBlocks()) {
			for (InsnNode insn : block.getInstructions()) {
				replaceWrapped(mth, ev, oev, insn, decrypted, builtArrays);
			}
			for (InsnNode insn : new ArrayList<>(block.getInstructions())) {
				InsnNode repl = tryReplace(mth, ev, oev, insn, decrypted, builtArrays, false);
				if (repl != null) {
					BlockUtils.replaceInsn(mth, block, insn, repl);
				}
			}
		}
		int folded = options.isFoldConsts() ? foldIntConstants(mth, ev) : 0;
		if (options.isCleanup()) {
			for (RegisterArg arrArg : builtArrays) {
				cleanupArrayBuild(mth, arrArg);
			}
			removeDeadPureInsns(mth); // drop now-unused table reads / folded-away arithmetic
			if (options.isCleanupOrphanArrays()) {
				removeDeadArrayBuilds(mth); // sweep orphan `new byte[]{...}[N] = X` statements after folds
			}
		}
		if (!decrypted.isEmpty()) {
			LOG.info("string-decrypt: {} string(s) in {}", decrypted.size(), mth);
			if (options.isComments()) {
				mth.addInfoComment("String decrypt: " + summarizeDecrypted(decrypted));
			}
		}
		if (folded > 0) {
			LOG.debug("string-decrypt: folded {} integer constant(s) in {}", folded, mth);
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
			List<String> decrypted, List<RegisterArg> builtArrays) {
		boolean outerIsLookup = isReflectiveNameLookupInvoke(insn);
		for (int i = 0; i < insn.getArgsCount(); i++) {
			InsnArg arg = insn.getArg(i);
			if (arg.isInsnWrap()) {
				InsnNode wrapInsn = ((InsnWrapArg) arg).getWrapInsn();
				replaceWrapped(mth, ev, oev, wrapInsn, decrypted, builtArrays);
				InsnNode repl = tryReplace(mth, ev, oev, wrapInsn, decrypted, builtArrays, outerIsLookup);
				if (repl != null) {
					InsnRemover.unbindArgUsage(mth, arg);
					insn.setArg(i, InsnArg.wrapInsnIntoArg(repl));
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
		return ReplacementFactory.makeReplacementInsn(insn, folded, targetType, decrypted, suppressLookup);
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
		LOG.debug("fold {} -> \"{}\" in {}", inv.getCallMth().getShortId(), text, mth);
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
		LOG.debug("decrypt({} bytes) -> \"{}\" in {}", bytes.length, text, mth);
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
