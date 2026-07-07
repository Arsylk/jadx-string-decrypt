package jadx.plugins.stringdecrypt;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import jadx.api.JadxArgs;
import jadx.api.JadxDecompiler;
import jadx.api.JavaClass;

import static org.assertj.core.api.Assertions.assertThat;

/** Regression for separated StringBuilder mutations: never fold sb.toString() from a fresh empty builder. */
class MutableStringBuilderFoldTest {

	@Test
	void doesNotFoldExternallyMutatedStringBuilderToEmptyString() {
		Path sample = Paths.get("..", "..", "samples", "classes.dex").normalize();
		assertThat(Files.exists(sample)).as("sample dex exists: %s", sample.toAbsolutePath()).isTrue();

		JadxArgs args = new JadxArgs();
		args.getInputFiles().add(sample.toFile());
		args.setSkipResources(true);
		try (JadxDecompiler jadx = new JadxDecompiler(args)) {
			jadx.registerPlugin(new StringDecryptPlugin());
			jadx.load();
			JavaClass cls = jadx.searchJavaClassByOrigFullName("com.nasty.horny.b");
			assertThat(cls).isNotNull();
			String code = cls.getCode();

			assertThat(code).contains("n = sb.toString();");
			assertThat(code).contains("String str2 = this.p.getName() + d;");
			assertThat(code).contains("getInt(str + h, 1)");
			assertThat(code).contains("new d(this.r, str2 + i3 + b)");
			assertThat(code).doesNotContain("n = \"\";");
			assertThat(code).doesNotContain("prefs.getInt(\"\", 1)");
			assertThat(code).doesNotContain("new d(this.r, \"\")");
		}
	}
}
