package jadx.plugins.stringdecrypt;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import jadx.core.dex.info.ClassInfo;
import jadx.core.dex.info.MethodInfo;
import jadx.core.dex.instructions.FilledNewArrayNode;
import jadx.core.dex.instructions.IndexInsnNode;
import jadx.core.dex.instructions.InsnType;
import jadx.core.dex.instructions.InvokeNode;
import jadx.core.dex.instructions.InvokeType;
import jadx.core.dex.instructions.args.ArgType;
import jadx.core.dex.instructions.args.InsnArg;
import jadx.core.dex.instructions.args.InsnWrapArg;
import jadx.core.dex.instructions.args.LiteralArg;
import jadx.core.dex.instructions.args.RegisterArg;
import jadx.core.dex.nodes.InsnNode;
import jadx.core.dex.nodes.RootNode;

/**
 * Rewrites a resolvable reflective call {@code handle.invoke(recv, new Object[]{args...})} into the
 * direct call it stands for. Sits at the end of the {@link StringDecryptPass} resolver pipeline, so it
 * only fires on calls the constant folders could not collapse (i.e. the receiver/args are runtime
 * values).
 *
 * <p>
 * Two reflective layers are handled:
 * <ul>
 * <li><b>meta-peel</b>: when the resolved handle is {@code Method.invoke} itself, the call is the
 * obfuscator's {@code Method.class.getMethod("invoke").invoke(realHandle, recv, args)} form — peel one
 * layer and recurse on {@code realHandle};</li>
 * <li><b>direct</b>: build the real call — {@code STATIC} or {@code VIRTUAL}. For an instance target
 * the {@code Object}-typed reflective receiver is re-typed to the declaring class (a {@code CHECK_CAST}
 * is inserted only when its static type isn't already assignable; sound because the runtime object
 * provably is that type). A primitive return is re-boxed with {@code Boxed.valueOf(...)} to keep the
 * {@code Object}-typed reflective context's expectations; primitive args are unwrapped from their
 * {@code Boxed.valueOf(prim)}/literal form, reference args flow through (cast to the parameter type
 * when needed).</li>
 * </ul>
 * Anything it cannot prove safe is declined, leaving the existing (already string-folded) form.
 *
 * <p>
 * The arguments duplicated into the synthesized call ({@link #dup}) carry their {@link
 * jadx.core.dex.instructions.args.SSAVar}s but are bound into the use-lists only when the install
 * site calls {@code rebindArgs()} — for the wrapped (inlined) path this happens in {@code
 * StringDecryptPass.replaceWrapped}, mirroring {@code BlockUtils.replaceInsn} on the top-level path.
 * Skipping that rebind perturbs jadx's later type inference (an unrelated {@code Long.valueOf(i)}
 * rendering as {@code Long.valueOf((Object) i)}), which is why this resolver must never install a
 * register-bearing replacement without it.
 */
final class DeindirectionResolver implements Resolver {

	private static final String METHOD_CLASS = "java.lang.reflect.Method";
	private static final int MAX_PEEL = 4;

	private final StringDecryptOptions options;

	DeindirectionResolver(StringDecryptOptions options) {
		this.options = options;
	}

	@Override
	public @Nullable InsnNode resolve(ResolveContext ctx) {
		if (!options.isDeindirectReflection() || !(ctx.insn instanceof InvokeNode)) {
			return null;
		}
		InvokeNode inv = (InvokeNode) ctx.insn;
		MethodInfo call = inv.getCallMth();
		if (!"invoke".equals(call.getName()) || !METHOD_CLASS.equals(call.getDeclClass().getFullName())
				|| inv.getArgsCount() < 2) {
			return null;
		}
		// Method.invoke(receiver, Object[] args): handle=arg0, receiver=arg1, args array=arg2.
		InsnArg argsArray = inv.getArgsCount() >= 3 ? inv.getArg(2) : null;
		return deindirect(ctx, inv.getArg(0), inv.getArg(1), argsArray, ctx.insn, MAX_PEEL);
	}

	/**
	 * Resolve {@code handleArg.invoke(receiver, argsArray)} to a direct call. When the handle is itself
	 * {@code Method.invoke}, peel one reflective layer and recurse; otherwise emit the direct call (with
	 * result re-boxing for a primitive return). {@code metaInsn} is the original outermost instruction
	 * whose result register/metadata the replacement inherits.
	 */
	private @Nullable InsnNode deindirect(ResolveContext ctx, InsnArg handleArg, InsnArg receiverArg,
			@Nullable InsnArg argsArray, InsnNode metaInsn, int guard) {
		if (guard <= 0) {
			return null;
		}
		CallTarget target = ctx.oev.resolveCallTarget(handleArg, 0);
		if (target == null) {
			return null;
		}
		if (METHOD_CLASS.equals(target.declClass.getFullName()) && "invoke".equals(target.name)) {
			// meta layer: argsArray is {realReceiver, realArgsArray}; the real handle is this receiver.
			List<InsnArg> meta = arrayElements(argsArray);
			if (meta == null || meta.size() != 2) {
				return null;
			}
			return deindirect(ctx, receiverArg, meta.get(0), meta.get(1), metaInsn, guard - 1);
		}
		List<InsnArg> elems = arrayElements(argsArray);
		if (elems == null || elems.size() != target.params.size()) {
			return null;
		}
		List<InsnArg> callArgs = new ArrayList<>(elems.size());
		for (int i = 0; i < elems.size(); i++) {
			InsnArg mapped = mapArg(ctx, elems.get(i), target.params.get(i));
			if (mapped == null) {
				return null;
			}
			callArgs.add(mapped);
		}
		RootNode root = ctx.mth.root();
		MethodInfo mi = MethodInfo.fromDetails(root, target.declClass, target.name, target.params, target.returnType);
		InvokeNode direct;
		if (target.isStatic) {
			direct = new InvokeNode(mi, InvokeType.STATIC, callArgs.size());
		} else {
			// Instance call: the reflective receiver is typed Object, so re-type it to the declaring
			// class. coerceToType inserts a CHECK_CAST only when the static type isn't already the
			// declaring class (sound: the runtime object provably is that type, else the original
			// reflective invoke would have thrown). receiver becomes VIRTUAL arg 0.
			InsnArg recv = coerceToType(ctx, receiverArg, target.declClass.getType());
			if (recv == null) {
				return null;
			}
			direct = new InvokeNode(mi, InvokeType.VIRTUAL, 1 + callArgs.size());
			direct.addArg(recv);
		}
		for (InsnArg a : callArgs) {
			direct.addArg(a);
		}
		if (target.returnType.isPrimitive()) {
			return box(root, direct, target.returnType, metaInsn);
		}
		ReplacementFactory.copyReplacementMetadata(metaInsn, direct);
		return direct;
	}

	/** Wrap a primitive-returning direct call in {@code Boxed.valueOf(...)} so it fits an Object context. */
	private static @Nullable InsnNode box(RootNode root, InvokeNode direct, ArgType prim, InsnNode metaInsn) {
		String boxName = boxClassName(prim);
		if (boxName == null) {
			return null;
		}
		ClassInfo boxCls = ClassInfo.fromName(root, boxName);
		ArgType boxType = ArgType.object(boxName);
		MethodInfo valueOf = MethodInfo.fromDetails(root, boxCls, "valueOf", List.of(prim), boxType);
		InvokeNode boxed = new InvokeNode(valueOf, InvokeType.STATIC, 1);
		boxed.addArg(InsnArg.wrapInsnIntoArg(direct));
		ReplacementFactory.copyReplacementMetadata(metaInsn, boxed);
		return boxed;
	}

	private static @Nullable String boxClassName(ArgType prim) {
		if (ArgType.INT.equals(prim)) {
			return "java.lang.Integer";
		}
		if (ArgType.LONG.equals(prim)) {
			return "java.lang.Long";
		}
		if (ArgType.SHORT.equals(prim)) {
			return "java.lang.Short";
		}
		if (ArgType.BYTE.equals(prim)) {
			return "java.lang.Byte";
		}
		if (ArgType.CHAR.equals(prim)) {
			return "java.lang.Character";
		}
		if (ArgType.BOOLEAN.equals(prim)) {
			return "java.lang.Boolean";
		}
		if (ArgType.FLOAT.equals(prim)) {
			return "java.lang.Float";
		}
		if (ArgType.DOUBLE.equals(prim)) {
			return "java.lang.Double";
		}
		return null; // void or unknown -> can't box
	}

	/**
	 * The element args of a reflective {@code Object[]} argument: a {@code FILLED_NEW_ARRAY}
	 * {@code new Object[]{a, b, …}}, or an empty {@code new Object[0]} ({@code NEW_ARRAY} of constant
	 * size 0) — the latter is how a no-arg method is invoked reflectively.
	 */
	private static @Nullable List<InsnArg> arrayElements(@Nullable InsnArg arrayArg) {
		if (arrayArg == null) {
			return null;
		}
		InsnNode prod;
		if (arrayArg.isInsnWrap()) {
			prod = ((InsnWrapArg) arrayArg).getWrapInsn();
		} else if (arrayArg instanceof RegisterArg) {
			prod = ((RegisterArg) arrayArg).getAssignInsn();
		} else {
			return null;
		}
		if (prod instanceof FilledNewArrayNode) {
			FilledNewArrayNode arr = (FilledNewArrayNode) prod;
			List<InsnArg> out = new ArrayList<>(arr.getArgsCount());
			for (int i = 0; i < arr.getArgsCount(); i++) {
				out.add(arr.getArg(i));
			}
			return out;
		}
		if (prod != null && prod.getType() == InsnType.NEW_ARRAY && prod.getArgsCount() >= 1
				&& prod.getArg(0) instanceof LiteralArg && ((LiteralArg) prod.getArg(0)).getLiteral() == 0L) {
			return new ArrayList<>(); // no-arg call: new Object[0]
		}
		return null;
	}

	/** Map one reflective {@code Object[]} element to the direct call's argument for {@code param}. */
	private static @Nullable InsnArg mapArg(ResolveContext ctx, InsnArg elem, ArgType param) {
		if (param.isPrimitive()) {
			// a) the obfuscator's Boxed.valueOf(prim) wrap -> the underlying primitive
			InsnArg prim = unwrapValueOf(elem, param);
			if (prim != null) {
				return dup(prim);
			}
			// b) a boxed primitive the const folder already collapsed to a literal -> re-emit as `param`
			if (elem instanceof LiteralArg) {
				return InsnArg.lit(((LiteralArg) elem).getLiteral(), param);
			}
			return null;
		}
		// reference parameter: the reflective Object[] element is Object-typed, so cast to the param
		// type when its static type isn't already assignable.
		return coerceToType(ctx, elem, param);
	}

	/**
	 * Duplicate {@code arg} and, if its static type is not already assignable to {@code targetType},
	 * wrap it in an explicit {@code CHECK_CAST}. The reflective context types every receiver/argument
	 * as {@code Object}; the resolved target needs the real type. The cast is sound because the
	 * runtime value provably is that type (the original reflective invoke would have thrown otherwise).
	 */
	private static @Nullable InsnArg coerceToType(ResolveContext ctx, InsnArg arg, ArgType targetType) {
		InsnArg d = dup(arg);
		if (d == null) {
			return null;
		}
		if (assignableTo(ctx, d.getType(), targetType)) {
			return d;
		}
		InsnNode cast = new IndexInsnNode(InsnType.CHECK_CAST, targetType, 1);
		cast.addArg(d);
		InsnArg wrapped = InsnArg.wrapInsnIntoArg(cast);
		wrapped.setType(targetType);
		return wrapped;
	}

	/** True iff a value of static type {@code from} can be used where {@code to} is expected. */
	private static boolean assignableTo(ResolveContext ctx, @Nullable ArgType from, ArgType to) {
		if (from == null || !from.isTypeKnown()) {
			return false;
		}
		if (from.equals(to)) {
			return true;
		}
		return ctx.mth.root().getTypeCompare().compareTypes(from, to).isNarrowOrEqual();
	}

	/** {@code Boxed.valueOf(prim)} → the {@code prim} argument, when it matches the primitive {@code param}. */
	private static @Nullable InsnArg unwrapValueOf(InsnArg elem, ArgType param) {
		if (!elem.isInsnWrap()) {
			return null;
		}
		InsnNode p = ((InsnWrapArg) elem).getWrapInsn();
		// Peel CHECK_CAST/CAST wrappers: a nested reflective box that we already de-indirected arrives
		// as `(Boxed) Boxed.valueOf(prim)` (the coerceToType cast around the synthesized valueOf).
		while ((p.getType() == InsnType.CHECK_CAST || p.getType() == InsnType.CAST)
				&& p.getArgsCount() == 1 && p.getArg(0).isInsnWrap()) {
			p = ((InsnWrapArg) p.getArg(0)).getWrapInsn();
		}
		if (!(p instanceof InvokeNode)) {
			return null;
		}
		MethodInfo mi = ((InvokeNode) p).getCallMth();
		if (!"valueOf".equals(mi.getName()) || p.getArgsCount() != 1 || mi.getArgumentsTypes().size() != 1) {
			return null;
		}
		// Trust the resolved Boxed.valueOf(prim) signature: its single argument is that primitive.
		// (The wrapped arg's own type can be UNKNOWN when it's a synthesized CAST from a nested
		// de-indirection, so comparing inner.getType() directly would spuriously fail.)
		return param.equals(mi.getArgumentsTypes().get(0)) ? p.getArg(0) : null;
	}

	private static @Nullable InsnArg dup(InsnArg arg) {
		if (arg instanceof RegisterArg) {
			return ((RegisterArg) arg).duplicate(); // duplicate() carries reg, SSAVar and type
		}
		if (arg.isInsnWrap()) {
			return InsnArg.wrapInsnIntoArg(((InsnWrapArg) arg).getWrapInsn().copy());
		}
		if (arg instanceof LiteralArg) {
			LiteralArg lit = (LiteralArg) arg;
			return InsnArg.lit(lit.getLiteral(), lit.getType());
		}
		return null; // unknown arg kind -> refuse rather than risk a malformed call
	}
}
