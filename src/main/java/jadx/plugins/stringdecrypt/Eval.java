package jadx.plugins.stringdecrypt;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jadx.core.dex.info.FieldInfo;
import jadx.core.dex.info.MethodInfo;
import jadx.core.dex.instructions.ArithNode;
import jadx.core.dex.instructions.ConstClassNode;
import jadx.core.dex.instructions.ConstStringNode;
import jadx.core.dex.instructions.FillArrayData;
import jadx.core.dex.instructions.FillArrayInsn;
import jadx.core.dex.instructions.GotoNode;
import jadx.core.dex.instructions.IfNode;
import jadx.core.dex.instructions.IndexInsnNode;
import jadx.core.dex.instructions.InsnType;
import jadx.core.dex.instructions.InvokeNode;
import jadx.core.dex.instructions.NewArrayNode;
import jadx.core.dex.instructions.args.ArgType;
import jadx.core.dex.instructions.args.InsnArg;
import jadx.core.dex.instructions.args.LiteralArg;
import jadx.core.dex.instructions.args.RegisterArg;
import jadx.core.dex.nodes.ClassNode;
import jadx.core.dex.nodes.InsnNode;
import jadx.core.dex.nodes.MethodNode;
import jadx.core.dex.nodes.RootNode;

/**
 * Static helpers: the AES decrypt routine and reconstruction of compile-time {@link KeyData}
 * (static integral arrays + scalar fields) from each class's {@code <clinit>}.
 */
final class Eval {

	private static final Logger LOG = LoggerFactory.getLogger(Eval.class);

	private Eval() {
	}

	// ------------------------------------------------------------------------------------------
	// decrypt
	// ------------------------------------------------------------------------------------------

	/**
	 * Decrypt one "key-appended" ciphertext blob: the last {@code keyTailLen} bytes are the symmetric
	 * key, the preceding bytes are the ciphertext, decrypted with the given JCE {@code transformation}
	 * (e.g. {@code AES/ECB/PKCS5Padding}). A wrong scheme just throws / yields non-printable bytes and
	 * is discarded by the caller, so this is safe to attempt on any candidate. Returns null on any
	 * failure.
	 */
	static @Nullable String tryDecrypt(byte[] blob, int keyTailLen, String transformation) {
		try {
			int split = blob.length - keyTailLen;
			if (keyTailLen <= 0 || split < 0) {
				return null;
			}
			byte[] cipherText = Arrays.copyOfRange(blob, 0, split);
			byte[] key = Arrays.copyOfRange(blob, split, blob.length);
			String algo = transformation.contains("/") ? transformation.substring(0, transformation.indexOf('/')) : transformation;
			Cipher cipher = Cipher.getInstance(transformation);
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, algo));
			return new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
		} catch (Exception e) {
			return null;
		}
	}

	static @Nullable String tryDecodeBase64String(String encoded) {
		try {
			return new String(java.util.Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
		} catch (Exception e) {
			return null;
		}
	}

	static @Nullable String tryDecodePrefixedRc4(String encoded) {
		try {
			if (encoded == null || encoded.length() <= 12) {
				return null;
			}
			byte[] key = encoded.substring(0, 12).getBytes(StandardCharsets.UTF_8);
			String hex = new String(java.util.Base64.getDecoder().decode(encoded.substring(12)), StandardCharsets.UTF_8);
			if ((hex.length() & 1) != 0) {
				return null;
			}
			byte[] data = new byte[hex.length() / 2];
			for (int i = 0; i < hex.length(); i += 2) {
				int hi = Character.digit(hex.charAt(i), 16);
				int lo = Character.digit(hex.charAt(i + 1), 16);
				if (hi < 0 || lo < 0) {
					return null;
				}
				data[i / 2] = (byte) ((hi << 4) + lo);
			}
			return new String(rc4(key, data), StandardCharsets.UTF_8);
		} catch (Exception e) {
			return null;
		}
	}

	private static byte[] rc4(byte[] key, byte[] data) {
		int[] s = new int[256];
		for (int i = 0; i < 256; i++) {
			s[i] = i;
		}
		int j = 0;
		for (int i = 0; i < 256; i++) {
			j = (((j + s[i]) + key[i % key.length]) + 256) % 256;
			int t = s[i];
			s[i] = s[j];
			s[j] = t;
		}
		byte[] out = new byte[data.length];
		int i = 0;
		j = 0;
		for (int k = 0; k < data.length; k++) {
			i = (i + 1) % 256;
			j = (j + s[i]) % 256;
			int t = s[i];
			s[i] = s[j];
			s[j] = t;
			out[k] = (byte) (s[(s[i] + s[j]) % 256] ^ data[k]);
		}
		return out;
	}

	static boolean isPrintable(String s) {
		if (s.isEmpty()) {
			return false;
		}
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			if (ch < 32 && ch != '\n' && ch != '\r' && ch != '\t') {
				return false;
			}
			if (ch >= 0xD800 && ch <= 0xDFFF) {
				return false;
			}
		}
		return true;
	}

	// ------------------------------------------------------------------------------------------
	// numeric helpers
	// ------------------------------------------------------------------------------------------

	static boolean isIntegral(ArgType type) {
		return ArgType.BYTE.equals(type) || ArgType.SHORT.equals(type) || ArgType.CHAR.equals(type)
				|| ArgType.INT.equals(type) || ArgType.LONG.equals(type) || ArgType.BOOLEAN.equals(type);
	}

	/**
	 * Model the value produced when an array element / field of {@code type} is loaded into a register.
	 */
	static long extend(@Nullable ArgType type, long v) {
		if (ArgType.BYTE.equals(type)) {
			return (byte) v;
		}
		if (ArgType.SHORT.equals(type)) {
			return (short) v;
		}
		if (ArgType.CHAR.equals(type)) {
			return (char) v;
		}
		if (ArgType.INT.equals(type)) {
			return (int) v;
		}
		if (ArgType.BOOLEAN.equals(type)) {
			return v != 0 ? 1 : 0;
		}
		return v; // LONG (or unknown): keep as-is
	}

	// ------------------------------------------------------------------------------------------
	// <clinit> reconstruction: static integral arrays + scalar fields
	// ------------------------------------------------------------------------------------------

	static void buildKeyData(ClassNode cls, KeyData keys, boolean detectDecryptors) {
		try {
			buildOne(cls, keys);
			detectBase64StringHelpers(cls, keys);
			buildInstanceStringFields(cls, keys);
			if (detectDecryptors) {
				detectDecryptors(cls, keys);
			}
			snapshotPureCandidates(cls, keys);
		} catch (Throwable t) {
			// a class whose constants can't be reconstructed just won't resolve — surface at debug so
			// a genuine bug (vs. an expected un-reconstructable class) is diagnosable, not fully silent
			LOG.debug("string-decrypt: key-data reconstruction skipped for {}", cls, t);
		}
		for (ClassNode inner : cls.getInnerClasses()) {
			buildKeyData(inner, keys, detectDecryptors);
		}
	}

	// ------------------------------------------------------------------------------------------
	// mutable-static scan (soundness gate for folding static field reads)
	// ------------------------------------------------------------------------------------------

	/**
	 * Whole-program scan recording every static field written outside its declaring class's
	 * {@code <clinit>}: a direct {@code SPUT}, or an {@code APUT} into an array value loaded (via
	 * {@code SGET}, propagated through {@code MOVE}) from that field. Reads of such fields must not be
	 * folded to their reconstructed {@code <clinit>} value. Conservative (default-deny): anything that
	 * can't be proven to be a declaring-{@code <clinit>} write marks the field mutable.
	 */
	static void scanMutableFields(RootNode root, KeyData keys) {
		for (ClassNode cls : root.getClasses()) {
			MethodNode clinit = cls.getClassInitMth();
			for (MethodNode mth : cls.getMethods()) {
				if (mth.isNoCode()) {
					continue;
				}
				boolean isClinit = mth == clinit;
				boolean isConstructor = mth.isConstructor();
				boolean wasLoaded = mth.getInstructions() != null;
				try {
					scanMethodForMutations(cls, mth, isClinit, isConstructor, keys);
				} catch (Throwable t) {
					// ignore: a method we can't scan just won't unlock folding for the fields it touches
				} finally {
					if (!wasLoaded) {
						mth.unload();
					}
				}
			}
		}
	}

	private static void scanMethodForMutations(ClassNode cls, MethodNode mth, boolean isClinit, boolean isConstructor, KeyData keys) {
		InsnNode[] insns = rawInsns(mth);
		if (insns == null) {
			return;
		}
		Map<Integer, FieldInfo> regFieldArray = new HashMap<>(); // regs currently holding a static-field array
		for (InsnNode insn : insns) {
			if (insn == null) {
				continue;
			}
			if (insn.getType() == InsnType.SPUT) {
				FieldInfo f = fieldOf(insn);
				if (f != null && !isDeclaringClinitWrite(cls, isClinit, f)) {
					keys.mutableFields().add(f.getRawFullId());
				}
			} else if (insn.getType() == InsnType.IPUT) {
				FieldInfo f = fieldOf(insn);
				if (f != null && !isDeclaringConstructorWrite(cls, isConstructor, f)) {
					keys.mutableFields().add(f.getRawFullId());
				}
			} else if (insn.getType() == InsnType.APUT) {
				InsnArg base = insn.getArg(0);
				if (base instanceof RegisterArg) {
					FieldInfo f = regFieldArray.get(((RegisterArg) base).getRegNum());
					if (f != null && !isDeclaringClinitWrite(cls, isClinit, f)) {
						keys.mutableFields().add(f.getRawFullId());
					}
				}
			}
			// Track which register currently aliases a static-field array, and (critically) invalidate
			// that mapping the moment the register is reassigned to anything else — registers are reused,
			// so a stale `field-array` mapping would wrongly blame a later local-array `aput` on the field.
			RegisterArg res = insn.getResult();
			if (res != null) {
				int rn = res.getRegNum();
				FieldInfo arrField = null;
				if (insn.getType() == InsnType.SGET) {
					FieldInfo f = fieldOf(insn);
					if (f != null && f.getType().isArray()) {
						arrField = f;
					}
				} else if (insn.getType() == InsnType.MOVE && insn.getArg(0) instanceof RegisterArg) {
					arrField = regFieldArray.get(((RegisterArg) insn.getArg(0)).getRegNum());
				}
				if (arrField != null) {
					regFieldArray.put(rn, arrField);
				} else {
					regFieldArray.remove(rn); // reassigned to a non-field value -> drop the stale alias
				}
			}
		}
	}

	/**
	 * A write to {@code f} is safe only if it is in {@code f}'s own declaring class's {@code <clinit>}.
	 */
	private static boolean isDeclaringClinitWrite(ClassNode cls, boolean isClinit, FieldInfo f) {
		return isClinit && cls.getClassInfo().equals(f.getDeclClass());
	}

	private static boolean isDeclaringConstructorWrite(ClassNode cls, boolean isConstructor, FieldInfo f) {
		return isConstructor && cls.getClassInfo().equals(f.getDeclClass());
	}

	// ------------------------------------------------------------------------------------------
	// pure-helper body snapshots (for interprocedural folding)
	// ------------------------------------------------------------------------------------------

	/** Whitelisted instruction count above which a normal method is too large to be a foldable helper. */
	private static final int SNAPSHOT_MAX_INSNS = 512;
	/** Larger cap for the common inline byte-array XOR string helper: big switch/if ladder, still pure. */
	private static final int SNAPSHOT_MAX_INLINE_XOR_STRING_INSNS = 10_000;

	/**
	 * Snapshot every static method in {@code cls} that returns a {@code String} or integral value and
	 * is built only from the interpreter's whitelist of side-effect-free opcodes. The snapshot is
	 * immutable, so {@link PureFold} can interpret it later without touching the live (concurrently
	 * decompiling) {@link MethodNode}. Methods with any control flow or unsupported opcode are skipped.
	 */
	private static void snapshotPureCandidates(ClassNode cls, KeyData keys) {
		for (MethodNode mth : cls.getMethods()) {
			if (mth.isNoCode() || !mth.getAccessFlags().isStatic()) {
				continue;
			}
			if (!isFoldableReturn(mth.getMethodInfo().getReturnType())) {
				continue;
			}
			boolean wasLoaded = mth.getInstructions() != null;
			try {
				MethodBody body = snapshotBody(mth);
				if (body != null) {
					String id = mth.getMethodInfo().getRawFullId();
					keys.bodies().put(id, body);
					Map<Long, String> inlineXor = buildInlineXorStringTable(mth, body, keys);
					if (!inlineXor.isEmpty()) {
						keys.inlineXorStrings().put(id, inlineXor);
					}
				}
			} catch (Throwable t) {
				// a method that can't be snapshotted simply won't be folded — debug-log so an
				// unexpected snapshot failure is visible rather than silently dropped
				LOG.debug("string-decrypt: pure-helper snapshot skipped for {}", mth, t);
			} finally {
				if (!wasLoaded) {
					mth.unload(); // restore lazy state so we don't keep thousands of bodies loaded
				}
			}
		}
	}

	private static boolean isFoldableReturn(@Nullable ArgType ret) {
		if (ret == null) {
			return false;
		}
		if (isIntegral(ret) || (ret.isObject() && "java.lang.String".equals(ret.getObject()))) {
			return true;
		}
		// byte[]/char[]-returning pure helpers (hex/base64 decoders, key derivers, ...): the interpreter
		// already produces and unboxes these arrays, so snapshotting them lets a constant-arg call fold
		// (and the byte[]-as-string comment surface its text form). Other array element types are left
		// out — they are rarely the carrier of a decoded string.
		return ret.isArray() && (ArgType.BYTE.equals(ret.getArrayElement()) || ArgType.CHAR.equals(ret.getArrayElement()));
	}

	private static @Nullable MethodBody snapshotBody(MethodNode mth) {
		InsnNode[] insns = rawInsns(mth);
		if (insns == null) {
			return null;
		}
		// Pass 1: build a dex-offset -> op-array-index map. The raw insn array is offset-indexed (so
		// IF/GOTO targets are array indices in `insns`); we need to translate those to compact op-array
		// indices since the snapshot skips null/NOP slots.
		int maxInsns = snapshotMaxInsns(mth, insns);
		Map<Integer, Integer> offsetToOpIndex = new HashMap<>();
		int opCount = 0;
		for (int i = 0; i < insns.length; i++) {
			if (insns[i] == null || insns[i].getType() == InsnType.NOP || insns[i].getType() == InsnType.FILL_ARRAY_DATA) {
				continue;
			}
			offsetToOpIndex.put(i, opCount);
			opCount++;
			if (opCount > maxInsns) {
				return null;
			}
		}
		// Pass 2: emit the ops, resolving branch targets through the map. Any unresolvable target
		// (off-end or pointing at a NOP we filtered) refuses the snapshot.
		List<MethodBody.Op> ops = new ArrayList<>(opCount);
		for (int i = 0; i < insns.length; i++) {
			InsnNode insn = insns[i];
			if (insn == null || insn.getType() == InsnType.NOP || insn.getType() == InsnType.FILL_ARRAY_DATA) {
				continue;
			}
			MethodBody.Op op = snapshotOp(insn, offsetToOpIndex, insns);
			if (op == null) {
				return null;
			}
			ops.add(op);
		}
		if (ops.isEmpty()) {
			return null;
		}
		List<RegisterArg> argRegs = mth.getArgRegs();
		int[] regs = new int[argRegs.size()];
		int maxReg = -1;
		for (int i = 0; i < regs.length; i++) {
			regs[i] = argRegs.get(i).getRegNum();
			maxReg = Math.max(maxReg, regs[i]);
		}
		for (MethodBody.Op op : ops) {
			maxReg = Math.max(maxReg, op.resultReg);
			for (MethodBody.Arg arg : op.args) {
				if (!arg.literal) {
					maxReg = Math.max(maxReg, arg.reg);
				}
			}
		}
		return new MethodBody(ops.toArray(new MethodBody.Op[0]), regs, maxReg);
	}

	private static int snapshotMaxInsns(MethodNode mth, InsnNode[] insns) {
		if (isInlineXorStringHelperShape(mth, insns)) {
			return SNAPSHOT_MAX_INLINE_XOR_STRING_INSNS;
		}
		return SNAPSHOT_MAX_INSNS;
	}

	/**
	 * Common obfuscator helper shape:
	 * {@code static String a(int key) { if (key == C) { byte[] b = ...; ... b[i] ^= key; return new String(b, UTF_8); } ... }}.
	 * These are often thousands of raw instructions because one method contains all string cases, but
	 * they are still deterministic and pure, so allow a larger snapshot budget only for this narrow shape.
	 */
	private static boolean isInlineXorStringHelperShape(MethodNode mth, InsnNode[] insns) {
		MethodInfo mi = mth.getMethodInfo();
		if (mi.getArgsCount() != 1 || !ArgType.INT.equals(mi.getArgumentsTypes().get(0))) {
			return false;
		}
		ArgType ret = mi.getReturnType();
		if (ret == null || !ret.isObject() || !"java.lang.String".equals(ret.getObject())) {
			return false;
		}
		boolean hasByteArray = false;
		boolean hasXor = false;
		boolean hasStringCtor = false;
		for (InsnNode insn : insns) {
			if (insn == null) {
				continue;
			}
			if (insn.getType() == InsnType.NEW_ARRAY && insn instanceof NewArrayNode) {
				ArgType arrType = ((NewArrayNode) insn).getArrayType();
				hasByteArray |= arrType != null && arrType.isArray() && ArgType.BYTE.equals(arrType.getArrayElement());
			} else if (insn.getType() == InsnType.ARITH && insn instanceof ArithNode) {
				hasXor |= ((ArithNode) insn).getOp() == jadx.core.dex.instructions.ArithOp.XOR;
			} else if (insn.getType() == InsnType.INVOKE && insn instanceof InvokeNode) {
				MethodInfo call = ((InvokeNode) insn).getCallMth();
				hasStringCtor |= call.isConstructor() && "java.lang.String".equals(call.getDeclClass().getFullName());
			}
		}
		return hasByteArray && hasXor && hasStringCtor;
	}

	private static Map<Long, String> buildInlineXorStringTable(MethodNode mth, MethodBody body, KeyData keys) {
		if (!isInlineXorStringHelperShape(mth, rawInsns(mth))) {
			return java.util.Collections.emptyMap();
		}
		int argReg = body.argRegs.length == 1 ? body.argRegs[0] : -1;
		if (argReg < 0) {
			return java.util.Collections.emptyMap();
		}
		Map<Long, String> out = new HashMap<>();
		MethodBody.Op[] ops = body.ops;
		for (int pc = 0; pc < ops.length; pc++) {
			MethodBody.Op op = ops[pc];
			if (op.type != InsnType.IF || op.args.length < 2) {
				continue;
			}
			Long key = inlineXorBranchKey(op, argReg);
			if (key == null || out.containsKey(key)) {
				continue;
			}
			String decoded = simulateInlineXorBranch(ops, op.branchTarget, key, keys);
			if (decoded != null && isPrintable(decoded)) {
				out.put(key, decoded);
			}
		}
		return out;
	}

	@Nullable
	private static Long inlineXorBranchKey(MethodBody.Op op, int argReg) {
		MethodBody.Arg a = op.args[0];
		MethodBody.Arg b = op.args[1];
		if (!a.literal && a.reg == argReg && b.literal) {
			return b.value;
		}
		if (!b.literal && b.reg == argReg && a.literal) {
			return a.value;
		}
		return null;
	}

	@Nullable
	private static String simulateInlineXorBranch(MethodBody.Op[] ops, int startPc, long key, KeyData keys) {
		Map<Integer, Long> longs = new HashMap<>();
		Map<Integer, byte[]> arrays = new HashMap<>();
		for (int pc = startPc, count = 0; pc >= 0 && pc < ops.length && count++ < 20_000; pc++) {
			MethodBody.Op op = ops[pc];
			switch (op.type) {
				case CONST:
					putLong(longs, op.resultReg, valueOf(longs, op.args[0]));
					break;
				case MOVE:
					if (arrays.containsKey(op.args[0].reg)) {
						arrays.put(op.resultReg, arrays.get(op.args[0].reg));
					} else {
						putLong(longs, op.resultReg, valueOf(longs, op.args[0]));
					}
					break;
				case NEW_ARRAY: {
					Long size = valueOf(longs, op.args[0]);
					if (size == null || size < 0 || size > keys.maxArraySize()) {
						return null;
					}
					arrays.put(op.resultReg, new byte[size.intValue()]);
					break;
				}
				case FILL_ARRAY: {
					byte[] arr = arrays.get(op.args[0].reg);
					if (arr != null && op.payload instanceof long[]) {
						long[] data = (long[]) op.payload;
						for (int i = 0; i < data.length && i < arr.length; i++) {
							arr[i] = (byte) data[i];
						}
					}
					break;
				}
				case APUT: {
					byte[] arr = arrays.get(op.args[0].reg);
					Long idx = valueOf(longs, op.args[1]);
					Long val = valueOf(longs, op.args[2]);
					if (arr != null && idx != null && val != null && idx >= 0 && idx < arr.length) {
						arr[idx.intValue()] = (byte) val.longValue();
					}
					break;
				}
				case AGET: {
					byte[] arr = arrays.get(op.args[0].reg);
					Long idx = valueOf(longs, op.args[1]);
					if (arr == null || idx == null || idx < 0 || idx >= arr.length) {
						return null;
					}
					putLong(longs, op.resultReg, (long) arr[idx.intValue()]);
					break;
				}
				case CAST:
				case CHECK_CAST:
					putLong(longs, op.resultReg, valueOf(longs, op.args[0]));
					break;
				case ARITH: {
					Long a = valueOf(longs, op.args[0]);
					Long b = valueOf(longs, op.args[1]);
					Long r = a != null && b != null ? arithInline(op.arithOp, a, b) : null;
					putLong(longs, op.resultReg, r);
					break;
				}
				case IF: {
					Long a = valueOf(longs, op.args[0]);
					Long b = valueOf(longs, op.args[1]);
					if (a != null && b != null && compare(op.ifOp, a, b)) {
						pc = op.branchTarget - 1;
					}
					break;
				}
				case GOTO:
					pc = op.branchTarget - 1;
					break;
				case INVOKE:
					if (op.callMth != null && op.callMth.isConstructor()
							&& "java.lang.String".equals(op.callMth.getDeclClass().getFullName())
							&& op.args.length >= 2 && arrays.containsKey(op.args[1].reg)) {
						return new String(arrays.get(op.args[1].reg), java.nio.charset.StandardCharsets.UTF_8);
					}
					break;
				case RETURN:
					return null;
				default:
					break;
			}
		}
		return null;
	}

	private static void putLong(Map<Integer, Long> map, int reg, @Nullable Long value) {
		if (reg >= 0 && value != null) {
			map.put(reg, value);
		}
	}

	@Nullable
	private static Long valueOf(Map<Integer, Long> map, MethodBody.Arg arg) {
		return arg.literal ? arg.value : map.get(arg.reg);
	}

	@Nullable
	private static Long arithInline(@Nullable jadx.core.dex.instructions.ArithOp op, long a, long b) {
		if (op == null) {
			return null;
		}
		switch (op) {
			case ADD: return a + b;
			case SUB: return a - b;
			case MUL: return a * b;
			case DIV: return b != 0 ? a / b : null;
			case REM: return b != 0 ? a % b : null;
			case AND: return a & b;
			case OR: return a | b;
			case XOR: return a ^ b;
			case SHL: return a << ((int) b & 63);
			case SHR: return a >> ((int) b & 63);
			case USHR: return a >>> ((int) b & 63);
			default: return null;
		}
	}

	private static boolean compare(@Nullable jadx.core.dex.instructions.IfOp op, long a, long b) {
		if (op == null) {
			return false;
		}
		switch (op) {
			case EQ: return a == b;
			case NE: return a != b;
			case LT: return a < b;
			case LE: return a <= b;
			case GT: return a > b;
			case GE: return a >= b;
			default: return false;
		}
	}

	private static @Nullable MethodBody.Op snapshotOp(InsnNode insn, Map<Integer, Integer> offsetToOpIndex, InsnNode[] insns) {
		MethodBody.Arg[] args = new MethodBody.Arg[insn.getArgsCount()];
		for (int i = 0; i < args.length; i++) {
			InsnArg a = insn.getArg(i);
			if (a instanceof LiteralArg) {
				args[i] = MethodBody.Arg.lit(((LiteralArg) a).getLiteral());
			} else if (a instanceof RegisterArg) {
				args[i] = MethodBody.Arg.reg(((RegisterArg) a).getRegNum());
			} else {
				return null; // wrapped / unexpected operand -> refuse
			}
		}
		int resultReg = insn.getResult() != null ? insn.getResult().getRegNum() : -1;
		switch (insn.getType()) {
			case RETURN:
			case CONST:
			case MOVE:
			case NEG:
			case NOT:
			case AGET:
			case APUT:
			case NEW_INSTANCE:
			case MOVE_RESULT:
			case ARRAY_LENGTH:
			// MOVE_EXCEPTION/THROW belong to catch handlers the obfuscator wraps reflective calls in.
			// They are reachable only via an exception edge, which the linear+branch interpreter never
			// simulates; snapshotting them (rather than refusing the whole body) lets the normal path
			// fold, and PureFold's default-refuse keeps it sound if execution ever did reach them.
			case MOVE_EXCEPTION:
			case THROW:
				return new MethodBody.Op(insn.getType(), resultReg, args, null, null, null, null, null, -1);
			case CONST_STR:
				return new MethodBody.Op(InsnType.CONST_STR, resultReg, args, null, null, null, null, null, -1,
						((ConstStringNode) insn).getString());
			case CONST_CLASS:
				return new MethodBody.Op(InsnType.CONST_CLASS, resultReg, args,
						((ConstClassNode) insn).getClsType(), null, null, null, null, -1);
			case CAST:
			case CHECK_CAST: {
				Object idx = insn instanceof IndexInsnNode ? ((IndexInsnNode) insn).getIndex() : null;
				return new MethodBody.Op(insn.getType(), resultReg, args,
						idx instanceof ArgType ? (ArgType) idx : null, null, null, null, null, -1);
			}
			case FILL_ARRAY: {
				FillArrayData fad = findFillPayload(insns, ((FillArrayInsn) insn).getTarget());
				if (fad == null) {
					return null;
				}
				List<LiteralArg> lits = fad.getLiteralArgs(fad.getElementType());
				long[] data = new long[lits.size()];
				for (int i = 0; i < data.length; i++) {
					data[i] = lits.get(i).getLiteral();
				}
				return new MethodBody.Op(InsnType.FILL_ARRAY, resultReg, args, null, null, null, null, null, -1, data);
			}
			case ARITH:
				return new MethodBody.Op(InsnType.ARITH, resultReg, args, null, ((ArithNode) insn).getOp(), null, null, null, -1);
			case NEW_ARRAY:
				return new MethodBody.Op(InsnType.NEW_ARRAY, resultReg, args,
						((NewArrayNode) insn).getArrayType().getArrayElement(), null, null, null, null, -1);
			case SGET: {
				FieldInfo f = fieldOf(insn);
				return f == null ? null
						: new MethodBody.Op(InsnType.SGET, resultReg, args, null, null, f, null, null, -1);
			}
			case INVOKE:
				return new MethodBody.Op(InsnType.INVOKE, resultReg, args, null, null, null,
						((InvokeNode) insn).getCallMth(), null, -1);
			case IF: {
				IfNode ifn = (IfNode) insn;
				Integer target = offsetToOpIndex.get(ifn.getTarget());
				return target == null ? null
						: new MethodBody.Op(InsnType.IF, resultReg, args, null, null, null, null, ifn.getOp(), target);
			}
			case GOTO: {
				GotoNode gn = (GotoNode) insn;
				Integer target = offsetToOpIndex.get(gn.getTarget());
				return target == null ? null
						: new MethodBody.Op(InsnType.GOTO, -1, args, null, null, null, null, null, target);
			}
			case CONSTRUCTOR:
				// Currently the prepare-pass raw insns are still pre-ConstructorVisitor (the merge of
				// NEW_INSTANCE + invoke-direct <init>); we only see the explicit INVOKE form here. If we
				// ever start running against post-normalized IR, this case prevents a silent refusal.
				return new MethodBody.Op(InsnType.INVOKE, resultReg, args, null, null, null,
						((InvokeNode) insn).getCallMth(), null, -1);
			default:
				return null; // any unsupported instruction -> refuse
		}
	}

	/**
	 * Record every AES string-decryptor in the class: a {@code static String x(byte[])} whose body
	 * calls {@code javax.crypto.Cipher.doFinal}. This makes the plugin target renamed/duplicated
	 * decryptors (e.g. in a secondary dex) without relying on a hard-coded class name. The decrypt
	 * routine itself is canonical (see {@link #tryDecrypt}); a wrong match is filtered by the
	 * AES-padding + printable-string check.
	 */
	private static void detectDecryptors(ClassNode cls, KeyData keys) {
		for (MethodNode mth : cls.getMethods()) {
			if (isDecryptorMethod(mth, keys.decryptorDesc())) {
				keys.decryptors().add(mth.getMethodInfo().getRawFullId());
			}
		}
	}

	private static boolean isDecryptorMethod(MethodNode mth, String desc) {
		if (mth.isNoCode() || !mth.getAccessFlags().isStatic()) {
			return false;
		}
		if (!mth.getMethodInfo().getShortId().endsWith(desc)) {
			return false;
		}
		InsnNode[] insns = rawInsns(mth);
		if (insns == null) {
			return false;
		}
		for (InsnNode insn : insns) {
			if (insn != null && insn.getType() == InsnType.INVOKE && insn instanceof InvokeNode) {
				MethodInfo callMth = ((InvokeNode) insn).getCallMth();
				if ("doFinal".equals(callMth.getName())
						&& "javax.crypto.Cipher".equals(callMth.getDeclClass().getFullName())) {
					return true;
				}
			}
		}
		return false;
	}

	private static void buildInstanceStringFields(ClassNode cls, KeyData keys) {
		for (MethodNode mth : cls.getMethods()) {
			if (!mth.isConstructor() || mth.isNoCode()) {
				continue;
			}
			InsnNode[] insns = rawInsns(mth);
			if (insns == null) {
				continue;
			}
			Map<Integer, String> regStr = new HashMap<>();
			String pendingString = null;
			Map<String, String> seen = new HashMap<>();
			java.util.Set<String> conflict = new java.util.HashSet<>();
			for (InsnNode insn : insns) {
				if (insn == null) {
					continue;
				}
				switch (insn.getType()) {
					case CONST_STR: {
						RegisterArg res = insn.getResult();
						if (res != null) {
							regStr.put(res.getRegNum(), ((ConstStringNode) insn).getString());
						}
						break;
					}
					case MOVE: {
						RegisterArg res = insn.getResult();
						if (res != null && insn.getArg(0) instanceof RegisterArg) {
							String s = regStr.get(((RegisterArg) insn.getArg(0)).getRegNum());
							if (s != null) {
								regStr.put(res.getRegNum(), s);
							}
						}
						break;
					}
					case INVOKE: {
						pendingString = null;
						if (!(insn instanceof InvokeNode)) {
							break;
						}
						InvokeNode inv = (InvokeNode) insn;
						MethodInfo call = inv.getCallMth();
						if (!call.getDeclClass().equals(cls.getClassInfo()) || inv.getArgsCount() < inv.getFirstArgOffset() + 1) {
							break;
						}
						InsnArg arg = inv.getArg(inv.getFirstArgOffset());
						String in = arg instanceof RegisterArg ? regStr.get(((RegisterArg) arg).getRegNum()) : null;
						if (in == null) {
							break;
						}
						if ("b".equals(call.getName())) {
							pendingString = tryDecodePrefixedRc4(in);
						} else if ("a".equals(call.getName()) && keys.base64StringHelpers().contains(call.getRawFullId())) {
							pendingString = tryDecodeBase64String(in);
						}
						break;
					}
					case MOVE_RESULT: {
						RegisterArg res = insn.getResult();
						if (res != null && pendingString != null) {
							regStr.put(res.getRegNum(), pendingString);
						}
						pendingString = null;
						break;
					}
					case IPUT: {
						FieldInfo f = fieldOf(insn);
						if (f == null || !f.getType().isObject() || !"java.lang.String".equals(f.getType().getObject())) {
							break;
						}
						String val = null;
						InsnArg src = insn.getArg(0);
						if (src instanceof RegisterArg) {
							val = regStr.get(((RegisterArg) src).getRegNum());
						}
						if (val == null) {
							break;
						}
						String id = f.getRawFullId();
						String prev = seen.putIfAbsent(id, val);
						if (prev != null && !prev.equals(val)) {
							conflict.add(id);
						}
						break;
					}
					default:
						break;
				}
			}
			for (Map.Entry<String, String> e : seen.entrySet()) {
				if (!conflict.contains(e.getKey())) {
					keys.instanceStrings().putIfAbsent(e.getKey(), e.getValue());
				}
			}
		}
	}

	private static void detectBase64StringHelpers(ClassNode cls, KeyData keys) {
		for (MethodNode mth : cls.getMethods()) {
			if (!isBase64StringHelper(mth)) {
				continue;
			}
			keys.base64StringHelpers().add(mth.getMethodInfo().getRawFullId());
		}
	}

	private static boolean isBase64StringHelper(MethodNode mth) {
		if (mth.isNoCode()) {
			return false;
		}
		MethodInfo mi = mth.getMethodInfo();
		ArgType ret = mi.getReturnType();
		if (mi.getArgsCount() != 1 || !mi.getArgumentsTypes().get(0).isObject()
				|| ret == null || !ret.isObject() || !"java.lang.String".equals(ret.getObject())) {
			return false;
		}
		InsnNode[] insns = rawInsns(mth);
		if (insns == null) {
			return false;
		}
		boolean hasBase64Decode = false;
		boolean hasStringCtor = false;
		boolean hasNonJdkInvoke = false;
		for (InsnNode insn : insns) {
			if (insn == null || insn.getType() != InsnType.INVOKE || !(insn instanceof InvokeNode)) {
				continue;
			}
			MethodInfo call = ((InvokeNode) insn).getCallMth();
			String decl = call.getDeclClass().getFullName();
			String name = call.getName();
			if ("decode".equals(name) && ("android.util.Base64".equals(decl)
					|| "java.util.Base64.Decoder".equals(decl) || "java.util.Base64$Decoder".equals(decl))) {
				hasBase64Decode = true;
			} else if (call.isConstructor() && "java.lang.String".equals(decl)) {
				hasStringCtor = true;
			} else if (!decl.startsWith("java.")) {
				hasNonJdkInvoke = true;
			}
		}
		return hasBase64Decode && hasStringCtor && !hasNonJdkInvoke;
	}

	private static void buildOne(ClassNode cls, KeyData keys) {
		MethodNode clinit = cls.getClassInitMth();
		if (clinit == null) {
			return;
		}
		InsnNode[] insns = rawInsns(clinit);
		if (insns == null) {
			return;
		}
		// Linear register simulation. Arrays are tracked by register; an `aput` may target either the
		// original `new-array` register or an `sget`-reload of the field, so `sget` of an already-stored
		// array field re-binds the result register to the same array object (which `sput`/aput share).
		Map<Integer, Long> regConst = new HashMap<>();
		Map<Integer, long[]> regArray = new HashMap<>();
		Map<Integer, ArgType> regElem = new HashMap<>();
		Map<String, long[]> fieldArray = new HashMap<>();
		Map<String, ArgType> fieldElem = new HashMap<>();
		for (InsnNode insn : insns) {
			if (insn == null) {
				continue;
			}
			switch (insn.getType()) {
				case CONST: {
					RegisterArg res = insn.getResult();
					if (res != null && insn.getArgsCount() > 0 && insn.getArg(0) instanceof LiteralArg) {
						regConst.put(res.getRegNum(), ((LiteralArg) insn.getArg(0)).getLiteral());
					}
					break;
				}
				case MOVE: {
					// JVM-compiled array initializers `dup` the arrayref, modelled as MOVE: propagate both
					// tracked array references and scalar constants through register copies.
					RegisterArg res = insn.getResult();
					InsnArg src = insn.getArg(0);
					if (res != null && src instanceof RegisterArg) {
						int from = ((RegisterArg) src).getRegNum();
						int to = res.getRegNum();
						if (regArray.containsKey(from)) {
							regArray.put(to, regArray.get(from));
							regElem.put(to, regElem.get(from));
						} else if (regConst.containsKey(from)) {
							regConst.put(to, regConst.get(from));
						}
					}
					break;
				}
				case NEW_ARRAY: {
					RegisterArg res = insn.getResult();
					ArgType elem = ((NewArrayNode) insn).getArrayType().getArrayElement();
					if (res == null || !isIntegral(elem)) {
						break;
					}
					Long sz = regOf(regConst, insn.getArg(0));
					if (sz != null && sz > 0 && sz <= keys.maxArraySize()) {
						regArray.put(res.getRegNum(), new long[sz.intValue()]);
						regElem.put(res.getRegNum(), elem);
					}
					break;
				}
				case SGET: {
					FieldInfo f = fieldOf(insn);
					RegisterArg res = insn.getResult();
					if (f != null && res != null) {
						long[] arr = fieldArray.get(f.getRawFullId());
						if (arr != null) { // reload of a known array field -> same array object
							regArray.put(res.getRegNum(), arr);
							regElem.put(res.getRegNum(), fieldElem.get(f.getRawFullId()));
						}
					}
					break;
				}
				case APUT: {
					InsnArg arrArg = insn.getArg(0);
					if (!(arrArg instanceof RegisterArg)) {
						break;
					}
					int regNum = ((RegisterArg) arrArg).getRegNum();
					long[] arr = regArray.get(regNum);
					if (arr == null) {
						break;
					}
					Long idx = regOf(regConst, insn.getArg(1));
					Long val = regOf(regConst, insn.getArg(2));
					if (idx != null && val != null && idx >= 0 && idx < arr.length) {
						arr[idx.intValue()] = extend(regElem.get(regNum), val);
					}
					break;
				}
				case FILL_ARRAY: {
					InsnArg arrArg = insn.getArg(0);
					if (!(arrArg instanceof RegisterArg)) {
						break;
					}
					int regNum = ((RegisterArg) arrArg).getRegNum();
					long[] arr = regArray.get(regNum);
					if (arr == null) {
						break;
					}
					// At the raw stage the fill-array-data payload is a separate FILL_ARRAY_DATA insn,
					// not yet linked (ProcessInstructionsVisitor does that during decompile); resolve it
					// by its target offset, mirroring that visitor.
					FillArrayData payload = findFillPayload(insns, ((FillArrayInsn) insn).getTarget());
					if (payload == null) {
						break;
					}
					ArgType el = regElem.get(regNum) != null ? regElem.get(regNum) : payload.getElementType();
					List<LiteralArg> vals = payload.getLiteralArgs(el);
					for (int i = 0; i < vals.size() && i < arr.length; i++) {
						arr[i] = extend(el, vals.get(i).getLiteral());
					}
					break;
				}
				case SPUT: {
					FieldInfo field = fieldOf(insn);
					if (field == null) {
						break;
					}
					ArgType ft = field.getType();
					InsnArg src = insn.getArg(0);
					if (ft.isArray() && isIntegral(ft.getArrayElement()) && src instanceof RegisterArg) {
						long[] arr = regArray.get(((RegisterArg) src).getRegNum());
						if (arr != null) {
							ArgType el = regElem.get(((RegisterArg) src).getRegNum());
							fieldArray.put(field.getRawFullId(), arr);
							fieldElem.put(field.getRawFullId(), el != null ? el : ft.getArrayElement());
							keys.arrays().put(field.getRawFullId(), arr); // shared object; later aput fills it
						}
					} else if (isIntegral(ft)) {
						Long v = regOf(regConst, src);
						if (v != null) {
							keys.scalars().put(field.getRawFullId(), extend(ft, v));
						}
					}
					break;
				}
				default:
					break;
			}
		}
	}

	/**
	 * Find the {@code fill-array-data} payload referenced by a {@code FILL_ARRAY} target offset in the
	 * raw (offset-indexed) instruction array, mirroring {@code ProcessInstructionsVisitor}.
	 */
	private static @Nullable FillArrayData findFillPayload(InsnNode[] insns, int target) {
		for (int i = target; i >= 0 && i < insns.length; i++) {
			InsnNode n = insns[i];
			if (n != null && n.getType() != InsnType.NOP) {
				return n.getType() == InsnType.FILL_ARRAY_DATA ? (FillArrayData) n : null;
			}
		}
		return null;
	}

	@Nullable
	private static FieldInfo fieldOf(InsnNode insn) {
		if (insn instanceof IndexInsnNode && ((IndexInsnNode) insn).getIndex() instanceof FieldInfo) {
			return (FieldInfo) ((IndexInsnNode) insn).getIndex();
		}
		return null;
	}

	@Nullable
	static InsnNode[] rawInsns(MethodNode mth) {
		InsnNode[] a = mth.getInstructions();
		if (a != null) {
			return a;
		}
		try {
			mth.load();
			return mth.getInstructions();
		} catch (Throwable t) {
			return null;
		}
	}

	private static @Nullable Long regOf(Map<Integer, Long> reg, InsnArg arg) {
		return (arg instanceof RegisterArg) ? reg.get(((RegisterArg) arg).getRegNum()) : null;
	}
}
