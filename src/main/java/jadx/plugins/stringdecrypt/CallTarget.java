package jadx.plugins.stringdecrypt;

import java.util.List;

import jadx.core.dex.info.ClassInfo;
import jadx.core.dex.instructions.args.ArgType;

/**
 * The resolved real method that a reflective {@code Method} handle points at — everything needed to
 * synthesize a direct call in place of {@code handle.invoke(recv, args)}. Produced by
 * {@link ObjectEvaluator#resolveCallTarget} from either a host {@link java.lang.reflect.Method} (JDK
 * targets) or a snapshotted app {@code MethodNode}.
 */
final class CallTarget {

	final ClassInfo declClass;
	final String name;
	final List<ArgType> params;
	final ArgType returnType;
	final boolean isStatic;

	CallTarget(ClassInfo declClass, String name, List<ArgType> params, ArgType returnType, boolean isStatic) {
		this.declClass = declClass;
		this.name = name;
		this.params = params;
		this.returnType = returnType;
		this.isStatic = isStatic;
	}
}
