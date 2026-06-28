package jadx.plugins.stringdecrypt;

import java.io.File;
import java.net.URL;

import org.junit.jupiter.api.Test;

import jadx.api.JadxArgs;
import jadx.api.JadxDecompiler;
import jadx.api.JavaClass;
import jadx.core.dex.nodes.ClassNode;
import jadx.core.dex.nodes.FieldNode;
import jadx.core.dex.nodes.MethodNode;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Covers the jadx-gui "Copy as string-decrypt pipeline" snippet generation ({@link StringDecryptGuiActions}):
 * the text contract (helper + exact id / owner predicate) directly, and the node-kind dispatch against real
 * {@link MethodNode}/{@link ClassNode}/{@link FieldNode} parsed from the sample APK.
 */
class GuiActionsTest {

	private static final String APK = "ali-crackme-1.apk";
	private static final String CLASS = "k2015.a1.Check";

	@Test
	void methodSnippet_carriesExactIdAndReflectiveBridge() {
		String id = "da.ba.aa.fa.s(CLjava/lang/String;)Ljava/lang/String;";
		String snippet = StringDecryptGuiActions.methodSnippet(id, "s");

		assertThat(snippet).contains("pipeline(\"s\", \"" + id + "\")");
		// uses the classloader-safe reflective bridge (works for an installed jar), not the typed in-tree API
		assertThat(snippet).contains(".getMethod(\"registerPipeline\", String::class.java, String::class.java, Function::class.java)");
		assertThat(snippet).contains("getById(\"" + StringDecryptPlugin.PLUGIN_ID + "\")");
		assertThat(snippet).contains("frame[\"args\"]");
		assertThat(snippet).doesNotContain("import jadx.plugins.stringdecrypt"); // no plugin types referenced
	}

	@Test
	void ownerSnippet_scopesPredicateToClass() {
		String snippet = StringDecryptGuiActions.ownerSnippet("com.app.Foo", null);

		assertThat(snippet).contains("registerPipeline\", String::class.java, Predicate::class.java, Function::class.java");
		assertThat(snippet).contains("it[\"owner\"] == \"com.app.Foo\"");
		assertThat(snippet).contains("pipelineWhere(\"in-Foo\"");
	}

	@Test
	void fieldSnippet_scopesToDeclaringClass_andNotesField() {
		String fieldId = "com.app.Foo.KEY:[B";
		String snippet = StringDecryptGuiActions.ownerSnippet("com.app.Foo", fieldId);

		assertThat(snippet).contains("it[\"owner\"] == \"com.app.Foo\"");
		assertThat(snippet).contains("field raw id: " + fieldId);
		assertThat(snippet).contains("not a call candidate");
	}

	@Test
	void buildSnippet_dispatchesByNodeKind_onRealNodes() {
		File apk = sampleFile(APK);
		JadxArgs args = new JadxArgs();
		args.getInputFiles().add(apk);
		args.setSkipResources(true);
		try (JadxDecompiler jadx = new JadxDecompiler(args)) {
			jadx.load();
			JavaClass javaCls = jadx.searchJavaClassByOrigFullName(CLASS);
			assertThat(javaCls).as("class %s not found", CLASS).isNotNull();
			ClassNode cls = javaCls.getClassNode();

			String clsSnippet = StringDecryptGuiActions.buildSnippet(cls);
			assertThat(clsSnippet).contains("it[\"owner\"] == \"" + cls.getRawName() + "\"");

			MethodNode mth = cls.getMethods().stream().filter(m -> !m.getMethodInfo().isClassInit()).findFirst().orElseThrow();
			String mthSnippet = StringDecryptGuiActions.buildSnippet(mth);
			assertThat(mthSnippet).contains("\"" + mth.getMethodInfo().getRawFullId() + "\"");

			if (!cls.getFields().isEmpty()) {
				FieldNode fld = cls.getFields().get(0);
				String fldSnippet = StringDecryptGuiActions.buildSnippet(fld);
				assertThat(fldSnippet).contains("field raw id: " + fld.getFieldInfo().getRawFullId());
				assertThat(fldSnippet).contains(fld.getParentClass().getRawName());
			}
		}
	}

	private static File sampleFile(String name) {
		URL res = GuiActionsTest.class.getClassLoader().getResource("test-samples/" + name);
		assertThat(res).as("missing test sample %s", name).isNotNull();
		return new File(res.getFile());
	}
}
