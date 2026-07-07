package jadx.plugins.stringdecrypt;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import jadx.core.dex.info.FieldInfo;
import jadx.core.dex.instructions.ArithNode;
import jadx.core.dex.instructions.ArithOp;
import jadx.core.dex.instructions.FillArrayInsn;
import jadx.core.dex.instructions.FilledNewArrayNode;
import jadx.core.dex.instructions.IndexInsnNode;
import jadx.core.dex.instructions.InsnType;
import jadx.core.dex.instructions.InvokeNode;
import jadx.core.dex.instructions.NewArrayNode;
import jadx.core.dex.instructions.args.ArgType;
import jadx.core.dex.instructions.args.InsnArg;
import jadx.core.dex.instructions.args.InsnWrapArg;
import jadx.core.dex.instructions.args.LiteralArg;
import jadx.core.dex.instructions.args.RegisterArg;
import jadx.core.dex.instructions.args.SSAVar;
import jadx.core.dex.nodes.InsnNode;
import jadx.core.dex.nodes.MethodNode;
import jadx.core.utils.InsnUtils;

/**
 * Per-method compile-time constant evaluator over jadx abstracted instructions. Resolves the
 * constant byte[] argument of a decrypt call, folding arbitrary nested integer expressions:
 * literals, {@code CONST}, arithmetic ({@code ARITH}/{@code NEG}/{@code NOT}), conversions
 * ({@code CAST}), scalar static fields ({@code SGET}), and array reads ({@code AGET}) over static
 * "key tables" or method-local constant arrays.
 */
final class Evaluator {

	private static final long[] FAILED = new long[0];
	private static final long[] CYCLE = new long[0]; // marks a phi back-edge re-entered mid-resolution
	private static final int MAX_DEPTH = 500;

	private final MethodNode mth;
	private final KeyData keys;
	private final PureFold pureFold;
	private final MethodInsnIndex insnIndex;
	private final IdentityHashMap<SSAVar, long[]> localArrays = new IdentityHashMap<>();
	private final Set<FilledNewArrayNode> filledInProgress = Collections.newSetFromMap(new IdentityHashMap<>());
	private final Set<InsnNode> activePhi = Collections.newSetFromMap(new IdentityHashMap<>());
	private final IdentityHashMap<InsnNode, Long> evalCache = new IdentityHashMap<>(); // memoize evalInt

	Evaluator(MethodNode mth, KeyData keys, PureFold pureFold, MethodInsnIndex insnIndex) {
		this.mth = mth;
		this.keys = keys;
		this.pureFold = pureFold;
		this.insnIndex = insnIndex;
	}

	// ------------------------------------------------------------------------------------------
	// constant byte[] resolution (the decrypt argument)
	// ------------------------------------------------------------------------------------------

	/**
	 * Resolve any integral array reference to its constant {@code long[]} contents (widened/extended
	 * per element type). Convenience wrapper for non-{@code byte[]} callers — used by
	 * {@link ObjectEvaluator} to reconstruct {@code char[]}/{@code int[]}/etc.
	 */
	@Nullable
	long[] resolveLongArray(InsnArg arg) {
		return resolveArrayBase(arg, 0);
	}

	@Nullable
	byte[] resolveByteArray(InsnArg arg) {
		InsnNode assign = producer(arg);
		// filled-new-array byte[]{...}
		if (assign instanceof FilledNewArrayNode && ArgType.BYTE.equals(((FilledNewArrayNode) assign).getElemType())) {
			FilledNewArrayNode fa = (FilledNewArrayNode) assign;
			byte[] out = new byte[fa.getArgsCount()];
			for (int i = 0; i < fa.getArgsCount(); i++) {
				Long v = evalInt(fa.getArg(i), 0);
				if (v == null) {
					return null;
				}
				out[i] = v.byteValue();
			}
			if (arg instanceof RegisterArg && !applyByteAputs((RegisterArg) arg, out)) {
				return null;
			}
			return out;
		}
		if (!(arg instanceof RegisterArg)) {
			return null;
		}
		RegisterArg ra = (RegisterArg) arg;
		SSAVar sVar = ra.getSVar();
		if (sVar == null) {
			return null;
		}
		Integer size = null;
		if (assign != null && assign.getType() == InsnType.NEW_ARRAY
				&& ArgType.BYTE.equals(((NewArrayNode) assign).getArrayType().getArrayElement())) {
			Long s = evalInt(assign.getArg(0), 0);
			if (s != null) {
				size = s.intValue();
			}
		}
		byte[] out = null;
		FillArrayInsn fill = findFill(sVar); // fill-array-data seed (optionally patched by aput)
		if (fill != null) {
			byte[] seed = fillToBytes(fill);
			if (size == null) {
				size = seed.length;
			}
			if (size < 0 || size > keys.maxArraySize()) {
				return null;
			}
			out = new byte[size];
			System.arraycopy(seed, 0, out, 0, Math.min(seed.length, size));
		}
		if (size == null || size <= 0 || size > keys.maxArraySize()) {
			return null;
		}
		if (out == null) {
			out = new byte[size];
		}
		return applyByteAputs(ra, out) ? out : null;
	}

	/** Apply every {@code aput} into {@code arrArg} in program order (last write wins). */
	private boolean applyByteAputs(RegisterArg arrArg, byte[] out) {
		for (InsnNode insn : insnIndex.aputsFor(arrArg)) {
			Long idx = evalInt(insn.getArg(1), 0);
			Long val = evalInt(insn.getArg(2), 0);
			if (idx == null || val == null) {
				return false;
			}
			int i = idx.intValue();
			if (i < 0 || i >= out.length) {
				return false;
			}
			out[i] = val.byteValue();
		}
		return true;
	}

	@Nullable
	private FillArrayInsn findFill(SSAVar sVar) {
		return insnIndex.fillFor(sVar);
	}

	private static java.util.List<LiteralArg> asLiteralArgs(FillArrayInsn fill, ArgType elem) {
		return fill.getLiteralArgs(elem);
	}

	private static byte[] fillToBytes(FillArrayInsn fill) {
		List<LiteralArg> lits = fill.getLiteralArgs(ArgType.BYTE);
		byte[] out = new byte[lits.size()];
		for (int i = 0; i < lits.size(); i++) {
			out[i] = (byte) lits.get(i).getLiteral();
		}
		return out;
	}

	// ------------------------------------------------------------------------------------------
	// integer expression evaluator
	// ------------------------------------------------------------------------------------------

	@Nullable
	Long evalInt(InsnArg arg, int depth) {
		if (depth > MAX_DEPTH) {
			return null; // stack-safety net only; obfuscated expression trees are far shallower
		}
		Object c = InsnUtils.getConstValueByArg(mth.root(), arg);
		if (c instanceof LiteralArg) {
			return ((LiteralArg) c).getLiteral();
		}
		InsnNode insn = producer(arg);
		if (insn == null) {
			return null;
		}
		// Memoize by producing instruction: obfuscated code reuses sub-expressions heavily
		// (b = a^a; c = b^b; ...), which would otherwise re-evaluate shared nodes exponentially. The
		// tentative null entry below also breaks any def-use cycle (a node re-entered mid-evaluation
		// reads null = "not a compile-time constant").
		if (evalCache.containsKey(insn)) {
			return evalCache.get(insn);
		}
		evalCache.put(insn, null);
		Long result = evalProducer(insn, depth);
		evalCache.put(insn, result);
		return result;
	}

	@Nullable
	private Long evalProducer(InsnNode insn, int depth) {
		switch (insn.getType()) {
			case CONST:
				// a CONST holds raw bits; for a float/double literal those bits are NOT an integer value,
				// so refuse it (otherwise `(int) 3.14f` etc. would fold to the bit pattern)
				if (isFloatingPoint(insn.getResult())) {
					return null;
				}
				return (insn.getArgsCount() > 0 && insn.getArg(0) instanceof LiteralArg)
						? ((LiteralArg) insn.getArg(0)).getLiteral()
						: null;
			case MOVE:
				return evalInt(insn.getArg(0), depth + 1);
			case CAST: {
				// float/double -> int is a value conversion (F2I/D2I), not a bit truncation: can't be
				// computed from the source's integer model, so refuse rather than fold the wrong value
				if (insn.getArgsCount() > 0 && isFloatingPoint(insn.getArg(0))) {
					return null;
				}
				Long v = evalInt(insn.getArg(0), depth + 1);
				if (v == null) {
					return null;
				}
				// integer narrowing conversion (e.g. long-to-int, int-to-byte): truncate to target type
				Object to = insn instanceof IndexInsnNode ? ((IndexInsnNode) insn).getIndex() : null;
				return to instanceof ArgType ? Eval.extend((ArgType) to, v) : v;
			}
			case NEG: {
				Long a = evalInt(insn.getArg(0), depth + 1);
				return a == null ? null : -a;
			}
			case PHI: {
				if (!activePhi.add(insn)) {
					return null; // cycle (loop-carried phi)
				}
				try {
					Long result = null;
					for (int k = 0; k < insn.getArgsCount(); k++) {
						Long v = evalInt(insn.getArg(k), depth + 1);
						if (v == null) {
							return null;
						}
						if (result == null) {
							result = v;
						} else if (!result.equals(v)) {
							return null; // incoming values disagree -> not a constant
						}
					}
					return result;
				} finally {
					activePhi.remove(insn);
				}
			}
			case NOT: {
				Long a = evalInt(insn.getArg(0), depth + 1);
				return a == null ? null : ~a;
			}
			case ARITH: {
				Long a = evalInt(insn.getArg(0), depth + 1);
				Long b = evalInt(insn.getArg(1), depth + 1);
				if (a == null || b == null) {
					return null;
				}
				return arith(((ArithNode) insn).getOp(), a, b);
			}
			case SGET: {
				FieldInfo f = fieldOf(insn);
				return (f != null && keys.isImmutable(f.getRawFullId())) ? keys.scalars().get(f.getRawFullId()) : null;
			}
			case AGET: {
				long[] table = resolveArrayBase(insn.getArg(0), depth + 1);
				if (table == null || table == CYCLE) {
					return null;
				}
				Long idx = evalInt(insn.getArg(1), depth + 1);
				if (idx == null) {
					return null;
				}
				int i = idx.intValue();
				return (i >= 0 && i < table.length) ? table[i] : null;
			}
			case INVOKE: {
				// interprocedural: a pure static helper returning an integer, with constant args
				InvokeNode inv = (InvokeNode) insn;
				int argc = inv.getArgsCount();
				Object[] args = new Object[argc];
				for (int k = 0; k < argc; k++) {
					Long a = evalInt(inv.getArg(k), depth + 1);
					if (a == null) {
						return null; // non-constant arg (or instance receiver) -> can't fold
					}
					args[k] = a;
				}
				Object r = pureFold.foldCall(inv, args, depth + 1);
				return r instanceof Long ? (Long) r : null;
			}
			default:
				return null;
		}
	}

	/** Resolve an array operand to its constant contents: a static key table or a local array. */
	@Nullable
	private long[] resolveArrayBase(InsnArg base, int depth) {
		if (depth > MAX_DEPTH) {
			return null;
		}
		InsnNode p = producer(base);
		if (p == null) {
			return null;
		}
		switch (p.getType()) {
			case SGET: {
				FieldInfo f = fieldOf(p);
				return (f != null && keys.isImmutable(f.getRawFullId())) ? keys.arrays().get(f.getRawFullId()) : null;
			}
			case MOVE:
			case CAST:
			case CHECK_CAST:
				// a move / array-typed cast doesn't change the contents: follow through to the source
				return resolveArrayBase(p.getArg(0), depth + 1);
			case PHI: {
				// loop-carried (or merged) array reference: constant only if every non-back-edge
				// resolves to the very same reconstructed table object. A back-edge that re-enters this
				// in-progress phi (the loop simply carries the array unchanged) is skipped, not failed.
				if (activePhi.contains(p)) {
					return CYCLE;
				}
				activePhi.add(p);
				try {
					long[] common = null;
					for (int k = 0; k < p.getArgsCount(); k++) {
						long[] t = resolveArrayBase(p.getArg(k), depth + 1);
						if (t == CYCLE) {
							continue; // back-edge: value comes from the entry edge(s)
						}
						if (t == null || (common != null && common != t)) {
							return null;
						}
						common = t;
					}
					return common; // null only if every edge cycled (degenerate)
				} finally {
					activePhi.remove(p);
				}
			}
			case NEW_ARRAY:
				return (base instanceof RegisterArg) ? reconstructLocalArray((RegisterArg) base, (NewArrayNode) p) : null;
			case FILLED_NEW_ARRAY:
				return reconstructFilled((FilledNewArrayNode) p);
			default:
				return null;
		}
	}

	@Nullable
	private long[] reconstructLocalArray(RegisterArg arrArg, NewArrayNode na) {
		ArgType elem = na.getArrayType().getArrayElement();
		if (!Eval.isIntegral(elem)) {
			return null;
		}
		SSAVar sVar = arrArg.getSVar();
		if (sVar == null) {
			return null;
		}
		long[] cached = localArrays.get(sVar);
		if (cached != null) {
			return cached == FAILED ? null : cached;
		}
		localArrays.put(sVar, FAILED); // mark in-progress to break cycles; stays as failure unless overwritten
		Long s = evalInt(na.getArg(0), 0);
		if (s == null) {
			return null;
		}
		int size = s.intValue();
		if (size <= 0 || size > keys.maxArraySize()) {
			return null;
		}
		long[] table = new long[size];
		// 1) Seed from any associated FILL_ARRAY payload (e.g. dx-style "new byte[N]; fill-array-data").
		//    Without this, the initial table is all-zeros and reads of pre-APUT positions wrongly return 0.
		FillArrayInsn fill = findFill(sVar);
		if (fill != null) {
			List<LiteralArg> lits = fill.getLiteralArgs(elem);
			for (int i = 0; i < lits.size() && i < size; i++) {
				table[i] = Eval.extend(elem, lits.get(i).getLiteral());
			}
		}
		// 2) Apply APUTs in program order (last write wins). Critically, this means a downstream AGET
		//    that reads a mutated index sees the POST-APUT value — required for the obfuscator pattern
		//    `byte[] arr = {…}; arr[k] = X; … BigInteger.valueOf(arr[k] + N)` to fold correctly.
		for (InsnNode insn : insnIndex.aputsFor(arrArg)) {
			Long idx = evalInt(insn.getArg(1), 0);
			Long val = evalInt(insn.getArg(2), 0);
			if (idx == null || val == null) {
				return null;
			}
			int i = idx.intValue();
			if (i < 0 || i >= size) {
				return null;
			}
			table[i] = Eval.extend(elem, val);
		}
		localArrays.put(sVar, table);
		return table;
	}

	@Nullable
	private long[] reconstructFilled(FilledNewArrayNode fa) {
		ArgType elem = fa.getElemType();
		if (!Eval.isIntegral(elem)) {
			return null;
		}
		long[] table = new long[fa.getArgsCount()];
		for (int i = 0; i < fa.getArgsCount(); i++) {
			Long v = evalInt(fa.getArg(i), 0);
			if (v == null) {
				return null;
			}
			table[i] = Eval.extend(elem, v);
		}
		// Apply any APUTs targeting this FILLED_NEW_ARRAY's result reg: the obfuscator pattern
		// `byte[] arr = {...}; arr[k] = X; … arr[k]` would otherwise see arr[k]'s un-patched
		// literal at a downstream AGET. Cycle break: a Set membership check on the FA itself
		// avoids infinite recursion if an APUT value resolves through this same array.
		RegisterArg result = fa.getResult();
		if (result == null || result.getSVar() == null) {
			return table;
		}
		if (!filledInProgress.add(fa)) {
			return table; // re-entered: just expose the inline-literal view to the inner caller
		}
		try {
			for (InsnNode insn : insnIndex.aputsFor(result)) {
				Long idx = evalInt(insn.getArg(1), 0);
				Long val = evalInt(insn.getArg(2), 0);
				if (idx == null || val == null) {
					continue; // skip APUT we can't resolve; keep the inline value at that slot
				}
				int i = idx.intValue();
				if (i < 0 || i >= table.length) {
					continue;
				}
				table[i] = Eval.extend(elem, val);
			}
		} finally {
			filledInProgress.remove(fa);
		}
		return table;
	}

	@Nullable
	private static FieldInfo fieldOf(InsnNode insn) {
		if (insn instanceof IndexInsnNode && ((IndexInsnNode) insn).getIndex() instanceof FieldInfo) {
			return (FieldInfo) ((IndexInsnNode) insn).getIndex();
		}
		return null;
	}

	@Nullable
	private static InsnNode producer(InsnArg arg) {
		if (arg.isInsnWrap()) {
			return ((InsnWrapArg) arg).getWrapInsn();
		}
		if (arg.isRegister()) {
			return ((RegisterArg) arg).getAssignInsn();
		}
		return null;
	}

	/** True for {@code float}/{@code double} operands — their bits are not an integer value. */
	private static boolean isFloatingPoint(InsnArg arg) {
		if (arg == null) {
			return false;
		}
		ArgType t = arg.getType();
		return ArgType.FLOAT.equals(t) || ArgType.DOUBLE.equals(t);
	}

	@Nullable
	private static Long arith(ArithOp op, long a, long b) {
		switch (op) {
			case ADD:
				return a + b;
			case SUB:
				return a - b;
			case MUL:
				return a * b;
			case DIV:
				return b != 0 ? a / b : null;
			case REM:
				return b != 0 ? a % b : null;
			case AND:
				return a & b;
			case OR:
				return a | b;
			case XOR:
				return a ^ b;
			case SHL:
				return a << ((int) b & 63);
			case SHR:
				return a >> ((int) b & 63);
			case USHR:
				return a >>> ((int) b & 63);
			default:
				return null;
		}
	}
}
