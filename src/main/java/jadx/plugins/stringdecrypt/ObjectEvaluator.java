package jadx.plugins.stringdecrypt;

import java.util.IdentityHashMap;

import org.jetbrains.annotations.Nullable;

import jadx.core.dex.info.ClassInfo;
import jadx.core.dex.info.MethodInfo;
import jadx.core.dex.instructions.BaseInvokeNode;
import jadx.core.dex.instructions.ConstClassNode;
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
import jadx.core.dex.nodes.ClassNode;
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
			case CONST_CLASS:
				return resolveConstClass(((ConstClassNode) insn).getClsType());
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
	 *
	 * <p>
	 * {@code arrReg} may be null when the array is constructed inline (a wrap arg without a named
	 * result reg) — then we recover the element type from the {@link FilledNewArrayNode} itself and
	 * skip the SSA-driven APUT scan (no APUTs can target an unnamed temp anyway).
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
		if (javaEl.isPrimitive()) {
			// Non-integral primitive element reaches here only for float/double (integral arrays are
			// handled above). We don't model floating-point constant arrays: reflecting one yields a
			// float[]/double[] that neither casts to Object[] (ClassCastException) nor accepts boxed
			// values (ArrayStoreException). Refuse to fold rather than crash the pass.
			return null;
		}
		if (assignInsn instanceof FilledNewArrayNode) {
			FilledNewArrayNode fa = (FilledNewArrayNode) assignInsn;
			int n = fa.getArgsCount();
			Object[] out = (Object[]) java.lang.reflect.Array.newInstance(javaEl, n);
			for (int i = 0; i < n; i++) {
				Object v = evalArrayElement(fa.getArg(i), depth + 1);
				if (v == UNRESOLVED_ARRAY_ELEMENT || !storable(javaEl, v)) {
					return null;
				}
				out[i] = v;
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
						Object v = evalArrayElement(insn.getArg(2), depth + 1);
						if (v == UNRESOLVED_ARRAY_ELEMENT || !storable(javaEl, v)) {
							return null;
						}
						out[i] = v;
					}
				}
			}
			return out;
		}
		return null;
	}

	/**
	 * True iff {@code v} can be stored into an array with component type {@code elemType} without an
	 * {@link ArrayStoreException}. A {@code null} is always storable into an object array; otherwise the
	 * value must be an instance of the element type (e.g. a resolved {@code Long} must not be written
	 * into a {@code String[]} — a type mismatch in the reconstructed array means we refuse the fold).
	 */
	private static boolean storable(Class<?> elemType, Object v) {
		return v == null || elemType.isInstance(v);
	}

	private static final Object UNRESOLVED_ARRAY_ELEMENT = new Object();

	private Object evalArrayElement(InsnArg arg, int depth) {
		Object v = evalObject(arg, depth);
		if (v == null) {
			return UNRESOLVED_ARRAY_ELEMENT;
		}
		return v == TypeMap.NULL_REF ? null : v;
	}

	/**
	 * Resolve a {@code CONST_CLASS} ({@code SomeClass.class}) to its live {@link Class}. This is the
	 * root of nearly every reflective bridge an obfuscator emits ({@code StringBuilder.class
	 * .getConstructor(...)}, {@code Object.class.getMethod("toString")}, ...); without it those chains
	 * dead-end at their first link. Gated to types the {@link JdkInterpreter} actually models (or
	 * primitives / arrays of them) so we never hand back a live {@code Class} for app code we must not
	 * reflect into — app {@code .class} literals stay un-folded. Downstream reflective invokes are
	 * separately gated, so this only widens what can be <em>read</em>, never what gets executed.
	 */
	@Nullable
	private Object resolveConstClass(ArgType clsType) {
		Class<?> c = TypeMap.toClass(clsType);
		if (c == null) {
			return null;
		}
		Class<?> base = c;
		while (base.isArray()) {
			base = base.getComponentType();
		}
		// A primitive, a modelled JDK type, or any standard JDK API class. The last lets a bare
		// `java.math.MathContext.class` resolve to its live Class so reflective getMethod/getField
		// lookups that take it as a parameter type can be folded. Sound: handing back a Class constant
		// only widens what can be *read*; reflective *invocation* on an unmodelled class is still
		// refused by MethodReflHandler. App `.class` literals (no handler, non-JDK package) stay un-folded.
		if (base.isPrimitive() || jdk.handles(base.getName()) || isStandardJdkClass(base.getName())) {
			return c;
		}
		return null;
	}

	private static boolean isStandardJdkClass(String name) {
		return name.startsWith("java.") || name.startsWith("javax.");
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
		String name = callMth.getName();
		if (!jdk.handles(declClass)) {
			return null;
		}
		int firstArg;
		Object instance;
		if (ctor || isStatic) {
			firstArg = 0;
			instance = null; // handler's constructor path creates the instance / static call
		} else {
			firstArg = 1;
			instance = evalObject(inv.getArg(0), depth + 1);
			if (instance == null) {
				return null;
			}
		}
		// Symbolic reflection over app classes: an obfuscator routes a call to its own (snapshotted)
		// decryptor helper through `Class.forName("<app cls>").getDeclaredMethod(name,..).invoke(..)`.
		// The host JVM can't load app code, so we model the class/method as jadx IR nodes and run a
		// snapshotted helper's reflective invoke through the same interpreter a direct call uses.
		if (instance instanceof AppClassRef && "java.lang.Class".equals(declClass)) {
			return symbolicClassMethod((AppClassRef) instance, name, inv, depth);
		}
		if (instance instanceof AppMethodRef && "java.lang.reflect.Method".equals(declClass) && "invoke".equals(name)) {
			return symbolicMethodInvoke((AppMethodRef) instance, inv, depth);
		}
		Object[] args = collectArgs(inv, firstArg, depth);
		if (args == null) {
			return null;
		}
		Object result = jdk.invoke(callMth, instance, args);
		// JDK Class.forName refused the name (not a whitelisted JDK class): if it names a class jadx
		// loaded from the app, hand back a symbolic ref so the chain above can continue.
		if (result == null && isStatic && "forName".equals(name) && "java.lang.Class".equals(declClass)
				&& args.length >= 1 && args[0] instanceof String && !((String) args[0]).isEmpty()) {
			try {
				ClassNode node = mth.root().resolveClass((String) args[0]);
				if (node != null) {
					return new AppClassRef(node);
				}
			} catch (RuntimeException e) {
				// a name that decrypted to something un-parseable (empty/garbage) makes ClassInfo.fromName
				// throw (e.g. StringIndexOutOfBounds in cleanObjectName) — treat as un-resolvable, don't fold
				return null;
			}
		}
		return result;
	}

	/** Symbolic ref to a class jadx loaded from the app (not host-loadable for reflection). */
	private static final class AppClassRef {
		final ClassNode cls;

		AppClassRef(ClassNode cls) {
			this.cls = cls;
		}
	}

	/** Symbolic ref to a resolved app {@link MethodNode} (the target of a reflective {@code invoke}). */
	private static final class AppMethodRef {
		final MethodNode mth;

		AppMethodRef(MethodNode mth) {
			this.mth = mth;
		}
	}

	/**
	 * Resolve the {@code Method} handle in {@code methodHandleArg} to the real method it points at, so a
	 * {@code handle.invoke(recv, args)} can be rewritten to a direct call (see {@code DeindirectionResolver}).
	 * Works for JDK targets (a live {@link java.lang.reflect.Method} from the reflective-bridge chain) and
	 * app targets (a snapshotted {@link AppMethodRef}); returns null for anything else.
	 */
	@Nullable
	CallTarget resolveCallTarget(InsnArg methodHandleArg, int depth) {
		Object handle = evalObject(methodHandleArg, depth);
		if (handle instanceof java.lang.reflect.Method) {
			java.lang.reflect.Method m = (java.lang.reflect.Method) handle;
			java.util.List<ArgType> params = new java.util.ArrayList<>();
			for (Class<?> p : m.getParameterTypes()) {
				ArgType at = TypeMap.fromClass(p);
				if (at == null) {
					return null;
				}
				params.add(at);
			}
			ArgType ret = TypeMap.fromClass(m.getReturnType());
			if (ret == null) {
				return null;
			}
			ClassInfo declClass = ClassInfo.fromName(mth.root(), m.getDeclaringClass().getName());
			return new CallTarget(declClass, m.getName(), params, ret,
					java.lang.reflect.Modifier.isStatic(m.getModifiers()));
		}
		if (handle instanceof AppMethodRef) {
			MethodNode node = ((AppMethodRef) handle).mth;
			MethodInfo mi = node.getMethodInfo();
			return new CallTarget(mi.getDeclClass(), mi.getName(), mi.getArgumentsTypes(),
					mi.getReturnType(), node.getAccessFlags().isStatic());
		}
		return null;
	}

	/** {@code appClass.getMethod/getDeclaredMethod(name, paramTypes)} → symbolic {@link AppMethodRef}. */
	@Nullable
	private Object symbolicClassMethod(AppClassRef ref, String name, BaseInvokeNode inv, int depth) {
		if (!"getDeclaredMethod".equals(name) && !"getMethod".equals(name)) {
			return null; // getField / getConstructor on app classes not modelled yet
		}
		Object[] a = collectArgs(inv, 1, depth);
		if (a == null || a.length < 1 || !(a[0] instanceof String)) {
			return null;
		}
		Class<?>[] params = a.length >= 2 ? asClassArray(a[1]) : new Class<?>[0];
		if (params == null) {
			return null;
		}
		MethodNode m = findAppMethod(ref.cls, (String) a[0], params);
		return m != null ? new AppMethodRef(m) : null;
	}

	/** {@code appMethod.invoke(receiver, args)} → fold via the helper snapshot (only if snapshotted). */
	@Nullable
	private Object symbolicMethodInvoke(AppMethodRef ref, BaseInvokeNode inv, int depth) {
		MethodInfo mi = ref.mth.getMethodInfo();
		if (!keys.bodies().containsKey(mi.getRawFullId())) {
			return null; // not a foldable pure helper -> leave the reflective call as-is
		}
		// Method.invoke(receiver, Object[] args): the varargs array is the call's arg index 2
		// (arg0 = the Method handle/receiver of `.invoke`, arg1 = the invoke target receiver). Resolve
		// it allowing null elements: obfuscators pass an unresolvable decoy (e.g. System.out) the helper
		// never reads, so a single unresolved element must not poison the whole arg list.
		Object[] callArgs = inv.getArgsCount() >= 3 ? resolveVarargsAllowingNull(inv.getArg(2), depth) : new Object[0];
		if (callArgs == null) {
			return null;
		}
		return helperFold.foldCall(mi, callArgs, depth + 1);
	}

	/**
	 * Resolve a constant {@code Object[]} (the varargs array of a reflective {@code invoke}) element by
	 * element, mapping both {@link TypeMap#NULL_REF} and an unresolved element to Java {@code null}.
	 * Unlike {@link #resolveArrayProducer} this never fails on a single unresolved element — the helper
	 * interpreter refuses on its own if it actually reads one.
	 */
	@Nullable
	private Object[] resolveVarargsAllowingNull(InsnArg arrayArg, int depth) {
		InsnNode prod;
		if (arrayArg.isInsnWrap()) {
			prod = ((InsnWrapArg) arrayArg).getWrapInsn();
		} else if (arrayArg instanceof RegisterArg) {
			prod = ((RegisterArg) arrayArg).getAssignInsn();
		} else {
			return null;
		}
		if (prod == null) {
			return null;
		}
		if (prod instanceof FilledNewArrayNode) {
			FilledNewArrayNode fa = (FilledNewArrayNode) prod;
			Object[] out = new Object[fa.getArgsCount()];
			for (int i = 0; i < out.length; i++) {
				out[i] = allowNull(evalObject(fa.getArg(i), depth + 1));
			}
			return out;
		}
		if (prod.getType() == InsnType.NEW_ARRAY) {
			Long size = intEval.evalInt(prod.getArg(0), 0);
			if (size == null || size < 0 || size > keys.maxArraySize()) {
				return null;
			}
			Object[] out = new Object[size.intValue()];
			RegisterArg arrReg = prod.getResult();
			if (arrReg == null || arrReg.getSVar() == null) {
				return out;
			}
			for (BlockNode block : mth.getBasicBlocks()) {
				for (InsnNode insn : block.getInstructions()) {
					if (insn.getType() == InsnType.APUT && insn.getArg(0).isSameVar(arrReg)) {
						Long idx = intEval.evalInt(insn.getArg(1), 0);
						if (idx == null || idx < 0 || idx >= out.length) {
							return null;
						}
						out[idx.intValue()] = allowNull(evalObject(insn.getArg(2), depth + 1));
					}
				}
			}
			return out;
		}
		return null;
	}

	/** Match an app method by name + exact (host-resolvable) parameter types; null if absent/ambiguous. */
	@Nullable
	private MethodNode findAppMethod(ClassNode cls, String name, Class<?>[] params) {
		MethodNode match = null;
		for (MethodNode m : cls.getMethods()) {
			MethodInfo mi = m.getMethodInfo();
			if (!mi.getName().equals(name) || mi.getArgumentsTypes().size() != params.length) {
				continue;
			}
			boolean ok = true;
			for (int i = 0; i < params.length; i++) {
				Class<?> pc = TypeMap.toClass(mi.getArgumentsTypes().get(i));
				if (pc == null || !pc.equals(params[i])) {
					ok = false;
					break;
				}
			}
			if (ok) {
				if (match != null) {
					return null; // ambiguous overloads -> refuse
				}
				match = m;
			}
		}
		return match;
	}

	private static @Nullable Class<?>[] asClassArray(Object v) {
		if (v == null) {
			return new Class<?>[0];
		}
		if (v instanceof Class<?>[]) {
			return (Class<?>[]) v;
		}
		if (v instanceof Object[]) {
			Object[] in = (Object[]) v;
			Class<?>[] out = new Class<?>[in.length];
			for (int i = 0; i < in.length; i++) {
				if (!(in[i] instanceof Class<?>)) {
					return null;
				}
				out[i] = (Class<?>) in[i];
			}
			return out;
		}
		return null;
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

	/**
	 * Like {@link #collectArgs}, but a single unresolved arg does not fail the whole list — it lands
	 * as {@link TypeMap#UNRESOLVED} (a real {@code null} literal stays {@code null}). A pure-helper
	 * slot that is never read (a decoy) folds anyway; the interpreter refuses the moment it actually
	 * consumes an {@code UNRESOLVED}, so unresolved is never silently conflated with {@code null}.
	 */
	private Object[] collectArgsAllowingNull(BaseInvokeNode inv, int start, int depth) {
		int total = inv.getArgsCount();
		Object[] args = new Object[total - start];
		for (int i = 0; i < args.length; i++) {
			args[i] = allowNull(evalObject(inv.getArg(start + i), depth + 1));
		}
		return args;
	}

	/** Map an {@code evalObject} result to a helper-arg slot: unresolved → UNRESOLVED, NULL_REF → null. */
	private static Object allowNull(Object v) {
		if (v == null) {
			return TypeMap.UNRESOLVED;
		}
		return v == TypeMap.NULL_REF ? null : v;
	}
}
