package jadx.plugins.stringdecrypt;

import org.junit.jupiter.api.Test;

/**
 * Real-sample regression test: the {@code AliCrackme_1} APK ships a string-obfuscated
 * {@code k2015.a1.Check} class. After the deobfuscator runs, the class must be fully folded —
 * no residual decrypt calls or opaque key-table arithmetic — and must match the golden fixture.
 */
class AliCrackme1Test extends RealApkDeobfTestBase {

	private static final String APK = "ali-crackme-1.apk";

	@Test
	void check_isFullyFolded() {
		String code = decompile(APK, "k2015.a1.Check");
		assertMatchesGolden("k2015.a1.Check.java", code);
	}
}
