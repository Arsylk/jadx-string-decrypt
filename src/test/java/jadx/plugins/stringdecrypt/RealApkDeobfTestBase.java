package jadx.plugins.stringdecrypt;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jadx.api.JadxArgs;
import jadx.api.JadxDecompiler;
import jadx.api.JavaClass;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Base for real-APK anti-regression tests. Each test decompiles an actual obfuscated APK with the
 * {@link StringDecryptPlugin} passes enabled and asserts the decompiled source of a target class
 * against a checked-in golden fixture.
 *
 * <p>
 * Real samples are the only fixtures we keep: toy/synthetic inputs pass while real obfuscators still
 * break, so the suite asserts on genuine deobfuscation quality. A class is promoted to a golden only
 * once it is verified fully folded/deobfuscated; from then on the golden gates every plugin change.
 *
 * <p>
 * Regenerate goldens after an <i>intended</i> output change with
 * {@code ./gradlew :jadx-plugins:jadx-string-decrypt:test -DupdateGolden=true} and review the diff.
 */
abstract class RealApkDeobfTestBase {

	private static final String SAMPLES_DIR = "test-samples/";
	private static final Path GOLDEN_DIR = Paths.get("src/test/resources/golden");

	private static boolean updateGolden() {
		return Boolean.parseBoolean(System.getProperty("updateGolden", "false"));
	}

	/** Decompile {@code classFqn} from the given test-sample APK with the deobfuscator plugin active. */
	protected String decompile(String apkResource, String classFqn) {
		File apk = sampleFile(apkResource);
		JadxArgs args = new JadxArgs();
		args.getInputFiles().add(apk);
		args.setSkipResources(true); // only the code matters; skip arsc/manifest parsing
		try (JadxDecompiler jadx = new JadxDecompiler(args)) {
			jadx.registerPlugin(new StringDecryptPlugin());
			jadx.load();
			JavaClass cls = jadx.searchJavaClassByOrigFullName(classFqn);
			assertThat(cls)
					.as("class %s not found in %s", classFqn, apkResource)
					.isNotNull();
			String code = cls.getCode();
			dumpActual(classFqn, code); // always written under build/ for inspection
			return code;
		}
	}

	/**
	 * Assert {@code actual} equals the golden fixture {@code goldenName}. When {@code -DupdateGolden}
	 * is set (or the golden does not exist yet) the fixture is (re)written and the assertion passes,
	 * so the first run bootstraps the baseline that later runs are gated against.
	 */
	protected void assertMatchesGolden(String goldenName, String actual) {
		Path golden = GOLDEN_DIR.resolve(goldenName);
		try {
			if (updateGolden() || !Files.exists(golden)) {
				Files.createDirectories(golden.getParent());
				Files.write(golden, actual.getBytes(StandardCharsets.UTF_8));
				System.out.println("[golden] wrote " + golden.toAbsolutePath());
				return;
			}
			String expected = new String(Files.readAllBytes(golden), StandardCharsets.UTF_8);
			assertThat(actual)
					.as("decompiled output drifted from golden %s "
							+ "(re-run with -DupdateGolden=true if the change is intended)", goldenName)
					.isEqualTo(expected);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static void dumpActual(String classFqn, String code) {
		try {
			Path out = Paths.get("build/deobf-actual").resolve(classFqn + ".java");
			Files.createDirectories(out.getParent());
			Files.write(out, code.getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static File sampleFile(String name) {
		URL res = RealApkDeobfTestBase.class.getClassLoader().getResource(SAMPLES_DIR + name);
		assertThat(res).as("missing test sample %s", name).isNotNull();
		return new File(res.getFile());
	}
}
