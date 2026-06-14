package jadx.plugins.stringdecrypt;

import org.jetbrains.annotations.Nullable;

import jadx.core.dex.nodes.InsnNode;

/**
 * A replacement strategy consulted by {@link StringDecryptPass} for each candidate instruction.
 * Returns the replacement IR node, or {@code null} to decline so the next resolver gets a turn. The
 * pass runs its resolvers in a fixed order and takes the first non-null result.
 *
 * <p>
 * This is the plug-in seam for the replacement layer — the counterpart to
 * {@link jadx.plugins.stringdecrypt.jdk.JdkClassHandler} on the evaluation side. New capabilities
 * (reflective de-indirection, user {@code .jadx.kts} script pipelines) are added as additional
 * resolvers rather than more branches inside the pass.
 */
@FunctionalInterface
interface Resolver {

	@Nullable
	InsnNode resolve(ResolveContext ctx);
}
