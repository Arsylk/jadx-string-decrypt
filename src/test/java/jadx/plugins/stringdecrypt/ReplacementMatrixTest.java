package jadx.plugins.stringdecrypt;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import jadx.core.dex.instructions.ConstClassNode;
import jadx.core.dex.instructions.ConstStringNode;
import jadx.core.dex.instructions.FilledNewArrayNode;
import jadx.core.dex.instructions.InsnType;
import jadx.core.dex.instructions.args.ArgType;
import jadx.core.dex.instructions.args.LiteralArg;
import jadx.core.dex.nodes.InsnNode;
import jadx.plugins.stringdecrypt.eval.TypeMap;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the value→IR replacement surface every {@code PipelineResult.replace…} factory routes through
 * ({@link ReplacementFactory#makeReplacementInsn}). This is the shared boundary the built-in folders and
 * script pipelines both use, so it is exercised directly with the full breadth of value/target-type
 * combinations a pipeline can emit, asserting the concrete jadx IR node each produces.
 */
class ReplacementMatrixTest {

	private final List<String> decrypted = new ArrayList<>();

	private InsnNode make(Object value, ArgType targetType) {
		return ReplacementFactory.makeReplacementInsn(from(), value, targetType, decrypted, false);
	}

	/** A neutral source instruction with no result register — enough for metadata copy. */
	private static InsnNode from() {
		return new InsnNode(InsnType.NOP, 0);
	}

	@Test
	void string_becomesConstString() {
		InsnNode n = make("hello", ArgType.STRING);
		assertThat(n).isInstanceOf(ConstStringNode.class);
		assertThat(((ConstStringNode) n).getString()).isEqualTo("hello");
		assertThat(decrypted).contains("\"hello\"");
	}

	@Test
	void scalars_becomeTypedConst() {
		assertConst(make(42, ArgType.INT), 42L, ArgType.INT);
		assertConst(make((long) 9, ArgType.LONG), 9L, ArgType.LONG);
		assertConst(make('z', ArgType.CHAR), (long) 'z', ArgType.CHAR);
		assertConst(make(true, ArgType.BOOLEAN), 1L, ArgType.BOOLEAN);
		assertConst(make((byte) 7, ArgType.BYTE), 7L, ArgType.BYTE);
	}

	@Test
	void charArray_becomesFilledNewArrayOfChar() {
		InsnNode n = make(new char[] { 'a', 'b', 'c' }, ArgType.array(ArgType.CHAR));
		assertThat(n).isInstanceOf(FilledNewArrayNode.class);
		FilledNewArrayNode arr = (FilledNewArrayNode) n;
		assertThat(arr.getElemType()).isEqualTo(ArgType.CHAR);
		assertThat(arr.getArgsCount()).isEqualTo(3);
	}

	@Test
	void byteArray_becomesFilledNewArrayOfByte() {
		InsnNode n = make(new byte[] { 1, 2, 3, 4 }, ArgType.array(ArgType.BYTE));
		assertThat(n).isInstanceOf(FilledNewArrayNode.class);
		assertThat(((FilledNewArrayNode) n).getElemType()).isEqualTo(ArgType.BYTE);
		assertThat(n.getArgsCount()).isEqualTo(4);
	}

	@Test
	void classValue_becomesConstClass() {
		InsnNode n = make(String.class, null);
		assertThat(n).isInstanceOf(ConstClassNode.class);
		assertThat(((ConstClassNode) n).getClsType()).isEqualTo(ArgType.STRING);
	}

	@Test
	void typedNull_becomesConstZero() {
		InsnNode n = make(TypeMap.NULL_REF, ArgType.STRING);
		assertThat(n.getType()).isEqualTo(InsnType.CONST);
		assertThat(((LiteralArg) n.getArg(0)).getLiteral()).isZero();
	}

	@Test
	void incompatibleValue_refused() {
		assertThat(make("not-an-int", ArgType.INT)).isNull(); // String into a primitive int -> refuse
		assertThat(make(42, ArgType.STRING)).isNull(); // int into a String -> refuse
		assertThat(make(TypeMap.NULL_REF, ArgType.INT)).isNull(); // null into a primitive -> refuse
	}

	private static void assertConst(InsnNode n, long expectedLiteral, ArgType expectedType) {
		assertThat(n.getType()).isEqualTo(InsnType.CONST);
		LiteralArg lit = (LiteralArg) n.getArg(0);
		assertThat(lit.getLiteral()).isEqualTo(expectedLiteral);
		assertThat(lit.getType()).isEqualTo(expectedType);
	}
}
