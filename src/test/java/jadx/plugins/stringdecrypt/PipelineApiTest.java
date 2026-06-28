package jadx.plugins.stringdecrypt;

import java.util.List;

import org.junit.jupiter.api.Test;

import jadx.core.dex.instructions.args.ArgType;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Contract tests for the script-pipeline public API and registry that do not require a full
 * decompilation: registration order, exact/predicate routing, {@link PipelineRegistration} lifecycle,
 * and {@link PipelineResult} factory semantics. The end-to-end pass integration is exercised separately
 * against the real-APK golden (no regression when no pipelines are registered).
 */
class PipelineApiTest {

	private static final String ID_A = "a.b.C.d(Ljava/lang/String;)Ljava/lang/String;";
	private static final String ID_B = "a.b.C.e(I)Ljava/lang/String;";

	@Test
	void registry_routesExactBeforePredicate_inRegistrationOrder() {
		PipelineRegistry registry = new PipelineRegistry();
		assertThat(registry.isEmpty()).isTrue();

		PipelineRegistration exact1 = registry.register(reg(registry, "exact1", ID_A));
		PipelineRegistration pred1 = registry.register(predicate(registry, "pred1"));
		PipelineRegistration exact2 = registry.register(reg(registry, "exact2", ID_A));
		PipelineRegistration exactB = registry.register(reg(registry, "exactB", ID_B));

		assertThat(registry.isEmpty()).isFalse();
		assertThat(registry.all()).containsExactly(exact1, pred1, exact2, exactB);

		// for ID_A: both exact matches (registration order) then the predicate
		assertThat(registry.candidatesFor(ID_A)).containsExactly(exact1, exact2, pred1);
		// for an id with no exact registration: only predicates
		assertThat(registry.candidatesFor("x.Y.z()V")).containsExactly(pred1);
	}

	@Test
	void registry_candidates_nullWhenNothingCouldMatch() {
		PipelineRegistry registry = new PipelineRegistry();
		registry.register(reg(registry, "exactA", ID_A));
		// no predicates registered, and this id has no exact entry -> skip frame entirely
		assertThat(registry.candidatesFor(ID_B)).isNull();
		assertThat(registry.candidatesFor(null)).isNull();
	}

	@Test
	void registry_removeAndClear() {
		PipelineRegistry registry = new PipelineRegistry();
		PipelineRegistration a = registry.register(reg(registry, "a", ID_A));
		PipelineRegistration p = registry.register(predicate(registry, "p"));

		a.remove();
		assertThat(registry.candidatesFor(ID_A)).containsExactly(p); // predicate still matches any id
		assertThat(registry.all()).containsExactly(p);

		registry.clear();
		assertThat(registry.isEmpty()).isTrue();
		assertThat(registry.candidatesFor(ID_A)).isNull();
	}

	@Test
	void registration_disableEnable() {
		PipelineRegistration r = reg(new PipelineRegistry(), "r", ID_A);
		assertThat(r.isEnabled()).isTrue();
		assertThat(r.disable().isEnabled()).isFalse();
		assertThat(r.enable().isEnabled()).isTrue();
		assertThat(r.name()).isEqualTo("r");
		assertThat(r.exactId()).isEqualTo(ID_A);
	}

	@Test
	void result_keepAndControlKinds() {
		assertThat(PipelineResult.keep().kind()).isEqualTo(PipelineResult.Kind.KEEP);
		assertThat(PipelineResult.commentOnly("hi").kind()).isEqualTo(PipelineResult.Kind.COMMENT_ONLY);
		assertThat(PipelineResult.commentOnly("hi").message()).isEqualTo("hi");
		PipelineResult fail = PipelineResult.fail("boom", new IllegalStateException("x"));
		assertThat(fail.kind()).isEqualTo(PipelineResult.Kind.FAIL);
		assertThat(fail.cause()).isInstanceOf(IllegalStateException.class);
	}

	@Test
	void result_replaceFactoriesCarryTypedValues() {
		assertThat(PipelineResult.replaceString("x").value()).isEqualTo("x");
		assertThat(PipelineResult.replaceCharSequence(new StringBuilder("sb")).value()).isEqualTo("sb");
		assertThat(PipelineResult.replaceInt(7).value()).isEqualTo(7);
		assertThat(PipelineResult.replaceBoolean(true).value()).isEqualTo(true);
		assertThat(PipelineResult.replaceChar('z').value()).isEqualTo('z');
		assertThat(PipelineResult.replaceLong(9L).value()).isEqualTo(9L);
		assertThat(PipelineResult.replaceChars(new char[] { 'a', 'b' }).value()).isInstanceOf(char[].class);
		assertThat(PipelineResult.replaceBytes(new byte[] { 1, 2 }).value()).isInstanceOf(byte[].class);
		assertThat(PipelineResult.replaceClass(String.class).value()).isEqualTo(String.class);

		PipelineResult ct = PipelineResult.replaceClassType(ArgType.STRING);
		assertThat(ct.kind()).isEqualTo(PipelineResult.Kind.REPLACE);
		assertThat(ct.value()).isNull();
		assertThat(ct.classType()).isEqualTo(ArgType.STRING);

		PipelineResult nul = PipelineResult.replaceNull(ArgType.STRING);
		assertThat(nul.value()).isSameAs(jadx.plugins.stringdecrypt.eval.TypeMap.NULL_REF);
		assertThat(nul.targetTypeOverride()).isEqualTo(ArgType.STRING);
	}

	@Test
	void result_fluentMetadataAccumulates() {
		PipelineResult r = PipelineResult.replaceString("out")
				.comment("note")
				.cleanupArg(0)
				.cleanupArg(1)
				.cleanupRawArg(3)
				.cleanupReceiver()
				.targetType(ArgType.STRING);
		assertThat(r.message()).isEqualTo("note");
		assertThat(r.cleanupUserArgs()).containsExactly(0, 1);
		assertThat(r.cleanupRawArgs()).containsExactly(3);
		assertThat(r.cleanupReceiverRequested()).isTrue();
		assertThat(r.targetTypeOverride()).isEqualTo(ArgType.STRING);
	}

	@Test
	void plugin_registrationApi() {
		StringDecryptPlugin plugin = new StringDecryptPlugin();
		assertThat(plugin.getPipelines()).isEmpty();

		PipelineRegistration r1 = plugin.pipeline("p1", ID_A, frame -> PipelineResult.keep());
		PipelineRegistration r2 = plugin.pipeline("p2", PipelineMatcher.name("d"), frame -> PipelineResult.keep());
		List<PipelineRegistration> all = plugin.getPipelines();
		assertThat(all).containsExactly(r1, r2);
		assertThat(plugin.apiClassLoader()).isSameAs(PipelineResult.class.getClassLoader());

		r1.remove();
		assertThat(plugin.getPipelines()).containsExactly(r2);
		plugin.clearPipelines();
		assertThat(plugin.getPipelines()).isEmpty();
	}

	// --- helpers ----------------------------------------------------------------------------------

	private static PipelineRegistration reg(PipelineRegistry registry, String name, String id) {
		return new PipelineRegistration(name, id, PipelineMatcher.exact(id), frame -> PipelineResult.keep(), registry);
	}

	private static PipelineRegistration predicate(PipelineRegistry registry, String name) {
		return new PipelineRegistration(name, null, frame -> true, frame -> PipelineResult.keep(), registry);
	}
}
