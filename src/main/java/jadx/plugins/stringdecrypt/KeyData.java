package jadx.plugins.stringdecrypt;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Compile-time constants reconstructed from {@code <clinit>}, keyed by {@code FieldInfo.rawFullId}:
 * static integral arrays used as "key tables", and scalar static fields used as split constants.
 * Also holds the {@code MethodInfo.rawFullId} of every auto-detected AES string-decryptor method.
 * Written once in the prepare pass, read-only during the (threaded) decompile pass.
 */
public final class KeyData {

	private final Map<String, long[]> arrays = new ConcurrentHashMap<>();
	private final Map<String, Long> scalars = new ConcurrentHashMap<>();
	private final Map<String, String> instanceStrings = new ConcurrentHashMap<>();
	private final Set<String> decryptors = ConcurrentHashMap.newKeySet();
	private final Set<String> base64StringHelpers = ConcurrentHashMap.newKeySet();
	private final Map<String, MethodBody> bodies = new ConcurrentHashMap<>();
	private final Map<String, Map<Long, String>> inlineXorStrings = new ConcurrentHashMap<>();
	private final Set<String> mutableFields = ConcurrentHashMap.newKeySet();
	private int maxArraySize = StringDecryptOptions.DEFAULT_MAX_TABLE_SIZE;
	private String decryptorDesc = StringDecryptOptions.DEFAULT_DECRYPTOR_DESC;

	public Map<String, long[]> arrays() {
		return arrays;
	}

	/** Upper bound on reconstructed static-array length (configurable; guards memory). */
	int maxArraySize() {
		return maxArraySize;
	}

	void setMaxArraySize(int maxArraySize) {
		this.maxArraySize = maxArraySize;
	}

	/** Method descriptor a candidate must have to be recognised as a string decryptor. */
	String decryptorDesc() {
		return decryptorDesc;
	}

	void setDecryptorDesc(String decryptorDesc) {
		this.decryptorDesc = decryptorDesc;
	}

	/**
	 * {@code FieldInfo.rawFullId} of every static field written (SPUT, or APUT into its array) outside
	 * its declaring class's {@code <clinit>}. Reads of these must not be folded to the reconstructed
	 * {@code <clinit>} value — the runtime value may differ. Populated in the prepare pass.
	 */
	Set<String> mutableFields() {
		return mutableFields;
	}

	/** A reconstructed static field whose reads are safe to fold to their {@code <clinit>} value. */
	boolean isImmutable(String fieldId) {
		return !mutableFields.contains(fieldId);
	}

	/**
	 * Immutable snapshots of pure static helper bodies (keyed by {@code MethodInfo.rawFullId}), used
	 * by the interprocedural folder. Populated in the prepare pass, read-only during decompile.
	 */
	Map<String, MethodBody> bodies() {
		return bodies;
	}

	/** Precomputed decode table for large {@code static String(int)} inline byte-array XOR helpers. */
	Map<String, Map<Long, String>> inlineXorStrings() {
		return inlineXorStrings;
	}

	public Map<String, Long> scalars() {
		return scalars;
	}

	/** Constructor-initialized instance String fields proven constant and not later mutated. */
	Map<String, String> instanceStrings() {
		return instanceStrings;
	}

	public Set<String> decryptors() {
		return decryptors;
	}

	/** String(String) helpers whose body directly invokes Base64.decode and new String(...). */
	Set<String> base64StringHelpers() {
		return base64StringHelpers;
	}

	public int size() {
		return arrays.size() + scalars.size();
	}
}
