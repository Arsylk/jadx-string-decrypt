package jadx.plugins.stringdecrypt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import jadx.core.dex.instructions.FillArrayInsn;
import jadx.core.dex.instructions.InsnType;
import jadx.core.dex.instructions.args.RegisterArg;
import jadx.core.dex.instructions.args.SSAVar;
import jadx.core.dex.nodes.BlockNode;
import jadx.core.dex.nodes.InsnNode;
import jadx.core.dex.nodes.MethodNode;

/**
 * Per-method instruction index for hot def-use scans performed by the string-decrypt evaluators.
 *
 * <p>The decompile pass asks the same questions many times while folding a method: "which APUTs write
 * this array?", "does this array have a fill-array-data seed?". Scanning every block for every query
 * is quadratic on methods with lots of local byte-array string builders. This index is intentionally
 * small and built once at method visit start; instructions are not removed until the post-fold cleanup,
 * so the index remains valid for all evaluator queries in the replacement phase.
 */
final class MethodInsnIndex {

	private final Map<SSAVar, List<InsnNode>> aputsByArray = new IdentityHashMap<>();
	private final Map<SSAVar, FillArrayInsn> fillByArray = new IdentityHashMap<>();
	private final Map<InsnNode, Integer> order = new IdentityHashMap<>();

	MethodInsnIndex(MethodNode mth) {
		int pos = 0;
		for (BlockNode block : mth.getBasicBlocks()) {
			for (InsnNode insn : block.getInstructions()) {
				order.put(insn, pos++);
				if (insn.getArgsCount() == 0) {
					continue;
				}
				InsnType type = insn.getType();
				if (type != InsnType.APUT && type != InsnType.FILL_ARRAY) {
					continue;
				}
				if (!(insn.getArg(0) instanceof RegisterArg)) {
					continue;
				}
				SSAVar arrayVar = ((RegisterArg) insn.getArg(0)).getSVar();
				if (arrayVar == null) {
					continue;
				}
				if (type == InsnType.APUT) {
					aputsByArray.computeIfAbsent(arrayVar, k -> new ArrayList<>()).add(insn);
				} else if (!fillByArray.containsKey(arrayVar)) {
					fillByArray.put(arrayVar, (FillArrayInsn) insn);
				}
			}
		}
	}

	List<InsnNode> aputsFor(RegisterArg arrayArg) {
		SSAVar sVar = arrayArg.getSVar();
		if (sVar == null) {
			return Collections.emptyList();
		}
		List<InsnNode> list = aputsByArray.get(sVar);
		return list != null ? list : Collections.emptyList();
	}

	@Nullable
	FillArrayInsn fillFor(SSAVar arrayVar) {
		return fillByArray.get(arrayVar);
	}

	int orderOf(InsnNode insn) {
		Integer pos = order.get(insn);
		return pos != null ? pos : Integer.MAX_VALUE;
	}
}
