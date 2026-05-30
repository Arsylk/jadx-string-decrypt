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

import jadx.core.dex.info.FieldInfo;
import jadx.core.dex.info.MethodInfo;
import jadx.core.dex.instructions.ArithNode;
import jadx.core.dex.instructions.FillArrayData;
import jadx.core.dex.instructions.FillArrayInsn;
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
			if (detectDecryptors) {
				detectDecryptors(cls, keys);
			}
			snapshotPureCandidates(cls, keys);
		} catch (Throwable t) {
			// ignore: a class whose constants can't be reconstructed just won't resolve
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
				boolean wasLoaded = mth.getInstructions() != null;
				try {
					scanMethodForMutations(cls, mth, isClinit, keys);
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

	private static void scanMethodForMutations(ClassNode cls, MethodNode mth, boolean isClinit, KeyData keys) {
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

	// ------------------------------------------------------------------------------------------
	// pure-helper body snapshots (for interprocedural folding)
	// ------------------------------------------------------------------------------------------

	/** Whitelisted instruction count above which a method is too large to be a foldable helper. */
	private static final int SNAPSHOT_MAX_INSNS = 512;

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
					keys.bodies().put(mth.getMethodInfo().getRawFullId(), body);
				}
			} catch (Throwable t) {
				// ignore: a method that can't be snapshotted simply won't be folded
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
		return isIntegral(ret) || (ret.isObject() && "java.lang.String".equals(ret.getObject()));
	}

	private static @Nullable MethodBody snapshotBody(MethodNode mth) {
		InsnNode[] insns = rawInsns(mth);
		if (insns == null) {
			return null;
		}
		List<MethodBody.Op> ops = new ArrayList<>();
		for (InsnNode insn : insns) {
			if (insn == null) {
				continue;
			}
			if (ops.size() >= SNAPSHOT_MAX_INSNS) {
				return null;
			}
			MethodBody.Op op = snapshotOp(insn);
			if (op == null) {
				return null; // unsupported opcode -> refuse to snapshot (so it is never folded)
			}
			ops.add(op);
		}
		if (ops.isEmpty()) {
			return null;
		}
		List<RegisterArg> argRegs = mth.getArgRegs();
		int[] regs = new int[argRegs.size()];
		for (int i = 0; i < regs.length; i++) {
			regs[i] = argRegs.get(i).getRegNum();
		}
		return new MethodBody(ops.toArray(new MethodBody.Op[0]), regs);
	}

	private static @Nullable MethodBody.Op snapshotOp(InsnNode insn) {
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
				return new MethodBody.Op(insn.getType(), resultReg, args, null, null, null, null);
			case CAST: {
				Object idx = insn instanceof IndexInsnNode ? ((IndexInsnNode) insn).getIndex() : null;
				return new MethodBody.Op(InsnType.CAST, resultReg, args,
						idx instanceof ArgType ? (ArgType) idx : null, null, null, null);
			}
			case ARITH:
				return new MethodBody.Op(InsnType.ARITH, resultReg, args, null, ((ArithNode) insn).getOp(), null, null);
			case NEW_ARRAY:
				return new MethodBody.Op(InsnType.NEW_ARRAY, resultReg, args,
						((NewArrayNode) insn).getArrayType().getArrayElement(), null, null, null);
			case SGET: {
				FieldInfo f = fieldOf(insn);
				return f == null ? null : new MethodBody.Op(InsnType.SGET, resultReg, args, null, null, f, null);
			}
			case INVOKE:
				return new MethodBody.Op(InsnType.INVOKE, resultReg, args, null, null, null, ((InvokeNode) insn).getCallMth());
			default:
				return null; // control flow or any unsupported instruction
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
