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
import jadx.core.dex.instructions.ConstStringNode;
import jadx.core.dex.instructions.InsnType;
import jadx.core.dex.instructions.InvokeNode;
import jadx.core.dex.instructions.args.ArgType;
import jadx.core.dex.instructions.args.InsnArg;
import jadx.core.dex.instructions.args.InsnWrapArg;
import jadx.core.dex.instructions.args.RegisterArg;
import jadx.core.dex.instructions.args.SSAVar;
import jadx.core.dex.nodes.BlockNode;
import jadx.core.dex.nodes.ClassNode;
import jadx.core.dex.nodes.InsnNode;
import jadx.core.dex.nodes.MethodNode;
import jadx.core.dex.nodes.RootNode;
import jadx.core.utils.BlockUtils;
import jadx.core.utils.InsnRemover;

/**
 * Finds resolvable decrypt calls (top-level or wrapped into another instruction's argument),
 * replaces them with the decrypted constant string, and removes the now-dead instructions that were
 * consumed only to build the byte[] argument.
 */
public class StringDecryptPass implements JadxDecompilePass {

	private static final Logger LOG = LoggerFactory.getLogger(StringDecryptPass.class);

	private final StringDecryptOptions options;
	private final KeyData keys;
	private final PureFold pureFold;

	public StringDecryptPass(StringDecryptOptions options, KeyData keys) {
		this.options = options;
		this.keys = keys;
		this.pureFold = new PureFold(keys);
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
		List<String> decrypted = new ArrayList<>();
		List<RegisterArg> builtArrays = new ArrayList<>();
		for (BlockNode block : mth.getBasicBlocks()) {
			for (InsnNode insn : block.getInstructions()) {
				replaceWrapped(mth, ev, insn, decrypted, builtArrays);
			}
			for (InsnNode insn : new ArrayList<>(block.getInstructions())) {
				ConstStringNode repl = tryReplace(mth, ev, insn, decrypted, builtArrays);
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
		}
		if (!decrypted.isEmpty()) {
			LOG.info("string-decrypt: {} string(s) in {}", decrypted.size(), mth);
			if (options.isComments()) {
				mth.addInfoComment("String decrypt: " + String.join("; ", decrypted));
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

	private void replaceWrapped(MethodNode mth, Evaluator ev, InsnNode insn,
			List<String> decrypted, List<RegisterArg> builtArrays) {
		for (int i = 0; i < insn.getArgsCount(); i++) {
			InsnArg arg = insn.getArg(i);
			if (arg.isInsnWrap()) {
				InsnNode wrapInsn = ((InsnWrapArg) arg).getWrapInsn();
				replaceWrapped(mth, ev, wrapInsn, decrypted, builtArrays);
				ConstStringNode repl = tryReplace(mth, ev, wrapInsn, decrypted, builtArrays);
				if (repl != null) {
					InsnRemover.unbindArgUsage(mth, arg);
					insn.setArg(i, InsnArg.wrapInsnIntoArg(repl));
				}
			}
		}
	}

	/** Try the string-decrypt path first, then interprocedural pure String-returning helper folding. */
	private @Nullable ConstStringNode tryReplace(MethodNode mth, Evaluator ev, InsnNode insn,
			List<String> decrypted, List<RegisterArg> builtArrays) {
		ConstStringNode repl = options.isDecryptStrings() ? tryDecrypt(mth, ev, insn, decrypted, builtArrays) : null;
		if (repl == null && options.isFoldHelperCalls()) {
			repl = tryFoldPureString(mth, ev, insn, decrypted);
		}
		return repl;
	}

	/**
	 * Prototype: inline a call to a pure, fully-interpretable {@code String}-returning static helper
	 * with constant arguments (e.g. a single-char {@code String.valueOf((char) i)} helper). Non-pure
	 * helpers (Random, I/O, loops) are not interpretable, so {@link PureFold} returns null and the
	 * call is left unchanged.
	 */
	private @Nullable ConstStringNode tryFoldPureString(MethodNode mth, Evaluator ev, InsnNode insn,
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
		Object folded = pureFold.foldCall(mth.root(), inv, args, 0);
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
	 * @return a replacement const-string node if {@code insn} is a resolvable decrypt call, else null
	 */
	private @Nullable ConstStringNode tryDecrypt(MethodNode mth, Evaluator ev, InsnNode insn,
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
