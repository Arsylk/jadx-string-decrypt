package jadx.plugins.stringdecrypt;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

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
 * First, narrowest <em>sound</em> slice — declines anything it can't prove safe, leaving the existing
 * (already string-folded) reflective form:
 * <ul>
 * <li><b>static</b> targets only (a virtual call would need the receiver re-typed to the declaring
 * class, which is not guaranteed in the reflective context);</li>
 * <li><b>reference</b> return type only (a primitive return would need the result re-boxed to match
 * the {@code Object}-typed reflective context);</li>
 * <li>each argument is either reference-typed (used unchanged) or a {@code Boxed.valueOf(prim)} for a
 * primitive parameter (unboxed to {@code prim}).</li>
 * </ul>
 */
final class DeindirectionResolver implements Resolver {

	private static final String METHOD_CLASS = "java.lang.reflect.Method";

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
		CallTarget target = ctx.oev.resolveCallTarget(inv.getArg(0), 0);
		if (target == null || !target.isStatic) {
			return null; // first slice: static targets only
		}
		if (!(target.returnType.isObject() || target.returnType.isArray())) {
			return null; // first slice: reference return only (no primitive-result re-boxing)
		}
		// Reflective call args live in the Object[] at invoke arg index 2 (arg0=handle, arg1=receiver).
		List<InsnArg> elems = arrayElements(inv.getArgsCount() >= 3 ? inv.getArg(2) : null);
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
		ReplacementFactory.copyReplacementMetadata(ctx.insn, direct);
		return direct;
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
