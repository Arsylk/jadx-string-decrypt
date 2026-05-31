package jadx.plugins.stringdecrypt;

import org.jetbrains.annotations.Nullable;

import jadx.core.dex.info.FieldInfo;
import jadx.core.dex.info.MethodInfo;
import jadx.core.dex.instructions.ArithOp;
import jadx.core.dex.instructions.IfOp;
import jadx.core.dex.instructions.InsnType;
import jadx.core.dex.instructions.args.ArgType;

/**
 * Immutable snapshot of a pure static method's raw instructions, taken in the prepare pass (before
 * the threaded decompile pass unloads/mutates the live {@code MethodNode}). {@link PureFold}
 * interprets this snapshot, so interprocedural folding never has to read a method body that another
 * thread may be decompiling concurrently.
 *
 * <p>
 * Supports a small instruction-pointer-driven control flow model — IF/GOTO with pre-resolved
 * branch targets ({@link Op#branchTarget} is an op-array index, never a raw dex offset) — so for-
 * and while-loops in pure helpers can be interpreted directly. A method containing any instruction
 * outside the interpreter's whitelist is not snapshotted at all (so it is simply never folded).
 */
final class MethodBody {

	/** A snapshotted instruction operand: either a constant literal or a register reference. */
	static final class Arg {
		final boolean literal;
		final long value; // literal value (when literal)
		final int reg; // register number (when register)

		private Arg(boolean literal, long value, int reg) {
			this.literal = literal;
			this.value = value;
			this.reg = reg;
		}

		static Arg lit(long v) {
			return new Arg(true, v, -1);
		}

		static Arg reg(int r) {
			return new Arg(false, 0, r);
		}
	}

	/** A snapshotted instruction: type, result register and operands plus immutable type metadata. */
	static final class Op {
		final InsnType type;
		final int resultReg; // -1 if none
		final Arg[] args;
		final @Nullable ArgType argType; // CAST target / NEW_ARRAY element type
		final @Nullable ArithOp arithOp; // ARITH operator
		final @Nullable FieldInfo field; // SGET field
		final @Nullable MethodInfo callMth; // INVOKE target
		final @Nullable IfOp ifOp; // IF comparator
		final int branchTarget; // op-array index (resolved from dex offset); -1 if not a branch

		Op(InsnType type, int resultReg, Arg[] args, @Nullable ArgType argType,
				@Nullable ArithOp arithOp, @Nullable FieldInfo field, @Nullable MethodInfo callMth,
				@Nullable IfOp ifOp, int branchTarget) {
			this.type = type;
			this.resultReg = resultReg;
			this.args = args;
			this.argType = argType;
			this.arithOp = arithOp;
			this.field = field;
			this.callMth = callMth;
			this.ifOp = ifOp;
			this.branchTarget = branchTarget;
		}
	}

	final Op[] ops;
	final int[] argRegs; // register numbers of the method parameters, in order

	MethodBody(Op[] ops, int[] argRegs) {
		this.ops = ops;
		this.argRegs = argRegs;
	}
}
