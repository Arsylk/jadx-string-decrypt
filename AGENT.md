# AGENT.md — jadx Constant Deobfuscator plugin

> Read this before touching the plugin or running it on a sample.
> Plugin id: `string-decrypt` · Java package: `jadx.plugins.stringdecrypt` · Repo: `Arsylk/jadx-string-decrypt`

## What it is

A jadx plugin that **statically deobfuscates compile-time constants** in obfuscated Android apps
and (optionally) decrypts their strings. It targets the obfuscation family that hides every
literal behind an opaque, table-based expression such as:

```java
((int) KEY[5]) ^ 1722740229                     // -> a single int, computable at compile time
new String(c, off, len)                         // helper that rebuilds a char from a static table
xxx.decrypt(new byte[]{ ...opaque bytes... })   // appended-key block cipher (e.g. AES/ECB)
```

It is **generic**: every behaviour is driven by settings and auto-detection. A wrong guess can
never corrupt output — folds happen only for values proven compile-time-constant, and a bad string
decryption is discarded by a printable-result check.

## What it does (two passes)

1. **Prepare pass (`KeyTablesPass`)** — before decompilation, single-threaded:
   - Reconstructs every static integral array ("key table") and scalar constant from each class's
     `<clinit>` (handles aput chains, fill-array-data, dup/MOVE-aliased initializers).
   - Snapshots small **pure** static helper bodies (for interprocedural folding) into an immutable
     form, since the live method bodies are unavailable/mutating during the threaded decompile.
   - Scans the whole program for **runtime-mutated** statics (SPUT/APUT outside the declaring
     `<clinit>`); reads of those are never folded (soundness gate).
   - Auto-detects string decryptors (`static String x(byte[])` that calls `Cipher.doFinal`).
   - Note: a self-contained JCE-cipher decryptor (`static String dec(String)` built only from
     `Base64.decode` + `Cipher`/`SecretKeySpec`/`IvParameterSpec` + `new String(...)`, e.g. a
     DESede or AES helper) is **not** handled by this AES-only detector — it is folded by the
     general pure-helper interpreter instead (see below), so no per-app key/algorithm config.
2. **Decompile pass (`StringDecryptPass`)** — per method:
   - Folds every compile-time-constant numeric/boolean expression to its literal (the headline
     generic feature), including constants reachable only through pure helper calls.
   - Replaces representable constant helper results with typed jadx IR for strings, scalars,
     primitive/object arrays (including `char[]`), and resolved `Class<?>` constants.
   - Interprets pure helper bodies end-to-end through a JDK whitelist (`jdk/JdkInterpreter` +
     `JdkClassHandler`s). Besides the stdlib (`String`/`StringBuilder`/`BigInteger`/`Math`/`Arrays`/
     `Charset`/…) this whitelist covers **`javax.crypto.Cipher` + `SecretKeySpec`/`IvParameterSpec`/
     `GCMParameterSpec`** and **Base64** (`android.util.Base64`, re-implemented since it is not on the
     host JDK; and `java.util.Base64` + its `Decoder`/`Encoder`, reflected). So a `static String
     dec(String)` whose body is `Base64.decode → new SecretKeySpec → Cipher.getInstance/init/doFinal →
     new String(...)` runs on real host JCE objects driven by its own constants, and every
     constant-argument call site inlines to plaintext — config-free, any algorithm/key the host
     supports. Soundness: only `DECRYPT_MODE`/`UNWRAP_MODE` `Cipher.init` is honoured (encrypt/wrap may
     draw a random IV → non-deterministic); the transformation must be a symmetric cipher; any failure
     (unsupported transform, bad padding, wrong key) throws and the call is simply left un-folded.
   - Decrypts resolvable string-decryptor calls whose `byte[]` argument is constant.
   - Rewrites resolvable reflective calls (`handle.invoke(...)`) to the direct call (de-indirection)
     and sweeps the dead reflective scaffolding it leaves behind.
   - Removes the now-dead feeder instructions (table reads, byte-array build, folded arithmetic).

   Replacement strategies live behind a `Resolver` pipeline (the first non-null wins): built-in
   decrypt → fold-const → fold-pure-string → de-indirection → **user `.jadx.kts` script pipelines
   (last)**. The single value→IR boundary is `ReplacementFactory`. Add capabilities as new `Resolver`s,
   not branches in `tryReplace`.

## Settings (all prefixed `string-decrypt.`)

| Option | Default | Meaning |
| --- | --- | --- |
| `enabled` | `true` | master switch |
| `fold-consts` | `true` | fold constant numeric/boolean expressions to literals |
| `fold-helper-calls` | `true` | also fold pure interpretable helper-method calls (interprocedural) |
| `decrypt-strings` | `true` | decrypt resolvable block-cipher string-decryptor calls |
| `deindirect-reflection` | `true` | rewrite resolvable reflective calls to direct calls |
| `cleanup-reflection` | `true` | remove dead reflective scaffolding (`X.class.getMethod(...).setAccessible` no-ops) |
| `script-pipelines` | `true` | run user-registered `.jadx.kts` replacement pipelines |
| `cleanup` | `true` | remove dead feeder instructions |
| `comments` | `true` | add a method comment listing decrypted strings |
| `decryptor-class` | `""` | restrict the decryptor to this raw class name; empty = auto-detect only |
| `decryptor-desc` | `([B)Ljava/lang/String;` | descriptor a decryptor candidate must match |
| `cipher` | `AES/ECB/PKCS5Padding` | JCE transformation; key = last `key-tail-len` bytes of each blob |
| `key-tail-len` | `16` | trailing key bytes appended to each ciphertext |
| `max-table-size` | `4000000` | cap on reconstructed static-array length (memory guard) |

Pass any with `-P string-decrypt.<opt>=<value>` on the CLI, or via the GUI plugin settings.

## Versioning policy

**Bump the version on every change that affects output or options.** Three places must stay in sync:

- `build.gradle.kts` → `version = "X.Y.Z"`
- `StringDecryptPlugin.VERSION` → `"X.Y.Z"`
- a matching `vX.Y.Z` git tag (triggers the release workflow → publishes
  `jadx-string-decrypt-X.Y.Z.jar` as a GitHub release asset, which jadx's `--install` resolver picks
  up automatically).

Use semver: PATCH for fixes that don't change defaults/options, MINOR for new options or new
folding capability, MAJOR for changed defaults or removed/renamed options. Also bump
`REQUIRED_JADX_VERSION` when the plugin depends on newer jadx APIs.

## How to install before running

```bash
# A. from the GitHub marketplace (when listed):
jadx plugins --install string-decrypt

# B. by location id (always works):
jadx plugins --install github:Arsylk:jadx-string-decrypt

# C. from a local build:
./gradlew jar
jadx plugins --install-jar build/libs/jadx-string-decrypt-1.3.0.jar

# verify
jadx plugins --list      # expect "string-decrypt  Constant Deobfuscator vX.Y.Z"
jadx -d out app.apk      # deobfuscates with default settings
```

For a folding-only run (skip the string-decryption path):
`jadx -P string-decrypt.decrypt-strings=false -d out app.apk`

## Invariants to preserve when editing

The pass interprets adversarial obfuscated code, so it must **never abort a class's decompile**: each
per-instruction fold in `StringDecryptPass.visit` is wrapped so an unforeseen evaluator failure is
logged and skipped, not propagated (a thrown pass = the class drops to an error stub). `ObjectEvaluator`
also defends its boundaries — refuse (return null), never throw, on type mismatches (array element /
`ArrayStoreException`), non-integral-primitive arrays (`float[]`/`double[]`), and un-parseable
`Class.forName` names. Individual decrypted values log at `DEBUG`; the per-method count stays at `INFO`.

On a known obfuscated sample, a correct build keeps:
- decompile **error count unchanged** vs. plugin-disabled baseline (folding must not break methods);
- **decrypted-string count stable** (the soundness gate must not over-exclude tables);
- **no hang** (the per-method evaluator is memoized — never reintroduce exponential re-evaluation);
- folded values **independently recomputable** from the raw table data.

Known deliberate non-fold: a constant inside a jadx `(intExpr) == true ? :` ternary-condition
artifact is left unfolded — folding it requires mutating jadx's synthetic condition node, which
introduces decompile errors. Do **not** "fix" this by mutating ternary conditions.

Script pipelines (`PIPELINE_SCRIPTING_PLAN.md`): keep them **last** in the resolver list (the user
directive — built-in deobfuscation is automatic and primary; pipelines operate on the already-resolved
values). `StringDecryptPlugin` holds a **live** `PipelineRegistry` reference handed to the pass, so a
script may register before or after `init`. With no pipelines registered, output must be
**byte-identical** to before — the resolver returns null immediately. Test gotcha:
`JadxDecompiler.registerPlugin` is discarded by `load()`; register on jadx's own loaded instance
(`getPluginManager().getResolvedPluginContexts()`) after `load()`, before the lazy `getCode()`.

Two registration fronts, by how the plugin is loaded:

- **Bundled in-tree** — the typed API (`pipeline(name, id|matcher, ScriptPipeline)` →
  `PipelineFrame`/`PipelineValue`/`PipelineResult`). Requires the plugin's classes on the script compile
  classpath, which only holds when bundled.
- **Installed jar (standalone)** — jadx loads the plugin in a private `URLClassLoader`
  (`JadxExternalPluginsLoader`), absent from the script's `dependenciesFromCurrentContext` classpath, so a
  script can't reference plugin types and `@DependsOn` would dual-load incompatible copies. The
  classloader-safe bridge (`ScriptBridge` + `StringDecryptPlugin.registerPipeline(name, id|Predicate,
  Function<Map,Object>)`) crosses the split with **shared types only** (JDK `Function`/`Predicate`/`Map`
  + jadx-core `ArgType`/`InsnNode`); the plugin builds the frame `Map` and interprets the returned value
  into a `PipelineResult` plugin-side. A script invokes it reflectively (`plugin.javaClass.getMethod
  ("registerPipeline", …)`), so nothing it touches is loaded by the plugin's classloader. **Do not modify
  `jadx-script`** — the bridge is what makes standalone work without it. Both fronts feed the same
  `PipelineRegistry`/`ScriptPipelineResolver`/`ReplacementFactory` engine; keep it one engine.

jadx-gui integration (`StringDecryptGuiActions`, registered in `init` only when `context.getGuiContext()
!= null` — null on the CLI): a code-area popup action **“Copy as string-decrypt pipeline”** that copies a
paste-ready `.jadx.kts` skeleton for the clicked node (`ICodeNodeRef` → `MethodNode`/`ClassNode`/
`FieldNode` → raw id via `getMethodInfo()/getFieldInfo().getRawFullId()` / `getRawName()`). It emits the
**standalone-bridge** form (reflective `registerPipeline`) so the copied snippet runs against an installed
jar — the GUI user's actual deployment. `JadxGuiContext`/`ICodeNodeRef` are jadx-core types, so no
jadx-gui dependency is added (still `compileOnly` jadx-core).
