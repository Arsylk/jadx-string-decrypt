# Script Defined Pipeline Replacement Plan

This document describes the best implementation path for user-defined
`.jadx.kts` pipelines in the `string-decrypt` / Constant Deobfuscator plugin.
The goal is to let a user define, from their normal jadx script, how a matched
method call or call chain should be evaluated and what should replace it in
jadx IR.

If this feature is applied to the historical `1.2.2` plugin line, it is a
minor-release feature suitable for `1.3.0`. In the current workspace, the source
already advertises `1.4.0`, so the actual version bump should follow
`AGENT.md`: use the next minor version available on that line, and keep
`build.gradle.kts`, `StringDecryptPlugin.VERSION`, generated `BuildInfo`, and
release tags aligned.

---

## STATUS (v1.10.0 — Phases 1–8 DONE, including the standalone-jar classloader bridge)

Implemented and green (all tests pass, golden unchanged with no pipelines registered):
- **Phase 1** was already done before this work: the value→IR boundary lives in `ReplacementFactory`
  (`makeReplacementInsn`/`copyReplacementMetadata`/`isCompatibleReplacementValue`/array+scalar+class
  helpers). The resolver seam is `Resolver`/`ResolveContext`. No re-extraction needed.
- **Phase 2** public API: `ScriptPipeline`, `PipelineMatcher` (+`exact/owner/name/returns/descriptor/
  anyOf/allOf`), `PipelineFrame` (`arg`/`receiver`/`rawArg`/`call`/`rawFullId`/`targetType`/…),
  `PipelineValue` (memoized; `object`/`string`/`bytes`/`chars`/scalars/arrays/`classValue`/`classType`/
  raw escape hatches), `PipelineResult` (`Kind` + `keep`/`commentOnly`/`fail`/`replace…` factories +
  fluent `comment`/`cleanupArg`/`targetType`), `PipelineRegistration`.
- **Phase 3** registry: package-private `PipelineRegistry` (exact-id `ConcurrentHashMap` fast path +
  `CopyOnWriteArrayList` predicates + global order); `StringDecryptPlugin.pipeline(name,id,cb)` /
  `pipeline(name,matcher,cb)` / `clearPipelines` / `getPipelines` / `apiClassLoader`. The pass holds a
  **live registry reference** (not a snapshot).
- **Phase 4** wiring: `ScriptPipelineResolver` converts the first `REPLACE` to IR via `ReplacementFactory`
  (and `ConstClassNode` for `replaceClassType`), applies cleanup to the pass's use-list-validated
  `builtArrays`, surfaces `comment()`/`note()` in the method summary, and never aborts decompilation on a
  callback exception (logs + declines). Option `string-decrypt.script-pipelines` (default on). A basic
  classloader-mismatch diagnostic is in place.
- **Phase 5** (Kotlin docs): the README "Script pipelines" section and `examples/*.jadx.kts` show the
  extension-function-free lambda style against `PipelineFrame`/`PipelineResult`; the plugin stays
  Java-only (no hard `jadx-script` dependency).
- **Phase 6** (standalone classloader — SOLVED **without** touching `jadx-script`): `ScriptBridge` +
  `StringDecryptPlugin.registerPipeline(name, id|Predicate, Function<Map,Object>)`. An installed plugin
  loads in its own `URLClassLoader` (`JadxExternalPluginsLoader`), absent from the script's
  `dependenciesFromCurrentContext(wholeClasspath)` compile classpath, so a script can't reference plugin
  types (and `@DependsOn` would dual-load incompatible copies). The bridge crosses the split with **shared
  types only** (JDK `Function`/`Predicate`/`Map` + jadx-core `ArgType`/`InsnNode`): the plugin builds the
  frame `Map` and interprets the returned `Object` (`null`=keep; String/boxed/array/`Class`=value;
  `ArgType`=class literal; `InsnNode`=raw IR; `Map`=structured action) into a `PipelineResult` plugin-side.
  A script invokes it reflectively, so nothing it touches is loaded by the plugin's classloader. Both
  fronts feed the same `PipelineRegistry`/`ScriptPipelineResolver`/`ReplacementFactory` engine.
- **Phase 7** tests (25 total, all green): `PipelineApiTest` (8 contract), `PipelinePassTest` (5 real-APK
  typed-API end-to-end), `ReplacementMatrixTest` (7: the full value→IR surface), and `StandaloneBridgeTest`
  (4: reflective shared-type registration replaces end-to-end + frame-map shape, predicate decline,
  `interpret` return-value contract, `Map`-action contract).
- **Phase 8** docs: README "Script pipelines" + "Installed-jar (standalone) pipelines" sections, four
  runnable `examples/*.jadx.kts` (`xor-char-string`, `char-array-result`, `class-result`,
  `standalone-bridge`), AGENT.md invariants (pipelines last, live registry, byte-identical with none
  registered, the two registration fronts, the `registerPlugin`/`load()` test gotcha), and the new options
  in both option tables.

### KEY DEVIATION from this plan (per user direction)
**Pipelines run LAST, not first.** The plan (§"Pipeline Execution Order") put scripts first so user
intent overrides built-ins. The user's correct call: the built-in decryption/folding/de-indirection are
the automatic primary deobfuscation, and pipelines operate *after* — on already-resolved values,
handling the app-specific calls the built-ins declined. So `ScriptPipelineResolver` is the **last**
resolver in `StringDecryptPass`. Consequence: a pipeline only sees candidates the built-ins left (their
frame args are already decrypted/folded because wrapped producers resolve inner-first), and cannot
override a built-in result at the same candidate. Update §"Pipeline Execution Order In The Pass"
accordingly.

### Test-harness gotcha (important for any future script test)
`JadxDecompiler.registerPlugin(p)` is **discarded** by `jadx.load()` (it calls `allPlugins.clear()` then
re-resolves from the loader/SPI). Register pipelines on jadx's *own* loaded instance, retrieved via
`jadx.getPluginManager().getResolvedPluginContexts()` → `getPluginInstance()`, **after `load()` but
before the lazy `getCode()`** — which works precisely because the registry is a live reference. This is
also the real `.jadx.kts` flow.

### Remaining (not yet done)
All core phases (1–8) are done, **including the standalone-jar path** (Phase 6) — solved with the
shared-type `ScriptBridge`/`registerPipeline` reflection bridge rather than the originally-planned
`jadx-script` changes (appending plugin jars to the compile classpath etc.), since the user constraint was
to make standalone work **without modifying `jadx-script`**. The typed in-tree API and the standalone
bridge coexist and share one engine. The legacy mismatch diagnostic
(`ScriptPipelineResolver.sameApiClassLoader` + `StringDecryptPlugin.apiClassLoader()`) is retained as a
safety net for the `@DependsOn` dual-load mistake.

Intentionally deferred (designed, not built): the optional convenience DSL, predicate-matcher helpers
beyond the basics, and the `PipelineEvaluationBackend` (smalivm/external-oracle) extension point.

---

## Target User Experience

The script author should not need to manually walk jadx IR for common cases.
They should be able to register an exact method pipeline, access all call
arguments as typed values, run arbitrary Kotlin/JDK code, and return a typed
replacement result.

Example, exact method call:

```kotlin
import jadx.plugins.stringdecrypt.PipelineResult
import jadx.plugins.stringdecrypt.StringDecryptPlugin

val jadx = getJadxInstance()
// After the planned script helper exposes plugin lookup:
val stringDecrypt = jadx.pluginRuntime(StringDecryptPlugin.PLUGIN_ID)
    .pluginInstance as StringDecryptPlugin

stringDecrypt.pipeline(
    name = "sample-xor-char-string",
    methodId = "da.ba.aa.fa.s(CLjava/lang/String;)Ljava/lang/String;",
) { frame ->
    val key = frame.arg(0).charValue() ?: return@pipeline PipelineResult.keep()
    val input = frame.arg(1).string() ?: return@pipeline PipelineResult.keep()

    val out = input.map { ch -> (ch.code xor key.code).toChar() }.joinToString("")
    PipelineResult.replaceString(out)
        .cleanupArg(1)
        .comment("\"$input\" -> \"$out\"")
}
```

Example, context-receiver style requested by the user:

```kotlin
import jadx.plugins.stringdecrypt.PipelineFrame
import jadx.plugins.stringdecrypt.PipelineResult

context(PipelineFrame)
fun decodeXorString(): PipelineResult {
    val key = arg(0).charValue() ?: return PipelineResult.keep()
    val input = arg(1).string() ?: return PipelineResult.keep()
    return PipelineResult.replaceString(
        input.map { ch -> (ch.code xor key.code).toChar() }.joinToString("")
    )
}

stringDecrypt.pipeline("sample-xor", "da.ba.aa.fa.s(CLjava/lang/String;)Ljava/lang/String;") { frame ->
    with(frame) { decodeXorString() }
}
```

Context receivers must be treated as a convenience, not the only API shape,
because the installed `jadx-script` Kotlin version and compiler flags may vary.
The fallback should always be:

```kotlin
fun PipelineFrame.decodeXorString(): PipelineResult {
    val key = arg(0).charValue() ?: return PipelineResult.keep()
    val input = arg(1).string() ?: return PipelineResult.keep()
    return PipelineResult.replaceString(input)
}

stringDecrypt.pipeline("sample-xor", methodId) { frame -> frame.decodeXorString() }
```

The important point: the user writes decoding logic against `PipelineFrame` and
`PipelineValue`, not against raw `InvokeNode` / `InsnArg` unless they choose to.

## Goals

1. Let `.jadx.kts` define custom method-call replacement pipelines.
2. Make argument access easy from the point of use:
   - `frame.arg(0)` is the first logical method argument.
   - `frame.receiver()` is the instance receiver when present.
   - `frame.rawArg(i)` remains available for users who need raw invoke layout.
3. Reuse the plugin's existing static IR evaluators:
   - `Evaluator` for integral expressions and array reconstruction.
   - `ObjectEvaluator` for strings, arrays, `Class<?>`, boxed scalars, and
     JDK-backed pure calls.
   - `JdkInterpreter` for whitelisted JDK math/string/reflection helpers.
4. Keep replacement generation inside the plugin:
   - Scripts return `PipelineResult`.
   - The plugin converts it to valid jadx IR with copied metadata and result
     registers.
5. Support a large replacement surface:
   - `String`, `CharSequence`.
   - primitive and boxed scalars.
   - primitive arrays including `char[]`, `byte[]`, `boolean[]`, `float[]`,
     and `double[]`.
   - object arrays where every element can be represented safely.
   - `Class<?>`, `Class<?>[]`, `ArgType`, and class-name/class-descriptor
     forms.
   - typed null references.
   - raw `InsnNode` for users who intentionally need full jadx IR control.
6. Avoid classloader/type identity traps between:
   - jadx itself,
   - `jadx-script`,
   - the in-tree bundled plugin,
   - later standalone marketplace plugin jars,
   - any script `@DependsOn` dependencies.
7. Keep the first implementation static-only. Do not add a smalivm/Dex runtime
   backend in this feature. Leave a future backend interface open.

## Non-Goals For The First Implementation

1. No general Android/Dex VM execution.
2. No arbitrary object materialization in decompiled code. If jadx cannot
   represent the value as a literal, array literal, class literal, null, or
   caller-provided `InsnNode`, the replacement must refuse.
3. No side-effectful helper evaluation. Existing purity gates remain mandatory.
4. No implicit network or dependency download from scripts.
5. No promise that a script can safely mutate jadx IR directly. Raw IR escape
   hatches exist, but the recommended API is typed `PipelineResult`.

## Current Plugin Grounding

The current plugin already has most of the machinery needed for script
pipelines:

- `StringDecryptPlugin` registers:
  - `KeyTablesPass` as a prepare pass.
  - `StringDecryptPass` as a decompile pass.
- `KeyTablesPass` reconstructs static tables/scalars, snapshots pure helper
  bodies, tracks runtime-mutated statics, and detects decryptors.
- `StringDecryptPass` already replaces values before `RegionMakerVisitor`.
- `StringDecryptPass.tryReplace` currently chooses among:
  - block-cipher string decryptor replacement,
  - `ObjectEvaluator` constant value folding,
  - legacy integer-argument pure string folding.
- `StringDecryptPass.makeReplacementInsn` already converts Java-side values to
  jadx IR for strings, primitive/boxed scalars, arrays, `Class<?>`, and null.
- `ObjectEvaluator` already evaluates:
  - string constructors and string operations,
  - `StringBuilder`,
  - integral arrays,
  - object arrays,
  - `Class<?>`,
  - JDK math/string/reflection handlers,
  - pure helper producers when the plugin has a safe snapshot.
- `TypeMap` already bridges `ArgType` and Java `Class<?>`.

The script feature should not duplicate this logic. It should extract and reuse
the replacement/value conversion boundary.

## High-Level Architecture

Add a public scripting API to the `jadx.plugins.stringdecrypt` package and keep
the implementation backed by package-private internals.

Recommended new public API classes:

- `ScriptPipeline`
- `PipelineMatcher`
- `PipelineFrame`
- `PipelineValue`
- `PipelineResult`
- `PipelineResult.Kind`
- `PipelineResult.UnsupportedReplacementException` or equivalent diagnostic
- optional `PipelineRegistration`

Recommended package-private implementation classes:

- `RegisteredPipeline`
- `PipelineRegistry`
- `PipelineReplacementFactory`
- `PipelineDiagnostics`

Recommended extraction from `StringDecryptPass`:

- Move `makeReplacementInsn`, `makeArrayInsn`, scalar conversion,
  `isCompatibleReplacementValue`, `copyReplacementMetadata`, and related helpers
  into `PipelineReplacementFactory`.
- Keep the factory package-private if possible, but make the public
  `PipelineResult` call into it through pass-owned code rather than exposing the
  factory directly.

The flow should be:

1. User script registers pipelines on `StringDecryptPlugin`.
2. `StringDecryptPlugin` stores registrations in a thread-safe registry.
3. `StringDecryptPass` receives a reference to the registry, not a one-time copy.
4. During each method visit, the pass builds the existing per-method evaluators.
5. For every invoke/constructor candidate, the pass builds a `PipelineFrame`.
6. Matching registered pipelines run before built-in replacement logic.
7. A `PipelineResult` is validated and converted to jadx IR.
8. If conversion succeeds, the pass replaces the instruction exactly like the
   built-in folders do.
9. Cleanup metadata from the result is merged with the pass's existing cleanup
   list.
10. If no script pipeline replaces the value, built-in decrypt/fold logic runs.

## Plugin Registration API

`StringDecryptPlugin` should own a registry:

```java
private final PipelineRegistry pipelines = new PipelineRegistry();
```

Minimum public methods:

```java
public PipelineRegistration pipeline(String name, String methodId, ScriptPipeline pipeline);

public PipelineRegistration pipeline(String name, PipelineMatcher matcher, ScriptPipeline pipeline);

public PipelineRegistration pipeline(ScriptPipelineSpec spec);

public void clearPipelines();

public List<PipelineRegistration> getPipelines();
```

`methodId` should be the raw full method id used by jadx:

```text
pkg.Class.method(ILjava/lang/String;)Ljava/lang/String;
```

Exact-ID registration should be the fastest and easiest path. Predicate
registration enables advanced matching:

```kotlin
stringDecrypt.pipeline("all-c-decoders", { frame ->
    frame.call().declaringClassRawName == "a.b.C" &&
        frame.call().name.startsWith("d") &&
        frame.call().returnType == ArgType.STRING
}) { frame ->
    ...
}
```

Order must be deterministic:

1. Pipelines execute in registration order.
2. The first `PipelineResult` that replaces wins.
3. `keep()` means "this pipeline declines; continue to the next pipeline".
4. `commentOnly()` adds a note and continues.
5. `fail()` logs a controlled error and continues unless configured otherwise.

The API should return `PipelineRegistration` so scripts can disable, inspect, or
remove their own registration when needed.

## ScriptPipeline Functional Interface

Keep the core callback Java-compatible:

```java
@FunctionalInterface
public interface ScriptPipeline {
    PipelineResult evaluate(PipelineFrame frame) throws Exception;
}
```

Why Java-compatible:

- It works from Kotlin scripts with a natural lambda.
- It avoids requiring Kotlin stdlib types in the plugin's public API.
- It keeps the standalone plugin jar simpler.
- It can be proxied by reflection if a future script/classloader bridge needs
  to register without compile-time class identity.

Do not return raw `Object` from the callback as the primary API. Raw `Object`
is ambiguous:

- `null` could mean keep, replace with typed null, or failed evaluation.
- `String` could mean replacement string or diagnostic.
- arrays require target type validation.
- raw `InsnNode` needs metadata/result handling.

`PipelineResult` is the necessary explicit boundary.

## PipelineMatcher Functional Interface

```java
@FunctionalInterface
public interface PipelineMatcher {
    boolean matches(PipelineFrame frame) throws Exception;
}
```

Common match helpers should be supplied so scripts rarely need to implement the
predicate manually:

```java
public static PipelineMatcher exact(String rawFullId);
public static PipelineMatcher owner(String rawClassName);
public static PipelineMatcher name(String methodName);
public static PipelineMatcher returns(ArgType type);
public static PipelineMatcher descriptor(String descriptor);
public static PipelineMatcher anyOf(PipelineMatcher... matchers);
public static PipelineMatcher allOf(PipelineMatcher... matchers);
```

The matcher receives a frame rather than only `MethodInfo` so advanced users can
match on surrounding context:

- current caller method,
- current caller class,
- whether the invoke is wrapped,
- result type,
- argument count,
- constant argument availability.

## PipelineFrame API

`PipelineFrame` is the script's view of one candidate instruction. It should be
public and immutable except for controlled cleanup/result annotations.

Core fields:

```java
public final class PipelineFrame {
    public MethodNode method();
    public ClassNode declaringClass();
    public InvokeNode invoke();          // if candidate is INVOKE
    public InsnNode instruction();       // generic candidate
    public MethodInfo call();
    public String rawFullId();
    public int userArgCount();
    public int rawArgCount();
    public boolean isStaticCall();
    public boolean hasReceiver();
    public PipelineValue receiver();
    public PipelineValue arg(int userIndex);
    public PipelineValue rawArg(int rawIndex);
    public List<PipelineValue> args();
    public List<PipelineValue> rawArgs();
    public ArgType returnType();
    public ArgType targetType();
    public int offset();
}
```

Argument indexing must be designed for ease of use:

- `arg(0)` means the first declared method argument, not the receiver.
- `receiver()` returns the instance object for instance calls.
- `rawArg(0)` means the raw jadx invoke argument at index 0.
- Internally `InvokeNode.getFirstArgOffset()` maps user args to raw args.

This removes the most common scripting footgun: forgetting that instance calls
carry a receiver at raw arg 0.

Evaluation services:

```java
public Object objectArg(int userIndex);
public String stringArg(int userIndex);
public byte[] bytesArg(int userIndex);
public char[] charsArg(int userIndex);
public long longArg(int userIndex);
public int intArg(int userIndex);
public Class<?> classArg(int userIndex);
```

The direct helpers are convenience wrappers over `arg(i).string()`, etc. They
should return nullable boxed types in Java for failure:

```java
public @Nullable Integer intOrNull(int userIndex);
public @Nullable Long longOrNull(int userIndex);
public @Nullable Character charOrNull(int userIndex);
```

Avoid throwing for normal "not constant" cases. A non-constant argument is
expected and should just make the script return `PipelineResult.keep()`.

Context and diagnostics:

```java
public void note(String message);
public void debug(String message);
public void cleanupArg(int userIndex);
public void cleanupReceiver();
public void cleanup(PipelineValue value);
```

However, prefer cleanup to live on `PipelineResult` so callback execution stays
easy to reason about:

```kotlin
return PipelineResult.replaceString(out).cleanupArg(0).cleanupArg(1)
```

## PipelineValue API

`PipelineValue` wraps one `InsnArg` and uses the frame's evaluators to convert
it to a script-friendly value.

Core:

```java
public final class PipelineValue {
    public int userIndex();              // -1 for receiver/non-user values
    public int rawIndex();
    public InsnArg rawArg();
    public ArgType type();
    public boolean isReceiver();
    public boolean isConstant();
    public boolean isNullRef();
    public @Nullable Object object();
}
```

Typed scalar helpers:

```java
public @Nullable Boolean booleanValue();
public @Nullable Byte byteValue();
public @Nullable Short shortValue();
public @Nullable Character charValue();
public @Nullable Integer intValue();
public @Nullable Long longValue();
public @Nullable Float floatValue();
public @Nullable Double doubleValue();
```

Typed object helpers:

```java
public @Nullable String string();
public @Nullable CharSequence charSequence();
public @Nullable Class<?> classValue();
public @Nullable ArgType classType();
```

Array helpers:

```java
public @Nullable boolean[] booleans();
public @Nullable byte[] bytes();
public @Nullable short[] shorts();
public @Nullable char[] chars();
public @Nullable int[] ints();
public @Nullable long[] longs();
public @Nullable float[] floats();
public @Nullable double[] doubles();
public @Nullable String[] strings();
public @Nullable Class<?>[] classes();
public @Nullable Object[] objects();
```

Raw escape hatches:

```java
public @Nullable InsnNode producer();
public @Nullable RegisterArg register();
```

The raw escape hatches are useful for rare script authors who understand jadx
IR, but examples and docs should focus on typed helpers.

Memoization:

- `PipelineValue` should memoize each expensive conversion per frame.
- `object()` should call `ObjectEvaluator.evalObject`.
- integer-like helpers should call `Evaluator.evalInt`.
- arrays should reuse existing array resolution paths.
- repeated script calls to `arg(0).bytes()` must not re-walk a large array.

## PipelineResult API

`PipelineResult` is the stable return contract between scripts and the plugin.

Basic result kinds:

```java
public enum Kind {
    KEEP,
    REPLACE,
    COMMENT_ONLY,
    FAIL
}
```

Basic factories:

```java
public static PipelineResult keep();
public static PipelineResult commentOnly(String message);
public static PipelineResult fail(String message);
public static PipelineResult fail(String message, Throwable cause);
public static PipelineResult replaceObject(Object value);
public static PipelineResult replaceInsn(InsnNode insn);
public static PipelineResult replaceArg(InsnArg arg);
```

String replacement:

```java
public static PipelineResult replaceString(String value);
public static PipelineResult replaceCharSequence(CharSequence value);
```

Scalar replacement:

```java
public static PipelineResult replaceBoolean(boolean value);
public static PipelineResult replaceByte(byte value);
public static PipelineResult replaceShort(short value);
public static PipelineResult replaceChar(char value);
public static PipelineResult replaceInt(int value);
public static PipelineResult replaceLong(long value);
public static PipelineResult replaceFloat(float value);
public static PipelineResult replaceDouble(double value);
```

Class replacement:

```java
public static PipelineResult replaceClass(Class<?> cls);
public static PipelineResult replaceClassType(ArgType type);
public static PipelineResult replaceClassName(String rawOrJavaName);
public static PipelineResult replaceClassDescriptor(String descriptor);
```

Array replacement:

```java
public static PipelineResult replaceBooleans(boolean[] value);
public static PipelineResult replaceBytes(byte[] value);
public static PipelineResult replaceShorts(short[] value);
public static PipelineResult replaceChars(char[] value);
public static PipelineResult replaceInts(int[] value);
public static PipelineResult replaceLongs(long[] value);
public static PipelineResult replaceFloats(float[] value);
public static PipelineResult replaceDoubles(double[] value);
public static PipelineResult replaceStrings(String[] value);
public static PipelineResult replaceClasses(Class<?>[] value);
public static PipelineResult replaceClassTypes(ArgType[] value);
public static PipelineResult replaceArray(Object array);
```

Null replacement:

```java
public static PipelineResult replaceNull(ArgType targetType);
public static PipelineResult replaceNullClass(String rawOrJavaName);
```

Fluent metadata:

```java
public PipelineResult comment(String message);
public PipelineResult cleanupArg(int userIndex);
public PipelineResult cleanupRawArg(int rawIndex);
public PipelineResult cleanupReceiver();
public PipelineResult cleanup(PipelineValue value);
public PipelineResult targetType(ArgType targetType);
public PipelineResult requireAssignable(boolean require);
```

Why many `replace...` methods are worth it:

- They make scripts readable.
- They avoid ambiguous `replaceObject`.
- They communicate intent to the replacement factory.
- They allow better diagnostics when the target type is incompatible.
- They address uncommon but real deobfuscation outputs such as `char[]`,
  `Class<?>`, and nested arrays.

`replaceObject(Object)` should still exist as a convenience, but it must route
through the same strict compatibility checks as built-in folding.

## Replacement Semantics

The replacement factory must always validate compatibility with the target
context before emitting IR.

Rules:

1. If the original instruction has a result register, the replacement must use
   a duplicate of that result unless the script-supplied raw `InsnNode` already
   has an intentional result.
2. Attributes, metadata, and offset should be copied from the original
   instruction.
3. If replacing a wrapped instruction argument, the pass should wrap the
   replacement with `InsnArg.wrapInsnIntoArg`.
4. If replacing a top-level instruction, the pass should use
   `BlockUtils.replaceInsn`.
5. If the replacement is incompatible with the original result type, refuse and
   continue with built-in folding.
6. A script must be able to explicitly override the target type only when it is
   still assignable or when returning raw IR.

Value to IR mapping:

| Script value | jadx IR |
| --- | --- |
| `String` | `ConstStringNode` |
| `CharSequence` | `ConstStringNode(value.toString())` |
| primitive/boxed scalar | `CONST` with typed literal |
| `Class<?>` | `ConstClassNode(TypeMap.fromClass(cls))` |
| `ArgType` as class literal | `ConstClassNode(type)` |
| primitive array | `FilledNewArrayNode(elementType, len)` |
| object array | `FilledNewArrayNode(elementType, len)` when each element can be represented |
| typed null | `CONST 0` with object/array type |
| raw `InsnNode` | supplied node with metadata handling |

For arrays:

- `char[]` must render as `new char[]{'a', 'b'}`-style IR, not as an opaque
  object.
- `byte[]`, `short[]`, `int[]`, `long[]`, `boolean[]`, `float[]`, `double[]`
  must all be supported.
- `String[]` elements should be wrapped `ConstStringNode`.
- `Class<?>[]` elements should be wrapped `ConstClassNode`.
- nested arrays should be supported when each nested value is itself
  representable.
- object arrays with unsupported elements should refuse unless the script
  returns raw IR.

For `Class<?>`:

- `String::class.java` should produce `String.class`.
- primitive classes such as `Int::class.javaPrimitiveType` should produce
  `int.class` equivalents through `ArgType.INT`.
- arrays such as `Array<String>::class.java` should produce the proper array
  class literal.
- `void.class` should be refused as a value replacement unless the target is a
  class literal context that can represent it.

## Pipeline Execution Order In The Pass

`StringDecryptPass.tryReplace` should become:

1. `tryScriptPipelines`
2. built-in decryptor replacement
3. built-in `ObjectEvaluator` constant replacement
4. built-in legacy `PureFold` string replacement

Rationale:

- Scripts are explicit user intent and should have first chance.
- If a script declines with `keep()`, built-ins still work.
- Exact script matches can override a built-in heuristic.
- This order enables user-defined pipelines for apps whose decryptors are not
  covered by the built-in AES appended-key model.

Wrapped instruction handling should stay recursive:

- Replace inner wrapped producers first.
- Then try the outer candidate.
- This lets scripts target either an inner decode helper or the final outer
  consumer call.

## Handling Method/Class Pipelines

A pipeline should conceptually target the root value producer that should be
replaced. That root is usually an `InvokeNode`, but future support should also
allow:

- `ConstructorInsn`, for patterns like `new String(...)`.
- `CHECK_CAST`, `CAST`, and `MOVE`, for reflective bridge wrappers.
- possibly `SGET`, when user-defined static constants need custom decoding.

For the first implementation, focus on `InvokeNode` and preserve the API shape
so `InsnNode` candidates can be added later.

For multi-call pipelines, the user has two supported approaches:

1. Register the outermost call and resolve inner producers through
   `frame.arg(i).object()`, `string()`, `bytes()`, etc.
2. Register multiple exact pipelines. Since wrapped replacements run inner-first,
   later outer replacements will see simpler constants.

Do not require the user to declare a rigid list of call stages up front. It is
less flexible than a script callback and duplicates what jadx IR already gives
through producer traversal.

Optional convenience DSL for later:

```kotlin
stringDecrypt.pipeline("rc4") {
    method("a.b.C.decode([BI)Ljava/lang/String;")
    returns(ArgType.STRING)
    evaluate {
        val data = arg(0).bytes() ?: keep()
        val key = arg(1).intValue() ?: keep()
        replaceString(rc4(data, key))
    }
}
```

This DSL should compile down to the same `PipelineMatcher` and `ScriptPipeline`
objects.

## Type Consistency And Classloader Strategy

This is the highest-risk part of the feature.

### The Problem

When `string-decrypt` is bundled in-tree, scripts and the plugin are likely to
see the same API classes. When `string-decrypt` is installed as a standalone
external plugin, jadx may load it with a plugin `URLClassLoader`. A `.jadx.kts`
script can also load jars using `@DependsOn`. If the script loads a second copy
of `jadx-string-decrypt`, these classes are not identical:

```text
plugin-loader: jadx.plugins.stringdecrypt.PipelineResult
script-loader: jadx.plugins.stringdecrypt.PipelineResult
```

They have the same name but are different JVM types. A callback returning the
script-loader `PipelineResult` cannot be cast to the plugin-loader
`PipelineResult`.

The same issue affects:

- `StringDecryptPlugin::class.java` lookups through `plugins().getInstance`.
- `ScriptPipeline` lambda conversion.
- `PipelineFrame` parameter typing.
- any future Kotlin extension DSL shipped inside the plugin jar.

### Required Design Rules

1. Do not tell users to add `@DependsOn("jadx-string-decrypt")` casually.
   That can create duplicate API classes.
2. In-tree bundled mode is the easiest supported path for the first version.
3. Standalone release mode must include a classloader diagnostic.
4. If script and plugin API classes are loaded by different classloaders, the
   plugin should log a clear error explaining the fix.
5. The public callback API should use minimal Java interfaces and POJOs so it
   remains possible to bridge by reflection/proxy later.

### Short-Term In-Tree Strategy

For the initial in-tree plugin:

- Put public API classes in `jadx.plugins.stringdecrypt`.
- Bundle `string-decrypt` with the same jadx distribution that contains
  `jadx-script`.
- Document imports that rely on the bundled classpath.
- Add a runtime diagnostic:

```java
public static void verifyApiClassLoader(Object scriptVisibleClassOrResult)
```

Better:

```java
public ClassLoader apiClassLoader();
public boolean sameApiClassLoader(Class<?> cls);
```

Scripts can log:

```kotlin
log.info { "string-decrypt API loader: ${StringDecryptPlugin::class.java.classLoader}" }
log.info { "plugin instance loader: ${stringDecrypt.javaClass.classLoader}" }
```

The plugin should also check returned `PipelineResult` instances and report:

```text
Pipeline 'x' returned jadx.plugins.stringdecrypt.PipelineResult from a different classloader.
Do not @DependsOn another copy of jadx-string-decrypt; use the bundled plugin API.
```

### Better Standalone Strategy

The ideal long-term standalone behavior requires cooperation from `jadx-script`:

1. During script compilation, append resolved plugin jars/classes to the script
   compilation classpath.
2. During script evaluation, prefer already-loaded plugin classes for plugin API
   packages rather than loading a second jar copy.
3. Expose a helper on `JadxScriptInstance`:

```kotlin
inline fun <reified T : JadxPlugin> plugin(): T
```

or:

```kotlin
fun pluginById(id: String): JadxPluginRuntimeData
```

`pluginById` already exists indirectly through `pluginContext.plugins()`, but
`JadxScriptInstance` should expose `pluginContext` or a small wrapper so scripts
do not need `internalDecompiler`.

Until `jadx-script` provides this, use:

```kotlin
val sdRuntime = jadx.pluginContext.plugins().getById("string-decrypt")
val sd = sdRuntime.pluginInstance
```

and cast only when classloaders are known to match.

### Reflection/Proxy Fallback

If standalone plugin use must work before `jadx-script` classpath behavior is
improved, provide a documented advanced fallback using only JDK classes:

- Fetch plugin instance by id.
- Load `ScriptPipeline`, `PipelineMatcher`, `PipelineResult` from the plugin
  instance classloader.
- Create a `java.lang.reflect.Proxy` for `ScriptPipeline`.
- Reflectively call `pipeline(name, methodId, proxy)`.

This fallback is powerful but not user-friendly, so it should not be the main
API. It exists as an escape hatch for classloader mismatch.

## Script Plugin Integration Improvements

The plan is best if `jadx-script` gains two small improvements:

1. `JadxScriptInstance` exposes:

```kotlin
val pluginContext: JadxPluginContext
fun pluginRuntime(id: String): JadxPluginRuntimeData
```

This avoids relying on `internalDecompiler` for plugin lookup.

2. `JadxScriptConfiguration` optionally includes resolved plugin classpaths.

Current script config uses:

```kotlin
dependenciesFromCurrentContext(wholeClasspath = true)
```

That covers the script plugin context. It does not necessarily make every
external plugin API type visible without duplicate loading. The improvement
should collect external plugin loader URLs from resolved plugin contexts when
available and append them as compile dependencies.

If classloader unification cannot be guaranteed, keep the first release scoped
to the in-tree bundled mode and document standalone script pipelines as
experimental.

## Threading And Lifecycle

jadx decompilation can visit methods concurrently. The pipeline registry and
callbacks must be safe under that model.

Registry:

- Use `CopyOnWriteArrayList` or immutable snapshots.
- Registration is expected during script evaluation before decompilation starts.
- If a registration happens during decompilation, behavior should be defined:
  either reject it or apply only to future methods. Rejecting after pass start is
  more deterministic.

Pass state:

- Per-method objects stay per-method:
  - `Evaluator`
  - `ObjectEvaluator`
  - `PipelineFrame`
  - `PipelineValue`
  - cleanup lists
- Do not share evaluator instances between methods.

Script callback state:

- Scripts may close over mutable state. That is user's code, but docs should
  warn that callbacks can run concurrently.
- Examples should use pure functions.
- If caching is needed, examples should use `ConcurrentHashMap`.

Error handling:

- An exception in one pipeline callback must not abort decompilation by default.
- Log the pipeline name, target method id, caller method, and exception.
- Count failures per pipeline.
- Optional future setting:

```text
string-decrypt.pipeline-errors = warn|disable-pipeline|fail-decompile
```

Default should be `warn` or `disable-pipeline` after repeated failures.

## Cleanup Semantics

Existing cleanup already removes consumed byte-array builds and orphan arrays.
Script pipelines need controlled access to this:

```kotlin
PipelineResult.replaceString(out)
    .cleanupArg(0)
    .cleanupArg(1)
```

Cleanup should mean:

- If the selected argument is a `RegisterArg` whose assignment is a
  `NEW_ARRAY`/`FILLED_NEW_ARRAY`, add it to the existing `builtArrays` cleanup
  list.
- The existing cleanup function must still refuse if the array is read by
  anything other than build-time stores.
- Never remove producer instructions just because a script asked if they still
  have runtime uses.

Additional cleanup helpers:

```java
cleanupArg(int userIndex)
cleanupRawArg(int rawIndex)
cleanupReceiver()
cleanup(PipelineValue value)
cleanupAllConstantArgs()
```

`cleanupAllConstantArgs()` sounds convenient but is risky. If added, it should
only mark array builds and still rely on normal use-list validation.

## Options

Add options only where they materially change behavior:

| Option | Default | Meaning |
| --- | --- | --- |
| `string-decrypt.script-pipelines` | `true` | run registered script pipelines |
| `string-decrypt.pipeline-comments` | inherits `comments` | include script pipeline notes in method comments |
| `string-decrypt.pipeline-errors` | `warn` | error policy for script callbacks |
| `string-decrypt.pipeline-max-failures` | `5` | disable a noisy pipeline after repeated exceptions |

Avoid adding options for every replacement type. Replacement compatibility is
already type-checked.

## Diagnostics And Comments

When a pipeline replaces a value:

- Debug log should include:
  - pipeline name,
  - raw target id,
  - caller method,
  - replacement kind/type,
  - whether cleanup was requested.
- Method comments should include only concise user-facing summaries, respecting
  the current summary cap used for decrypted strings.

Example comment entry:

```text
Pipeline sample-xor-char-string: "abc" -> "xyz"
```

If a pipeline fails to convert a returned value:

```text
Pipeline sample returned char[] but target type is java.lang.String; keeping original invoke
```

If classloader mismatch occurs:

```text
Pipeline sample returned PipelineResult loaded by <script loader>, but string-decrypt expects <plugin loader>.
Do not load a second copy of jadx-string-decrypt in @DependsOn.
```

## Security Model

`.jadx.kts` scripts are trusted code. They can run arbitrary JVM code inside the
jadx process. The pipeline API should not pretend to sandbox them.

Security boundaries are still useful inside the plugin:

- Do not evaluate app methods with arbitrary runtime execution.
- Keep static evaluator purity checks.
- Do not fold reads from runtime-mutated statics.
- Do not execute arbitrary reflected app code.
- Do not let replacement values bypass target type checks unless the script uses
  raw IR intentionally.

## Performance Model

The common case must be cheap:

1. Build a map from exact raw method id to registrations.
2. Check exact-id pipelines before predicate pipelines.
3. Only create full `PipelineFrame` when at least one candidate registration
   could match, or create a lightweight frame that lazily initializes values.
4. `PipelineValue` memoizes conversions.
5. Existing evaluator depth and array-size limits still apply.
6. Predicate pipelines should be documented as slower.

Potential registry structure:

```java
final class PipelineRegistry {
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<RegisteredPipeline>> exact = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<RegisteredPipeline> predicates = new CopyOnWriteArrayList<>();
}
```

Lookup:

```java
List<RegisteredPipeline> candidatesFor(PipelineFrame frame) {
    // exact list by frame.rawFullId(), then predicates
}
```

## Future Backend Extension Point

Do not add smalivm now, but avoid painting the design into a corner.

Possible future interface:

```java
public interface PipelineEvaluationBackend {
    @Nullable Object evaluate(PipelineFrame frame, PipelineEvaluationRequest request);
}
```

Initial backend:

```text
static-ir
```

Potential later backends:

```text
dex-smalivm
host-jvm-reflection-for-whitelisted-classes
external-oracle
```

The `PipelineFrame` and `PipelineResult` API should not mention smalivm. Scripts
should continue to call `arg(i).object()` or an explicit future
`frame.backend("dex").eval(...)`.

## Implementation Plan

### Phase 1: Extract Replacement Factory

Move the replacement conversion helpers out of `StringDecryptPass`:

- `makeReplacementInsn`
- `makeArrayInsn`
- `arrayElementType`
- `valueToArrayArg`
- `scalarLiteralType`
- `scalarLiteralBits`
- `isCompatibleReplacementValue`
- `replacementValueClass`
- `boxedScalarClass`
- `copyReplacementMetadata`

New package-private class:

```text
src/main/java/jadx/plugins/stringdecrypt/PipelineReplacementFactory.java
```

Expected result:

- Built-in folding still behaves identically.
- New factory can convert both built-in evaluator values and script results.
- Existing tests remain green.

### Phase 2: Add Public API Types

Add:

```text
ScriptPipeline.java
PipelineMatcher.java
PipelineFrame.java
PipelineValue.java
PipelineResult.java
PipelineRegistration.java
```

Keep them in package `jadx.plugins.stringdecrypt` so they can access
package-private evaluators indirectly without exposing those evaluators.

`PipelineFrame` constructor can stay package-private. Scripts receive instances
but cannot forge them.

### Phase 3: Add Registry To Plugin

Modify `StringDecryptPlugin`:

- add `PipelineRegistry pipelines = new PipelineRegistry();`
- expose `pipeline(...)` registration methods,
- pass `pipelines` into `StringDecryptPass`.

`StringDecryptPass` should hold the registry reference:

```java
private final PipelineRegistry pipelines;
```

This is important because script plugin initialization order may register
callbacks before or after `StringDecryptPlugin.init`; a live registry reference
keeps both cases working as long as registration happens before method visits.

### Phase 4: Wire `tryScriptPipelines`

Add to `StringDecryptPass`:

```java
private @Nullable InsnNode tryScriptPipelines(
    MethodNode mth,
    Evaluator ev,
    ObjectEvaluator oev,
    InsnNode insn,
    List<String> notes,
    List<RegisterArg> builtArrays,
    boolean consumerIsLookup
)
```

Rules:

- Only invoke for supported candidate instruction types.
- Build `PipelineFrame`.
- Get candidate registrations.
- For each candidate:
  - run matcher,
  - run callback,
  - handle `KEEP`, `COMMENT_ONLY`, `FAIL`, `REPLACE`.
- Convert replacement with `PipelineReplacementFactory`.
- Add cleanup requests to `builtArrays`.
- Add comments to `notes`.
- Return first replacement.

Place it first in `tryReplace`.

### Phase 5: Add Kotlin Script Convenience Docs

The plugin itself can be Java-only. Docs should show Kotlin script helpers.

Optional helper source if Kotlin is allowed in the plugin later:

```kotlin
fun JadxScriptInstance.stringDecrypt(): StringDecryptPlugin =
    pluginContext.plugins().getById(StringDecryptPlugin.PLUGIN_ID)
        .pluginInstance as StringDecryptPlugin
```

If this helper cannot live in `string-decrypt` because it would depend on
`jadx-script-runtime`, keep it as a documented snippet. Do not add a hard
dependency from `string-decrypt` to `jadx-script`.

### Phase 6: Classloader Diagnostics

Add diagnostics:

- plugin instance loader,
- API class loader,
- returned result class loader,
- script callback class loader.

Do not fail silently on mismatch. The error message should tell the user exactly
not to load a second plugin jar through `@DependsOn`.

### Phase 7: Tests

Add Java-level integration tests first, because they do not depend on script
plugin classpath details:

1. exact method registration replaces a `String` result,
2. exact method registration declines with `keep()` and built-in folding still
   runs,
3. user args exclude receiver for instance calls,
4. `receiver()` works for instance calls,
5. `replaceChars(char[])` emits valid char-array IR,
6. `replaceClass(Class<?>)` emits valid class literal IR,
7. `replaceClassType(ArgType)` emits valid class literal IR,
8. `replaceBytes(byte[])` emits valid byte-array IR,
9. `replaceBoolean`, `replaceChar`, and `replaceLong` preserve target type,
10. incompatible result refuses and leaves original code unchanged,
11. cleanupArg removes only dead array builders,
12. wrapped invoke replacement works,
13. callback exception logs and does not abort decompilation by default,
14. registration order is deterministic,
15. repeated value reads hit memoized evaluation.

Then add script-level tests if the repo can run `jadx-script` integration:

1. `.jadx.kts` imports plugin API and registers a pipeline,
2. context-receiver or extension-function style compiles where supported,
3. script without `@DependsOn` uses bundled plugin API,
4. intentional classloader mismatch produces a clear diagnostic.

### Phase 8: Documentation

Update:

- `README.md` with a "Script pipelines" section.
- `AGENT.md` with implementation invariants.
- add examples under an examples directory, for example:
  - `examples/xor-char-string.jadx.kts`
  - `examples/char-array-result.jadx.kts`
  - `examples/class-result.jadx.kts`

Docs must include:

- exact raw method id format,
- receiver vs user arg indexing,
- typed replacement methods,
- cleanup behavior,
- classloader warning,
- concurrency warning,
- "no smalivm in this version" scope.

## Acceptance Criteria

The feature is ready when:

1. A `.jadx.kts` script can register a pipeline in under 20 lines for a simple
   XOR/string decoder.
2. The script does not import or instantiate raw jadx IR classes for normal
   replacements.
3. The same API can replace `String`, `char[]`, primitive arrays, scalar values,
   `Class<?>`, and typed null.
4. Built-in decrypt/fold behavior remains unchanged when no pipelines are
   registered.
5. Script pipelines are deterministic under multi-threaded decompilation.
6. Classloader mismatch is detected and explained.
7. Existing plugin tests pass.
8. New tests cover broad replacement output types and cleanup.

## Recommended First Public Surface

Do this first:

```java
public PipelineRegistration pipeline(String name, String methodId, ScriptPipeline pipeline);

public final class PipelineFrame {
    public PipelineValue arg(int index);
    public PipelineValue receiver();
    public String rawFullId();
    public MethodInfo call();
    public MethodNode method();
    public ArgType targetType();
}

public final class PipelineValue {
    public @Nullable Object object();
    public @Nullable String string();
    public @Nullable byte[] bytes();
    public @Nullable char[] chars();
    public @Nullable Integer intValue();
    public @Nullable Long longValue();
    public @Nullable Character charValue();
    public @Nullable Class<?> classValue();
}

public final class PipelineResult {
    public static PipelineResult keep();
    public static PipelineResult replaceString(String value);
    public static PipelineResult replaceObject(Object value);
    public static PipelineResult replaceChars(char[] value);
    public static PipelineResult replaceBytes(byte[] value);
    public static PipelineResult replaceClass(Class<?> value);
    public static PipelineResult replaceClassType(ArgType value);
    public static PipelineResult replaceInsn(InsnNode value);
    public PipelineResult cleanupArg(int index);
    public PipelineResult comment(String message);
}
```

Then expand to the full replacement method set once the core loop is stable.

## Main Risks

1. Classloader mismatch between script and standalone plugin API.
2. Accidentally exposing too much raw jadx IR and making scripts fragile.
3. Over-cleaning consumed args that still have real runtime uses.
4. Re-evaluating large arrays repeatedly in callback code.
5. Callback exceptions making decompilation unreliable.
6. Predicate pipelines becoming expensive on large apps.
7. Returning values that are valid JVM objects but impossible to represent in
   Java source/IR without complex object construction.

The design above addresses these by:

- using a typed `PipelineResult`,
- centralizing IR conversion,
- preserving existing cleanup validation,
- memoizing `PipelineValue`,
- exact-id fast path,
- clear classloader diagnostics,
- raw IR as an explicit escape hatch rather than the normal path.

## Bottom Line

The best implementation is not a second deobfuscation engine. It is a scriptable
front door into the plugin's existing static evaluator and replacement factory.

The script author supplies the app-specific knowledge:

- which method id represents a pipeline,
- how to combine resolved arguments,
- what value should replace the call.

The plugin remains responsible for the hard jadx-specific work:

- resolving compile-time values safely,
- preserving types,
- building valid IR,
- copying metadata,
- cleaning consumed array builders,
- running predictably inside jadx's pass lifecycle.
