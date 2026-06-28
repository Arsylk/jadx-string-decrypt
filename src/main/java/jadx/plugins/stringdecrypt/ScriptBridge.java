package jadx.plugins.stringdecrypt;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import jadx.core.dex.info.MethodInfo;
import jadx.core.dex.instructions.args.ArgType;
import jadx.core.dex.nodes.InsnNode;

/**
 * Classloader-safe adapter behind {@link StringDecryptPlugin#registerPipeline}. It lets a standalone
 * {@code .jadx.kts} script register a replacement pipeline <b>without ever referencing a plugin type</b>.
 *
 * <p>
 * Why this exists: when the plugin is an installed jar, jadx loads it in its own child
 * {@code URLClassLoader} ({@code JadxExternalPluginsLoader}), while a script is compiled against — and
 * evaluated in — the app classloader's classpath (jadx-script's {@code dependenciesFromCurrentContext}).
 * The plugin jar is therefore absent from the script's compile classpath, and even an {@code @DependsOn}
 * would load a <em>second</em>, incompatible copy of {@code PipelineResult}/{@code ScriptPipeline}. So the
 * typed {@link StringDecryptPlugin#pipeline} API cannot work across that split.
 *
 * <p>
 * The bridge crosses the boundary using only types loaded by the <em>shared</em> parent classloader:
 * JDK {@link Function}/{@link Predicate}/{@link Map} and jadx-core {@link ArgType}/{@link InsnNode}. The
 * script supplies a {@code Function<Map,Object>}; this class — running plugin-side — builds the frame
 * snapshot, interprets the returned value, and constructs every {@link PipelineResult}/{@link PipelineFrame}
 * itself. No plugin-defined type ever needs to be loaded by the script's classloader.
 *
 * <h3>Frame map (input to the callback)</h3>
 * <ul>
 * <li>{@code "id"} — raw full method id {@code pkg.Cls.m(args)ret} (String, may be null)</li>
 * <li>{@code "owner"} — declaring class raw name (String)</li>
 * <li>{@code "name"} — method name (String)</li>
 * <li>{@code "shortId"} — short descriptor {@code name(args)ret} (String)</li>
 * <li>{@code "static"} — Boolean</li>
 * <li>{@code "returnType"} / {@code "targetType"} — jadx {@link ArgType}</li>
 * <li>{@code "argTypes"} — {@link ArgType}{@code []} of the declared (receiver-excluded) args</li>
 * <li>{@code "userArgCount"} — Integer</li>
 * <li>{@code "args"} — {@code Object[]} of the resolved declared args (String / boxed scalar / array /
 * {@code Class}); a {@code null} entry means "not a compile-time constant"</li>
 * <li>{@code "receiver"} / {@code "receiverType"} — resolved instance value and its {@link ArgType}</li>
 * </ul>
 * The matcher predicate receives the same map minus the (cost-bearing) {@code "args"}/{@code "receiver"}.
 *
 * <h3>Return value (output of the callback)</h3>
 * <ul>
 * <li>{@code null} — keep (decline; the next pipeline gets a turn)</li>
 * <li>{@code String} / boxed scalar / any array / {@code Class<?>} — replace with that literal</li>
 * <li>jadx {@link ArgType} — replace with a {@code .class} literal of that type (handles app types)</li>
 * <li>jadx {@link InsnNode} — replace with caller-built IR (advanced escape hatch)</li>
 * <li>{@code Map<String,Object>} — a structured action, keys: {@code value}, {@code classType}(ArgType),
 * {@code class}(Class), {@code nullType}(ArgType), {@code insn}(InsnNode), {@code keep}(Boolean),
 * {@code fail}(String), {@code comment}(String), {@code targetType}(ArgType),
 * {@code cleanupArgs}(int[]/Iterable&lt;Integer&gt;/Integer), {@code cleanupReceiver}(Boolean)</li>
 * </ul>
 */
final class ScriptBridge {

	private ScriptBridge() {
	}

	/** Wrap a shared-type callback as a {@link ScriptPipeline} the existing resolver can run. */
	static ScriptPipeline asPipeline(Function<Map<String, Object>, Object> callback) {
		return frame -> interpret(callback.apply(fullFrame(frame)));
	}

	/** Wrap a shared-type predicate as a {@link PipelineMatcher} (consulted with the light frame map). */
	static PipelineMatcher asMatcher(Predicate<Map<String, Object>> matcher) {
		return frame -> matcher.test(lightFrame(frame));
	}

	// --- frame snapshots --------------------------------------------------------------------------

	private static Map<String, Object> lightFrame(PipelineFrame f) {
		Map<String, Object> m = new HashMap<>();
		m.put("id", f.rawFullId());
		MethodInfo c = f.call();
		if (c != null) {
			m.put("owner", c.getDeclClass().getType().getObject());
			m.put("name", c.getName());
			m.put("shortId", c.getShortId());
		}
		m.put("static", f.isStaticCall());
		m.put("returnType", f.returnType());
		m.put("targetType", f.targetType());
		int n = f.userArgCount();
		m.put("userArgCount", n);
		ArgType[] argTypes = new ArgType[n];
		for (int i = 0; i < n; i++) {
			PipelineValue v = f.arg(i);
			argTypes[i] = v == null ? null : v.type();
		}
		m.put("argTypes", argTypes);
		return m;
	}

	private static Map<String, Object> fullFrame(PipelineFrame f) {
		Map<String, Object> m = lightFrame(f);
		int n = f.userArgCount();
		Object[] args = new Object[n];
		for (int i = 0; i < n; i++) {
			PipelineValue v = f.arg(i);
			args[i] = v == null ? null : resolveValue(v);
		}
		m.put("args", args);
		PipelineValue rec = f.receiver();
		if (rec != null) {
			m.put("receiver", resolveValue(rec));
			m.put("receiverType", rec.type());
		}
		return m;
	}

	/** Resolve one value to a plain boxed Java object (or {@code null} when it is not a constant). */
	private static @Nullable Object resolveValue(PipelineValue v) {
		Object o = v.object();
		if (o != null) {
			return o;
		}
		ArgType t = v.type();
		if (ArgType.BOOLEAN.equals(t)) {
			return v.booleanValue();
		}
		if (ArgType.BYTE.equals(t)) {
			return v.byteValue();
		}
		if (ArgType.CHAR.equals(t)) {
			return v.charValue();
		}
		if (ArgType.SHORT.equals(t)) {
			return v.shortValue();
		}
		if (ArgType.INT.equals(t)) {
			return v.intValue();
		}
		if (ArgType.LONG.equals(t)) {
			return v.longValue();
		}
		if (ArgType.FLOAT.equals(t)) {
			return v.floatValue();
		}
		if (ArgType.DOUBLE.equals(t)) {
			return v.doubleValue();
		}
		return null;
	}

	// --- result interpretation --------------------------------------------------------------------

	/** Turn the callback's plain return value into a {@link PipelineResult}. */
	static PipelineResult interpret(@Nullable Object r) {
		if (r == null) {
			return PipelineResult.keep();
		}
		if (r instanceof PipelineResult) {
			return (PipelineResult) r; // a Java caller may mix styles; harmless for scripts that can't see the type
		}
		if (r instanceof Map) {
			return interpretMap((Map<?, ?>) r);
		}
		return toReplace(r);
	}

	/** A bare value: ArgType -> class literal, InsnNode -> raw IR, else route through ReplacementFactory. */
	private static PipelineResult toReplace(Object value) {
		if (value instanceof ArgType) {
			return PipelineResult.replaceClassType((ArgType) value);
		}
		if (value instanceof InsnNode) {
			return PipelineResult.replaceInsn((InsnNode) value);
		}
		return PipelineResult.replaceObject(value);
	}

	private static PipelineResult interpretMap(Map<?, ?> m) {
		Object fail = m.get("fail");
		if (fail instanceof String) {
			return PipelineResult.fail((String) fail);
		}
		PipelineResult res = baseResult(m);
		Object comment = m.get("comment");
		if (comment instanceof String) {
			res.comment((String) comment);
		}
		Object targetType = m.get("targetType");
		if (targetType instanceof ArgType) {
			res.targetType((ArgType) targetType);
		}
		applyCleanupSpec(res, m.get("cleanupArgs"));
		if (Boolean.TRUE.equals(m.get("cleanupReceiver"))) {
			res.cleanupReceiver();
		}
		return res;
	}

	private static PipelineResult baseResult(Map<?, ?> m) {
		if (Boolean.TRUE.equals(m.get("keep"))) {
			return PipelineResult.keep();
		}
		Object classType = m.get("classType");
		if (classType instanceof ArgType) {
			return PipelineResult.replaceClassType((ArgType) classType);
		}
		Object cls = m.get("class");
		if (cls instanceof Class) {
			return PipelineResult.replaceClass((Class<?>) cls);
		}
		Object nullType = m.get("nullType");
		if (nullType instanceof ArgType) {
			return PipelineResult.replaceNull((ArgType) nullType);
		}
		Object insn = m.get("insn");
		if (insn instanceof InsnNode) {
			return PipelineResult.replaceInsn((InsnNode) insn);
		}
		if (m.containsKey("value")) {
			Object value = m.get("value");
			return value == null ? PipelineResult.keep() : toReplace(value);
		}
		// no replacement payload: a comment-only action (handled by the caller adding the comment)
		Object comment = m.get("comment");
		return comment instanceof String ? PipelineResult.commentOnly((String) comment) : PipelineResult.keep();
	}

	private static void applyCleanupSpec(PipelineResult res, @Nullable Object spec) {
		if (spec == null) {
			return;
		}
		if (spec instanceof int[]) {
			for (int i : (int[]) spec) {
				res.cleanupArg(i);
			}
		} else if (spec instanceof Integer) {
			res.cleanupArg((Integer) spec);
		} else if (spec instanceof Iterable) {
			for (Object o : (Iterable<?>) spec) {
				if (o instanceof Number) {
					res.cleanupArg(((Number) o).intValue());
				}
			}
		}
	}
}
