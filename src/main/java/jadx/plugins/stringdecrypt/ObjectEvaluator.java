package jadx.plugins.stringdecrypt;

import java.util.IdentityHashMap;

import org.jetbrains.annotations.Nullable;

import jadx.core.dex.info.MethodInfo;
import jadx.core.dex.instructions.BaseInvokeNode;
import jadx.core.dex.instructions.ConstStringNode;
import jadx.core.dex.instructions.FilledNewArrayNode;
import jadx.core.dex.instructions.InsnType;
import jadx.core.dex.instructions.InvokeNode;
import jadx.core.dex.instructions.args.ArgType;
import jadx.core.dex.instructions.args.InsnArg;
import jadx.core.dex.instructions.args.InsnWrapArg;
import jadx.core.dex.instructions.args.LiteralArg;
import jadx.core.dex.instructions.args.RegisterArg;
import jadx.core.dex.instructions.args.SSAVar;
import jadx.core.dex.instructions.mods.ConstructorInsn;
import jadx.core.dex.nodes.BlockNode;
import jadx.core.dex.nodes.InsnNode;
import jadx.core.dex.nodes.MethodNode;
import jadx.plugins.stringdecrypt.eval.TypeMap;
import jadx.plugins.stringdecrypt.jdk.JdkInterpreter;

/**
 * Per-method evaluator that folds an IR producer chain to a constant <em>Java object</em>: the
 * counterpart to {@link Evaluator}, whose remit is integral expressions. Handles the obfuscation
 * patterns built on the JDK stdlib — e.g. {@code new String(new BigInteger(arr).divide(...
 * ).toByteArray(), "UTF-8")} or {@code new StringBuilder("abc").reverse().toString()} — by walking
 * each producer, dispatching {@code INVOKE}/{@code CONSTRUCTOR} through {@link JdkInterpreter}, and
 * letting concrete handlers compute on real Java instances.
 *
 * <p>
 * Soundness: every fold ultimately returns either {@code null} (refuse — call is left as-is) or a
 * value the corresponding {@link jadx.plugins.stringdecrypt.jdk.JdkClassHandler} judged pure.
 * Helpers (loops, branches, non-stdlib calls) and runtime-mutated statics already refuse via
 * {@link Evaluator}'s gates; this class never relaxes them.
 *
 * <p>
 * Cycle break: each producer is tentatively cached as {@code null} on entry, so a self-referential
 * SSA chain returns {@code null} (non-constant) instead of recursing forever.
 */
final class ObjectEvaluator {

	private static final int MAX_DEPTH = 64;
	private static final Object NULL_SENTINEL = new Object();

	private final MethodNode mth;
	private final KeyData keys;
	private final Evaluator intEval;
	private final PureFold helperFold;
	private final JdkInterpreter jdk;
	private final IdentityHashMap<InsnNode, Object> cache = new IdentityHashMap<>();

	ObjectEvaluator(MethodNode mth, KeyData keys, Evaluator intEval, PureFold helperFold, JdkInterpreter jdk) {
		this.mth = mth;
		this.keys = keys;
		this.intEval = intEval;
		this.helperFold = helperFold;
		this.jdk = jdk;
	}

	/**
	 * Evaluate {@code arg} to its constant value. Returns {@link Long} for integral types, a real
	 * {@code String} / {@code byte[]} / {@code char[]} / {@code BigInteger} / {@code StringBuilder} /
	 * etc. for object types, or {@code null} if the value isn't a compile-time constant we can fold.
	 */
	@Nullable
	Object evalObject(InsnArg arg, int depth) {
		if (arg == null || depth > MAX_DEPTH) {
			return null;
		}
		// Integer-like (incl. wrapped arithmetic): always delegate to the int evaluator. Routes ARITH,
		// AGET, CAST, NEG, NOT, SGET, etc. through the well-tested evalInt rather than re-implementing
		// them in evalProducer's object-side switch.
		if (TypeMap.isIntegerLike(arg.getType())) {
			Long v = intEval.evalInt(arg, depth);
			return v;
		}
		if (arg instanceof LiteralArg) {
			// A literal 0 in an object slot is the null reference (jadx encodes null as CONST 0 with
			// the slot's object type). Return the NULL_REF sentinel so collectArgs distinguishes
			// "the value is null" from "couldn't resolve" — both are otherwise indistinguishable.
			if (((LiteralArg) arg).getLiteral() == 0L && arg.getType() != null
					&& (arg.getType().isObject() || arg.getType().isArray())) {
				return TypeMap.NULL_REF;
			}
			return null; // float/double literal: not relevant to current fold targets
		}
		if (arg.isInsnWrap()) {
			return evalProducer(((InsnWrapArg) arg).getWrapInsn(), depth);
		}
		if (arg instanceof RegisterArg) {
			InsnNode p = ((RegisterArg) arg).getAssignInsn();
			if (p == null) {
				return null;
			}
			Object r = evalProducer(p, depth);
			// Object-typed CONST 0 producers (null in source): same NULL_REF sentinel.
			if (r == null && p.getType() == InsnType.CONST && p.getArgsCount() > 0
					&& p.getArg(0) instanceof LiteralArg
					&& ((LiteralArg) p.getArg(0)).getLiteral() == 0L
					&& arg.getType() != null && (arg.getType().isObject() || arg.getType().isArray())) {
				return TypeMap.NULL_REF;
			}
			return r;
		}
		return null;
	}

	/**
	 * Evaluate a producer instruction directly. Used by the pass for CONSTRUCTOR insns (whose own
	 * result reg is null pre-merge) and for the wrap-walking fold attempts on String-typed calls
	 * where we want to start from the insn itself rather than its (possibly missing) result reg.
	 */
	@Nullable
	Object evalProducerDirect(InsnNode insn, int depth) {
		return evalProducer(insn, depth);
	}

	@Nullable
	private Object evalProducer(InsnNode insn, int depth) {
		if (cache.containsKey(insn)) {
			Object c = cache.get(insn);
			return c == NULL_SENTINEL ? null : c;
		}
		cache.put(insn, NULL_SENTINEL); // tentative: any reentry sees "not a constant"
		Object r = evalProducerImpl(insn, depth);
		cache.put(insn, r == null ? NULL_SENTINEL : r);
		return r;
	}

	@Nullable
	private Object evalProducerImpl(InsnNode insn, int depth) {
		RegisterArg result = insn.getResult();
		// SGET on a public-static-final field of a whitelisted JDK class (e.g. Integer.TYPE,
		// Long.TYPE, StandardCharsets.UTF_8) resolves to the actual constant. Anything else refuses.
		if (insn.getType() == InsnType.SGET) {
			return resolveStaticField(insn);
		}
		switch (insn.getType()) {
			case CONST_STR:
				return ((ConstStringNode) insn).getString();
			case MOVE:
			case CAST:
			case CHECK_CAST:
				return evalObject(insn.getArg(0), depth + 1);
			case NEW_ARRAY:
			case FILLED_NEW_ARRAY:
			case FILL_ARRAY:
				return resolveArrayProducer(result, insn, depth);
			case INVOKE: {
				InvokeNode inv = (InvokeNode) insn;
				return foldCall(inv.getCallMth(), inv, depth);
			}
			case CONSTRUCTOR: {
				ConstructorInsn cons = (ConstructorInsn) insn;
				return foldCall(cons.getCallMth(), cons, depth);
			}
			default:
				return null;
		}
	}

	/**
	 * Resolve a register holding any kind of constant array. Integer-element arrays go through the
	 * well-tested {@link Evaluator#resolveByteArray} / {@link Evaluator#resolveLongArray} resolvers;
	 * object-element arrays ({@code String[]}, {@code Class[]}, {@code Object[]}, ...) are
	 * reconstructed here by walking the {@code FILLED_NEW_ARRAY} args or by following the
	 * {@code NEW_ARRAY} + {@code APUT} chain and evaluating each element via {@link #evalObject}.
	 */
	/**
	 * Resolve a register holding any kind of constant array. {@code arrReg} may be null when the
	 * array is constructed inline (a wrap arg without a named result reg) — in that case we
	 * recover the element type from the {@link FilledNewArrayNode} itself and skip the SSA-driven
	 * APUT scan (no APUTs can target an unnamed temp anyway).
	 */
	@Nullable
	private Object resolveArrayProducer(@Nullable RegisterArg arrReg, InsnNode assignInsn, int depth) {
		ArgType type = arrReg != null ? arrReg.getType() : null;
		ArgType el = type != null && type.isArray() ? type.getArrayElement() : null;
		if (el == null && assignInsn instanceof FilledNewArrayNode) {
			el = ((FilledNewArrayNode) assignInsn).getElemType();
		}
		if (el == null && assignInsn.getType() == InsnType.NEW_ARRAY) {
			ArgType arrayType = ((jadx.core.dex.instructions.NewArrayNode) assignInsn).getArrayType();
			if (arrayType != null) {
				el = arrayType.getArrayElement();
			}
		}
		if (el == null) {
			return null;
		}
		if (Eval.isIntegral(el)) {
			return arrReg != null ? resolveIntegralArray(arrReg, depth) : resolveIntegralArrayFromInsn(assignInsn, el);
		}
		// Object-element array
		Class<?> javaEl = TypeMap.toClass(el);
		if (javaEl == null) {
			javaEl = Object.class;
		}
		if (assignInsn instanceof FilledNewArrayNode) {
			FilledNewArrayNode fa = (FilledNewArrayNode) assignInsn;
			int n = fa.getArgsCount();
			Object[] out = (Object[]) java.lang.reflect.Array.newInstance(javaEl, n);
			for (int i = 0; i < n; i++) {
				out[i] = evalObject(fa.getArg(i), depth + 1);
			}
			return out;
		}
		if (assignInsn.getType() == InsnType.NEW_ARRAY) {
			Long size = intEval.evalInt(assignInsn.getArg(0), 0);
			if (size == null || size < 0 || size > keys.maxArraySize()) {
				return null;
			}
			Object[] out = (Object[]) java.lang.reflect.Array.newInstance(javaEl, size.intValue());
			if (arrReg == null || arrReg.getSVar() == null) {
				return out; // no SSA var means no APUTs can target this temp; return as-is
			}
			SSAVar sVar = arrReg.getSVar();
			for (BlockNode block : mth.getBasicBlocks()) {
				for (InsnNode insn : block.getInstructions()) {
					if (insn.getType() == InsnType.APUT && insn.getArg(0).isSameVar(arrReg)) {
						Long idx = intEval.evalInt(insn.getArg(1), 0);
						if (idx == null) {
							return null;
						}
						int i = idx.intValue();
						if (i < 0 || i >= out.length) {
							return null;
						}
						out[i] = evalObject(insn.getArg(2), depth + 1);
					}
				}
			}
			return out;
		}
		return null;
	}

	/**
	 * Resolve an {@code SGET} on a JDK static field: only public, static, final, declared on a
	 * class in the {@link JdkInterpreter} whitelist (so {@code Integer.TYPE} → {@code int.class},
	 * {@code StandardCharsets.UTF_8} → the real {@code Charset}, but a user app's static field
	 * stays un-folded).
	 */
	@Nullable
	private Object resolveStaticField(InsnNode sgetInsn) {
		if (!(sgetInsn instanceof jadx.core.dex.instructions.IndexInsnNode)) {
			return null;
		}
		Object idx = ((jadx.core.dex.instructions.IndexInsnNode) sgetInsn).getIndex();
		if (!(idx instanceof jadx.core.dex.info.FieldInfo)) {
			return null;
		}
		jadx.core.dex.info.FieldInfo f = (jadx.core.dex.info.FieldInfo) idx;
		// Delegate to JdkInterpreter so the same allow-list governs SGET resolution everywhere
		// (here in caller-IR walks, and inside PureFold's interpreter for snapshotted helpers).
		return jdk.resolveStaticFinal(f.getDeclClass().getFullName(), f.getName());
	}

	/** Inline-array fallback: resolve integral arrays directly from the FILLED_NEW_ARRAY node. */
	@Nullable
	private Object resolveIntegralArrayFromInsn(InsnNode assignInsn, ArgType el) {
		if (!(assignInsn instanceof FilledNewArrayNode)) {
			return null;
		}
		FilledNewArrayNode fa = (FilledNewArrayNode) assignInsn;
		int n = fa.getArgsCount();
		long[] vals = new long[n];
		for (int i = 0; i < n; i++) {
			Long v = intEval.evalInt(fa.getArg(i), 0);
			if (v == null) {
				return null;
			}
			vals[i] = Eval.extend(el, v);
		}
		if (ArgType.BYTE.equals(el)) {
			byte[] out = new byte[n];
			for (int i = 0; i < n; i++) {
				out[i] = (byte) vals[i];
			}
			return out;
		}
		if (ArgType.CHAR.equals(el)) {
			char[] out = new char[n];
			for (int i = 0; i < n; i++) {
				out[i] = (char) vals[i];
			}
			return out;
		}
		if (ArgType.INT.equals(el)) {
			int[] out = new int[n];
			for (int i = 0; i < n; i++) {
				out[i] = (int) vals[i];
			}
			return out;
		}
		return vals;
	}

	@Nullable
	private Object resolveIntegralArray(RegisterArg arrReg, int depth) {
		ArgType type = arrReg.getType();
		if (!type.isArray()) {
			return null;
		}
		ArgType el = type.getArrayElement();
		if (ArgType.BYTE.equals(el)) {
			return intEval.resolveByteArray(arrReg);
		}
		long[] vals = intEval.resolveLongArray(arrReg);
		if (vals == null) {
			return null;
		}
		if (ArgType.CHAR.equals(el)) {
			char[] out = new char[vals.length];
			for (int i = 0; i < vals.length; i++) {
				out[i] = (char) vals[i];
			}
			return out;
		}
		if (ArgType.SHORT.equals(el)) {
			short[] out = new short[vals.length];
			for (int i = 0; i < vals.length; i++) {
				out[i] = (short) vals[i];
			}
			return out;
		}
		if (ArgType.INT.equals(el)) {
			int[] out = new int[vals.length];
			for (int i = 0; i < vals.length; i++) {
				out[i] = (int) vals[i];
			}
			return out;
		}
		if (ArgType.LONG.equals(el)) {
			return vals.clone();
		}
		if (ArgType.BOOLEAN.equals(el)) {
			boolean[] out = new boolean[vals.length];
			for (int i = 0; i < vals.length; i++) {
				out[i] = vals[i] != 0;
			}
			return out;
		}
		return null;
	}

	/**
	 * Generic call-folding entry: dispatches an app helper snapshot ({@link PureFold}) when one is
	 * available; otherwise looks up the JDK declaring class in {@link JdkInterpreter}. Pure helpers
	 * that return an integer / String are folded; everything else refuses.
	 */
	@Nullable
	private Object foldCall(MethodInfo callMth, BaseInvokeNode inv, int depth) {
		boolean ctor = inv instanceof ConstructorInsn;
		boolean isStatic = !ctor && (inv instanceof InvokeNode) && ((InvokeNode) inv).isStaticCall();

		// App helper snapshot: same path as the integer fold, but accepts mixed object args.
		// Allow null args here: snapshotted helpers often have decoy parameters (e.g. an unused
		// `Object` slot like `System.out` to disguise the call shape). The interpreter naturally
		// refuses if any unresolved arg is actually consumed — so it stays sound.
		if (keys.bodies().containsKey(callMth.getRawFullId())) {
			Object[] args = collectArgsAllowingNull(inv, 0, depth);
			return helperFold.foldCall(callMth, args, depth + 1);
		}

		String declClass = callMth.getDeclClass().getFullName();
		if (!jdk.handles(declClass)) {
			return null;
		}
		int firstArg;
		Object instance;
		if (ctor) {
			firstArg = 0;
			instance = null; // handler's constructor path creates the instance
		} else if (isStatic) {
			firstArg = 0;
			instance = null;
		} else {
			firstArg = 1;
			instance = evalObject(inv.getArg(0), depth + 1);
			if (instance == null) {
				return null;
			}
		}
		Object[] args = collectArgs(inv, firstArg, depth);
		if (args == null) {
			return null;
		}
		return jdk.invoke(callMth, instance, args);
	}

	@Nullable
	private Object[] collectArgs(BaseInvokeNode inv, int start, int depth) {
		int total = inv.getArgsCount();
		Object[] args = new Object[total - start];
		for (int i = 0; i < args.length; i++) {
			InsnArg a = inv.getArg(start + i);
			Object v = evalObject(a, depth + 1);
			if (v == null) {
				return null;
			}
			// Unwrap NULL_REF back to Java null at the boundary (the handler sees real null).
			args[i] = v == TypeMap.NULL_REF ? null : v;
		}
		return args;
	}

	/** Like {@link #collectArgs}, but unresolved args land as {@code null} rather than failing. */
	private Object[] collectArgsAllowingNull(BaseInvokeNode inv, int start, int depth) {
		int total = inv.getArgsCount();
		Object[] args = new Object[total - start];
		for (int i = 0; i < args.length; i++) {
			args[i] = evalObject(inv.getArg(start + i), depth + 1);
		}
		return args;
	}

	/** Type test: does this producer instruction yield an object (or String) value? */
	static boolean producesObject(InsnNode insn) {
		if (insn == null) {
			return false;
		}
		InsnType t = insn.getType();
		if (t == InsnType.CONST_STR || t == InsnType.NEW_ARRAY || t == InsnType.FILLED_NEW_ARRAY
				|| t == InsnType.CONSTRUCTOR) {
			return true;
		}
		if (t == InsnType.INVOKE) {
			ArgType ret = ((InvokeNode) insn).getCallMth().getReturnType();
			return ret != null && (ret.isObject() || ret.isArray());
		}
		return false;
	}
}
