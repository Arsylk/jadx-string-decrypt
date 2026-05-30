# jadx Constant Deobfuscator (`string-decrypt`)

A [jadx](https://github.com/skylot/jadx) plugin that **statically deobfuscates compile-time
constants** in obfuscated Android apps — and, where applicable, decrypts their strings.

Many commercial Android obfuscators replace every literal with an opaque, table-based expression
that is nonetheless fully resolvable at compile time. This plugin reconstructs the static "key
tables", folds the opaque expressions back to literals **everywhere** they appear (arguments,
array sizes/indices, returns, field initializers, conditions, byte-array builds), and decrypts
resolvable block-cipher string-decryptor calls.

It is **generic**: every behaviour is driven by settings and auto-detection. Folding is sound by
construction (only genuinely compile-time values are folded; reads of any static the plugin sees
mutated at runtime are excluded), and a wrong string decryption is discarded by a printable-result
check — so enabling it on an unrelated APK is safe (it just no-ops).

---

## Install

```bash
# Recommended (when listed on the marketplace):
jadx plugins --install string-decrypt

# By location id (always works):
jadx plugins --install github:Arsylk:jadx-string-decrypt

# Or from a local build:
./gradlew jar
jadx plugins --install-jar build/libs/jadx-string-decrypt-1.0.1.jar
```

Verify:

```bash
jadx plugins --list      # "string-decrypt  Constant Deobfuscator v1.0.1"
jadx -d out app.apk      # default settings deobfuscate out of the box
```

---

## Before / after — the same `onReceive` method, same APK

**With this plugin (12 lines, all opaque constants folded, string decrypted in place):**

```java
public void onReceive(Context context, Intent intent) {
    if (intent == null) return;
    ayp.m4254a().m4259a("onReceive Intent=" + intent, str);
    if (C0120a.getStr().equals(intent.getAction())) {
        bcq.m4272a(context).edit().putString(C0120a.getStr3(), "").apply();
        if (azb.m4322b(context)) return;
        bcp.m4271a(context, C0120a.getStr2(), null, true);
    }
}
```

**Without it — JEB on the same dex (excerpt; the full method runs ~200 lines):**

```java
public void onReceive(Context context0, Intent intent0) {
    if (intent0 == null) return;
    ayp ayp0 = ayp.a();
    StringBuilder stringBuilder0 = new StringBuilder();
    long[] arr_v = BootReceiver.a;
    byte[] arr_b = new byte[((int) arr_v[0]) ^ 0x7FC338CE];
    arr_b[((int) arr_v[1]) ^ 0x5854B62B] = ((int) arr_v[2]) ^ 0x3E6FC83;
    arr_b[((int) arr_v[3]) ^ 0x7C2EB27F] = ((int) arr_v[4]) ^ 0x443FC81;
    arr_b[((int) arr_v[5]) ^ 0x3CF60D34] = ((int) arr_v[6]) ^ 0x2C3D50A1;
    arr_b[((int) arr_v[7]) ^ 0x5E08ECD7] = ((int) arr_v[8]) ^ 1026261086;
    // ... ~180 more lines like this build the opaque ciphertext array, then:
    String s = bdh.a(arr_b);
    // ... the actual control flow is buried below
}
```

## Aggregate comparison vs JEB (73 paired files of `com.mistral.jon`)

| Metric | jadx + this plugin | JEB | Winner |
| --- | ---: | ---: | --- |
| Residual XOR (`^`) operators | **5** | **10,536** | this plugin (~2,000×) |
| Total output lines | **4,571** | 13,630 | this plugin (3× more compact) |
| Decrypted string literals in source | **+45** (in place) | 0 | this plugin |
| Methods that fail to decompile (`throw UnsupportedOpException`) | 5 | 5 | tied — same 5 methods both can't crack |
| Static `long[]` key tables | one-line Java literal | ~170 lines of `arr_v[i] = …L;` | this plugin (idiomatic) |

The 5 failures are on the same pathologically obfuscated service methods in both tools — a
property of the obfuscator, not of either decompiler.

JEB is a paid commercial tool; the comparison is "stock JEB" vs "free jadx + this plugin". The
plugin is doing all the heavy lifting on the jadx side — vanilla jadx without it would land much
closer to JEB on the same APK.

---

## Settings

All options are prefixed `string-decrypt.` (CLI: `-P string-decrypt.<opt>=<value>`; or the GUI
plugin settings). Defaults target general deobfuscation, so a fresh install needs no flags.

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
| `cipher` | `AES/ECB/PKCS5Padding` | JCE transformation; key = last `key-tail-len` bytes of the blob |
| `key-tail-len` | `16` | trailing key bytes appended to each ciphertext |
| `max-table-size` | `4000000` | cap on reconstructed static-array length |

Examples:

```bash
# fold opaque constants only, skip the string decryptor
jadx -P string-decrypt.decrypt-strings=false -d out app.apk

# target a different appended-key cipher
jadx -P string-decrypt.cipher=DESede/ECB/PKCS5Padding -P string-decrypt.key-tail-len=24 -d out app.apk
```

---

## How it works (overview)

Two passes, sound by construction:

1. **Prepare pass** — before decompilation:
   - Reconstructs every static integral key table / scalar constant from each class's `<clinit>`
     (handles aput chains, `fill-array-data`, and javac's `dup`/MOVE-aliased initializers).
   - Snapshots small *pure* static helper bodies into an immutable form (so the interprocedural
     folder can read them safely later, even while jadx's threaded decompile pass is mutating
     them).
   - Scans the whole program for runtime-mutated statics (SPUT/APUT outside the declaring
     `<clinit>`); reads of those are never folded — the soundness gate.
   - Auto-detects string decryptors (`static String x(byte[])` calling `Cipher.doFinal`).
2. **Decompile pass** — per method:
   - Folds every compile-time-constant numeric/boolean expression to its literal value,
     everywhere — including expressions reachable only through pure helper calls.
   - Replaces resolvable string-decryptor calls with the decrypted constant string.
   - Removes the dead feeder instructions left behind (table reads, byte-array builds,
     arithmetic).

### Safety

Folding is **sound**: only genuinely compile-time values are folded — literals, arithmetic,
conversions, reads of *immutable* static tables (the whole-program scan excludes any static
written at runtime), and pure helper calls. Object `null` (which the IR represents as `0`) and
`float` / `double` bit patterns are explicitly refused so they can never be inlined as integer
literals. A wrong string decryption produces non-printable bytes and is discarded, so enabling
decryption is safe even on apps that use a different scheme (it just no-ops).

---

## Build / develop

Requires JDK 11+.

```bash
./gradlew jar                          # -> build/libs/jadx-string-decrypt-<version>.jar
./gradlew jar -PinstallPrefix=...      # no effect — gradle props you pass are not used here
```

The plugin uses jadx-core internals (`jadx.core.dex.*`); the published `io.github.skylot:jadx-core`
artifact is referenced as `compileOnly`, so the jar contains only the plugin's own classes
(jadx-core is provided by the host jadx at install time).

Releases are cut from version tags `vX.Y.Z` via `.github/workflows/release.yml` — see
[`AGENT.md`](AGENT.md) for the version-bump policy.

## License

Apache 2.0 — see [`LICENSE`](LICENSE). Uses [jadx](https://github.com/skylot/jadx) (also
Apache 2.0).
