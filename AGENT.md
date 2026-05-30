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
2. **Decompile pass (`StringDecryptPass`)** — per method:
   - Folds every compile-time-constant numeric/boolean expression to its literal (the headline
     generic feature), including constants reachable only through pure helper calls.
   - Decrypts resolvable string-decryptor calls whose `byte[]` argument is constant.
   - Removes the now-dead feeder instructions (table reads, byte-array build, folded arithmetic).

## Settings (all prefixed `string-decrypt.`)

| Option | Default | Meaning |
| --- | --- | --- |
| `enabled` | `true` | master switch |
| `fold-consts` | `true` | fold constant numeric/boolean expressions to literals |
| `fold-helper-calls` | `true` | also fold pure interpretable helper-method calls (interprocedural) |
| `decrypt-strings` | `true` | decrypt resolvable block-cipher string-decryptor calls |
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
jadx plugins --install-jar build/libs/jadx-string-decrypt-1.0.1.jar

# verify
jadx plugins --list      # expect "string-decrypt  Constant Deobfuscator vX.Y.Z"
jadx -d out app.apk      # deobfuscates with default settings
```

For a folding-only run (skip the string-decryption path):
`jadx -P string-decrypt.decrypt-strings=false -d out app.apk`

## Invariants to preserve when editing

On a known obfuscated sample, a correct build keeps:
- decompile **error count unchanged** vs. plugin-disabled baseline (folding must not break methods);
- **decrypted-string count stable** (the soundness gate must not over-exclude tables);
- **no hang** (the per-method evaluator is memoized — never reintroduce exponential re-evaluation);
- folded values **independently recomputable** from the raw table data.

Known deliberate non-fold: a constant inside a jadx `(intExpr) == true ? :` ternary-condition
artifact is left unfolded — folding it requires mutating jadx's synthetic condition node, which
introduces decompile errors. Do **not** "fix" this by mutating ternary conditions.
