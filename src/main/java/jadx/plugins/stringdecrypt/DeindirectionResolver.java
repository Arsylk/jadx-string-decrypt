package jadx.plugins.stringdecrypt;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import jadx.core.dex.info.ClassInfo;
import jadx.core.dex.info.MethodInfo;
import jadx.core.dex.instructions.FilledNewArrayNode;
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
 * <li><b>direct</b>: build the real call. <b>Static</b> targets only for now (a virtual call would
 * need the receiver re-typed to the declaring class, which the reflective context does not guarantee).
 * A primitive return is re-boxed with {@code Boxed.valueOf(...)} to keep the {@code Object}-typed
 * reflective context's expectations; args are reference-typed (used unchanged) or {@code
 * Boxed.valueOf(prim)} unboxed to {@code prim}.</li>
 * </ul>
 * Anything it cannot prove safe is declined, leaving the existing (already string-folded) form.
 *
 * <p>
 * <b>EXPERIMENTAL — off by default.</b> The current argument duplication ({@link #dup}) is not yet
 * SSA-correct: re-using/copying a runtime register into the synthesized call perturbs jadx's later
 * type inference (e.g. an unrelated {@code Long.valueOf(i)} can render as {@code Long.valueOf((Object)
 * i)}). Re-enable only after the arg moves go through proper SSA use-list rebinding.
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
		if (!target.isStatic) {
			return null; // static targets only (no receiver re-typing yet)
		}
		List<InsnArg> elems = arrayElements(argsArray);
		if (elems == null || elems.size() != target.params.size()) {
			return null;
		}
		List<InsnArg> callArgs = new ArrayList<>(elems.size());
		for (int i = 0; i < elems.size(); i++) {
			InsnArg mapped = mapArg(elems.get(i), target.params.get(i));
			if (mapped == null) {
				return null;
			}
			callArgs.add(mapped);
		}
		RootNode root = ctx.mth.root();
		MethodInfo mi = MethodInfo.fromDetails(root, target.declClass, target.name, target.params, target.returnType);
		InvokeNode direct = new InvokeNode(mi, InvokeType.STATIC, callArgs.size());
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

	/** The element args of a {@code new Object[]{...}} produced as a {@code FILLED_NEW_ARRAY}. */
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
		if (!(prod instanceof FilledNewArrayNode)) {
			return null;
		}
		FilledNewArrayNode arr = (FilledNewArrayNode) prod;
		List<InsnArg> out = new ArrayList<>(arr.getArgsCount());
		for (int i = 0; i < arr.getArgsCount(); i++) {
			out.add(arr.getArg(i));
		}
		return out;
	}

	/** Map one reflective {@code Object[]} element to the direct call's argument for {@code param}. */
	private static @Nullable InsnArg mapArg(InsnArg elem, ArgType param) {
		if (param.isPrimitive()) {
			InsnArg prim = unwrapValueOf(elem, param);
			return prim != null ? dup(prim) : null;
		}
		return dup(elem); // reference parameter: the element flows through unchanged
	}

	/** {@code Boxed.valueOf(prim)} → the {@code prim} argument, when it matches the primitive {@code param}. */
	private static @Nullable InsnArg unwrapValueOf(InsnArg elem, ArgType param) {
		if (!elem.isInsnWrap()) {
			return null;
		}
		InsnNode p = ((InsnWrapArg) elem).getWrapInsn();
		if (!(p instanceof InvokeNode)) {
			return null;
		}
		MethodInfo mi = ((InvokeNode) p).getCallMth();
		if (!"valueOf".equals(mi.getName()) || p.getArgsCount() != 1) {
			return null;
		}
		InsnArg inner = p.getArg(0);
		return param.equals(inner.getType()) ? inner : null;
	}

	private static @Nullable InsnArg dup(InsnArg arg) {
		if (arg instanceof RegisterArg) {
			return ((RegisterArg) arg).duplicate();
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
