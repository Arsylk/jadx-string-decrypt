# Comparison materials

Side-by-side decompiler output (and the source dex files) used to produce the
[JEB vs jadx+plugin table in the top-level README](../README.md#aggregate-comparison-vs-jeb-73-paired-files-of-commistraljon).

All files are decompiled / extracted from one APK (`com.mistral.jon`), a real-world
sample whose obfuscator inserts opaque table-based constant expressions of the form
`((int) KEY[k]) ^ c` everywhere. It is the kind of code this plugin is built for.

## Layout

```
comparison/
├── jadx-output/    # 73 .java files — jadx (this fork) WITH the plugin enabled
├── jeb-output/     # 73 .java files — JEB on the same dex, with no deobfuscation
└── dex/
    ├── classes.dex                # the primary dex (4.0 MB; the input to both tools)
    ├── lncoecijgsyzlsoz.dex       # secondary dex (428 KB) — runtime-loaded
    └── SHA256SUMS
```

Same 73-file slice from each tool (the `com.mistral.jon` package, before the obfuscator's
own `z.*` classes). The slice is what makes the metrics meaningfully paired.

## Aggregate numbers (recap)

| Metric | jadx + this plugin | JEB | Winner |
| --- | ---: | ---: | --- |
| Residual XOR (`^`) operators | **5** | **10,536** | this plugin (~2,000×) |
| Total output lines | **4,571** | 13,630 | this plugin (3× more compact) |
| Decrypted string literals in source | **+45** in place | 0 | this plugin |
| Decompile failures (`throw new UnsupportedOperationException`) | 5 | 5 | tied — *same exact methods* |

The 5 tied failures are on the pathologically-obfuscated service methods (`WebViewService`,
`Ov2Service`, `AccessibilityRequestService`, `FgSvc`, `OvService`) — a property of the
obfuscator, not of either decompiler. They land at jadx's `RegionMaker` overflow limit and
JEB's equivalent recovery limit.

## Reproduce

```bash
# 1. install the plugin into any stock jadx (must not already bundle it — see top README)
jadx plugins --install github:Arsylk:jadx-string-decrypt
jadx plugins --list      # expect: string-decrypt  Constant Deobfuscator v1.0.1

# 2. decompile the dex
jadx -d /tmp/jadx_out comparison/dex/classes.dex

# 3. (optional) diff against the prebuilt outputs here
diff -r /tmp/jadx_out/sources/com/mistral/jon comparison/jadx-output/
```

Numbers you should reproduce on a default-settings run over `classes.dex`:

```
INFO  - string-decrypt: reconstructed 409 constant(s) (364 arrays, 45 scalars),
        detected 1 decryptor(s), snapshotted 197 pure helper body(ies),
        195 mutable static field(s) excluded from folding
ERROR - finished with errors, count: 18      ←  same 18 in baseline jadx (= no regression)
total decrypted strings : 1104
total int folds         : 81542
residual `^` operators  : 238 (over the *whole* output — the 73-file slice has 5)
```

## A note on the apk (for the curious)

`classes.dex` is **not** trivial to extract from `com.mistral.jon.apk`: the zip entry has
the *encryption flag* set (`flag_bits & 1`) although the bytes are plain deflate. It's an
anti-static-analysis decoy — standard `unzip` and Python `zipfile` refuse with
"password required", but the data isn't actually encrypted. jadx ignores the flag and
proceeds. To extract by hand:

```python
import zipfile
class IgnoreFakeEnc(zipfile.ZipFile):
    def _RealGetContents(self):
        super()._RealGetContents()
        for i in self.infolist():
            i.flag_bits &= ~1
with IgnoreFakeEnc('com.mistral.jon.apk') as z:
    z.extract('classes.dex', '.')
```

## Caveat — what these files are

These are **research samples** of a real (surveillance) Android app, kept here purely so
the comparison metrics in the top README are reproducible / browsable. They are not meant
to be run. Do not run the apk.
