package jadx.plugins.stringdecrypt;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jadx.api.metadata.ICodeNodeRef;
import jadx.api.plugins.gui.JadxGuiContext;
import jadx.core.dex.nodes.ClassNode;
import jadx.core.dex.nodes.FieldNode;
import jadx.core.dex.nodes.MethodNode;

/**
 * jadx-gui quality-of-life: a code-area context-menu action that copies a ready-to-paste
 * {@code .jadx.kts} <b>script pipeline</b> skeleton for whatever the caret is on (class / method / field).
 *
 * <p>
 * The emitted snippet uses the classloader-safe {@link StringDecryptPlugin#registerPipeline} bridge (see
 * {@link ScriptBridge}), so it runs whether the plugin is bundled or an <em>installed jar</em> — the same
 * deployment a GUI user has. For a method it targets the exact raw id; for a class/field it scopes a
 * predicate to the declaring class. Registered only when a GUI context is present (no-op on the CLI).
 */
final class StringDecryptGuiActions {

	private static final Logger LOG = LoggerFactory.getLogger(StringDecryptGuiActions.class);

	private static final String MENU_NAME = "Copy as string-decrypt pipeline";

	private StringDecryptGuiActions() {
	}

	static void register(JadxGuiContext gui) {
		// no keybinding — context-menu only, per request; enabled for class/method/field nodes
		gui.addPopupMenuAction(MENU_NAME, StringDecryptGuiActions::isSupported, null, node -> {
			String snippet = buildSnippet(node);
			if (snippet == null) {
				return;
			}
			gui.copyToClipboard(snippet);
			LOG.info("string-decrypt: copied pipeline snippet to clipboard for {}", describe(node));
		});
	}

	private static Boolean isSupported(@Nullable ICodeNodeRef node) {
		return node instanceof MethodNode || node instanceof ClassNode || node instanceof FieldNode;
	}

	static @Nullable String buildSnippet(ICodeNodeRef node) {
		if (node instanceof MethodNode) {
			MethodNode mth = (MethodNode) node;
			return methodSnippet(mth.getMethodInfo().getRawFullId(), mth.getMethodInfo().getName());
		}
		if (node instanceof ClassNode) {
			ClassNode cls = (ClassNode) node;
			return ownerSnippet(cls.getRawName(), null);
		}
		if (node instanceof FieldNode) {
			FieldNode fld = (FieldNode) node;
			// a field is not itself a call candidate; scope to its declaring class and note the field
			return ownerSnippet(fld.getParentClass().getRawName(), fld.getFieldInfo().getRawFullId());
		}
		return null;
	}

	// --- snippet builders -------------------------------------------------------------------------

	static String methodSnippet(String rawId, String methodName) {
		StringBuilder sb = new StringBuilder();
		header(sb, "pipeline for " + rawId);
		preamble(sb, false);
		sb.append("fun pipeline(name: String, methodId: String, body: (Map<String, Any?>) -> Any?) =\n");
		sb.append("    stringDecrypt.javaClass\n");
		sb.append("        .getMethod(\"registerPipeline\", String::class.java, String::class.java, Function::class.java)\n");
		sb.append("        .invoke(stringDecrypt, name, methodId, Function<Map<String, Any?>, Any?> { body(it) })\n\n");
		sb.append("pipeline(\"").append(escape(methodName)).append("\", \"").append(escape(rawId)).append("\") { frame ->\n");
		sb.append("    val args = frame[\"args\"] as Array<*>\n");
		todoBody(sb);
		sb.append("    null\n");
		sb.append("}\n");
		return sb.toString();
	}

	static String ownerSnippet(String rawClassName, @Nullable String fieldRawId) {
		StringBuilder sb = new StringBuilder();
		header(sb, "pipeline scoped to " + rawClassName + (fieldRawId != null ? " (from field " + fieldRawId + ")" : ""));
		preamble(sb, true);
		sb.append("fun pipelineWhere(name: String, where: (Map<String, Any?>) -> Boolean, body: (Map<String, Any?>) -> Any?) =\n");
		sb.append("    stringDecrypt.javaClass\n");
		sb.append("        .getMethod(\"registerPipeline\", String::class.java, Predicate::class.java, Function::class.java)\n");
		sb.append("        .invoke(stringDecrypt, name,\n");
		sb.append("            Predicate<Map<String, Any?>> { where(it) },\n");
		sb.append("            Function<Map<String, Any?>, Any?> { body(it) })\n\n");
		if (fieldRawId != null) {
			sb.append("// note: a field is not a call candidate; this matches calls whose owner is the field's class.\n");
			sb.append("//       field raw id: ").append(fieldRawId).append('\n');
		}
		sb.append("pipelineWhere(\"in-").append(escape(simpleName(rawClassName)))
				.append("\", { it[\"owner\"] == \"").append(escape(rawClassName)).append("\" }) { frame ->\n");
		sb.append("    val args = frame[\"args\"] as Array<*>\n");
		todoBody(sb);
		sb.append("    null\n");
		sb.append("}\n");
		return sb.toString();
	}

	// --- shared snippet parts ---------------------------------------------------------------------

	private static void header(StringBuilder sb, String what) {
		sb.append("/**\n");
		sb.append(" * string-decrypt ").append(what).append("\n");
		sb.append(" * Paste into a *.jadx.kts file (keep one copy of the header/helpers if you combine several).\n");
		sb.append(" * Pipelines run AFTER the built-in decryption/folding, so frame[\"args\"] are already resolved.\n");
		sb.append(" */\n");
	}

	private static void preamble(StringBuilder sb, boolean needsPredicate) {
		sb.append("import jadx.core.dex.instructions.args.ArgType\n");
		sb.append("import java.util.function.Function\n");
		if (needsPredicate) {
			sb.append("import java.util.function.Predicate\n");
		}
		sb.append('\n');
		sb.append("val jadx = getJadxInstance()\n");
		sb.append("val stringDecrypt = jadx.pluginContext.plugins().getById(\"")
				.append(StringDecryptPlugin.PLUGIN_ID).append("\")?.pluginInstance\n");
		sb.append("    ?: error(\"string-decrypt plugin is not installed/enabled\")\n\n");
	}

	private static void todoBody(StringBuilder sb) {
		sb.append("    // TODO: read the resolved args and return the replacement, or null to keep.\n");
		sb.append("    //   return a String / boxed scalar / array / java.lang.Class to replace with that literal,\n");
		sb.append("    //   an ArgType for a `.class` literal of an app type, or a Map action (value/comment/...).\n");
		sb.append("    // val key = args.getOrNull(0) as? Char ?: return@pipeline null\n");
		sb.append("    // val input = args.getOrNull(1) as? String ?: return@pipeline null\n");
		sb.append("    // input.map { (it.code xor key.code).toChar() }.joinToString(\"\")\n");
	}

	private static String simpleName(String rawClassName) {
		int dot = rawClassName.lastIndexOf('.');
		String name = dot >= 0 ? rawClassName.substring(dot + 1) : rawClassName;
		return name.replace('$', '.');
	}

	private static String escape(String s) {
		return s.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	private static String describe(ICodeNodeRef node) {
		if (node instanceof MethodNode) {
			return ((MethodNode) node).getMethodInfo().getRawFullId();
		}
		if (node instanceof FieldNode) {
			return ((FieldNode) node).getFieldInfo().getRawFullId();
		}
		if (node instanceof ClassNode) {
			return ((ClassNode) node).getRawName();
		}
		return String.valueOf(node);
	}
}
