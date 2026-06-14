# De-indirection SSA Arg-Binding Fix Plan

This document is a self-contained starting point for a fresh agent to fully implement
the fix that unblocks **reflective de-indirection** in the `string-decrypt` / Constant
Deobfuscator plugin. It assumes no prior context. Read it top-to-bottom before editing.

The de-indirection feature is already written but **gated off** because it produces
**invalid decompiled code**. The single root cause is identified and small in surface
area; the work is to make it SSA-correct, re-validate against the real-APK golden, and
re-enable it.

---

## 1. TL;DR of the blocker

`DeindirectionResolver` rewrites a reflective call
`handle.invoke(recv, new Object[]{args...})` into the direct call it stands for, e.g.
`Class.class.getMethod("forName", String.class).invoke(null, "java.math.MathContext")`
→ `Class.forName("java.math.MathContext")`.

When it does this for a candidate that is a **wrapped (inlined) argument** of another
instruction, the synthesized `InvokeNode`'s `RegisterArg`s are **never bound into their
`SSAVar` use-lists**. jadx's later type inference relies on those use-lists, so it
mis-infers the type of *unrelated* registers. Observed symptom on the golden:

```
v1.4.1 (correct):    Long.valueOf(i2)
v1.5.0 (de-indir):   Long.valueOf((Object) i2)      // INVALID — no Long.valueOf(Object) overload
```

`Long.valueOf((Object) i2)` does not compile. This is a soundness regression, so the
feature was disabled in v1.5.1 (`string-decrypt.deindirect-reflection` defaults to
`false`, marked experimental) and the golden was restored to the correct 641-line output.

**The fix is to rebind the replacement's args** in the wrapped-replacement path, mirroring
what `BlockUtils.replaceInsn` already does for top-level replacements.

---

## 2. Where everything lives

Standalone Gradle project (its own git repo, branch `main`):
`/opt/github/jadx/jadx-plugins/jadx-string-decrypt/`

Key files (all under `src/main/java/jadx/plugins/stringdecrypt/`):

- `StringDecryptPass.java` — the decompile pass. Owns the replacement pipeline.
  - `visit(MethodNode)` — two loops per block:
    1. `replaceWrapped(...)` — recurses into **wrapped (inlined)** args and replaces them
       **in place** via `insn.setArg(i, InsnArg.wrapInsnIntoArg(repl))`. **THIS PATH HAS
       THE BUG** (no rebind).
    2. `tryReplace(...)` on each top-level insn → `BlockUtils.replaceInsn(mth, block,
       insn, repl)` which **does** rebind (`insn.rebindArgs()`).
  - `tryReplace(...)` runs the ordered `resolvers` list (first non-null wins).
- `Resolver.java` / `ResolveContext.java` — the resolver seam. `Resolver.resolve(ctx)`
  returns the replacement `InsnNode` or `null`.
- `DeindirectionResolver.java` — the de-indirection resolver (last in the pipeline). Contains
  the buggy `dup()` and the (currently dormant) `Method.invoke` meta-peel + primitive boxing.
- `ReplacementFactory.java` — value→IR emission for the const folders (not directly involved,
  but `copyReplacementMetadata` is shared).
- `ObjectEvaluator.java` — `resolveCallTarget(InsnArg, depth)` resolves a `Method` handle
  (host `java.lang.reflect.Method` for JDK targets, or `AppMethodRef` for app targets) to a
  `CallTarget`.
- `CallTarget.java` — `{ClassInfo declClass, String name, List<ArgType> params, ArgType
  returnType, boolean isStatic}`.
- `StringDecryptOptions.java` — option `deindirect-reflection` (field `deindirectReflection`,
  default **false**). Re-enable here once fixed (flip field default + the `.defaultValue(...)`).

Relevant jadx-core (read-only reference) under `/opt/github/jadx/jadx-core/src/main/java/`:

- `jadx/core/dex/nodes/InsnNode.java` → `rebindArgs()` (~line 501): for each `RegisterArg`
  arg does `arg.getSVar().use(arg); ...updateUsedInPhiList();`, and recurses into
  `InsnWrapArg` via `getWrapInsn().rebindArgs()`.
- `jadx/core/utils/BlockUtils.java` → `replaceInsn(mth, block, i, insn)` (~line 1399):
  unbinds the old insn, sets the new one, and calls `insn.rebindArgs()`.
- `jadx/core/utils/InsnRemover.java` → `unbindArgUsage`, `unbindInsn`, `unbindAllArgs`.
- `jadx/core/dex/instructions/args/SSAVar.java` → `use(RegisterArg)`, `removeUse`,
  `getUseList`, `updateUsedInPhiList`.
- `jadx/core/dex/instructions/args/RegisterArg.java` → `getSVar()`, `duplicate()` and
  overloads `duplicate(int regNum, SSAVar sVar)`.

The pass runs `.after("ReplaceNewArray").before("RegionMakerVisitor")`, i.e. SSA is
established and object arrays are already `FilledNewArrayNode`, but type inference and
region making still run **after** us — which is why a broken use-list corrupts types.

---

## 3. How to reproduce / validate (the test harness)

Real-APK golden regression test (this is the ONLY accepted test style — no synthetic):

- Harness: `src/test/java/jadx/plugins/stringdecrypt/RealApkDeobfTestBase.java`
- Test: `src/test/java/jadx/plugins/stringdecrypt/AliCrackme1Test.java`
- Sample: `src/test/resources/test-samples/ali-crackme-1.apk` → class `k2015.a1.Check`
- Golden: `src/test/resources/golden/k2015.a1.Check.java` (currently the **correct** 641-line
  baseline, de-indirection off)

Build/test command (note the GRADLE_USER_HOME gotcha — the wrapper otherwise resolves to a
non-writable `/root/.gradle`):

```bash
cd /opt/github/jadx/jadx-plugins/jadx-string-decrypt
export GRADLE_USER_HOME=/home/arsylk/.gradle
./gradlew test --rerun-tasks --console=plain            # asserts vs golden
./gradlew test -DupdateGolden=true --rerun-tasks         # regenerate golden after an intended change
```

To reproduce the bug: temporarily flip `deindirectReflection` to default `true` (field
at `StringDecryptOptions.java` line ~33 AND the `.defaultValue(false)` for
`deindirect-reflection` at ~line 89), then `-DupdateGolden=true` and inspect the golden:

```bash
grep -n 'valueOf((Object)' src/test/resources/golden/k2015.a1.Check.java   # bug present == matches
```

The fix is correct when, **with de-indirection ON**, the golden:
1. contains **zero** `valueOf((Object)` (no spurious `(Object)` casts anywhere),
2. shows the expected direct calls (e.g. `Class.forName("java.math.MathContext")`,
   `Integer.class`, and — once the meta-peel/boxing path is reached —
   `Integer.valueOf(Integer.parseInt(str))`),
3. is byte-for-byte deterministic across repeated `--rerun-tasks` runs,
4. has no `JADX ERROR` / "Inconsistent" / `JADX WARN` markers.

---

## 4. Root cause (confirmed)

Two replacement paths in `StringDecryptPass`:

1. **Top-level** (`tryReplace` loop) → `BlockUtils.replaceInsn(mth, block, oldInsn, newInsn)`
   → internally calls `newInsn.rebindArgs()`. Register args of the replacement get added to
   their `SSAVar` use-lists. **Correct.**

2. **Wrapped / inlined** (`replaceWrapped`):
   ```java
   InsnNode repl = tryReplace(mth, ev, oev, wrapInsn, decrypted, builtArrays, outerIsLookup);
   if (repl != null) {
       InsnRemover.unbindArgUsage(mth, arg);            // unbinds the OLD wrapped insn's uses
       insn.setArg(i, InsnArg.wrapInsnIntoArg(repl));   // sets the NEW wrap — NO rebindArgs()!
   }
   ```
   The new `repl` is installed without `rebindArgs()`. For the pre-existing resolvers this
   was harmless: their replacements are `ConstStringNode` / `ConstClassNode` / `CONST` /
   `FilledNewArrayNode`-of-consts — **no `RegisterArg`s**, so there was nothing to bind.

`DeindirectionResolver` is the first resolver that emits an `InvokeNode` **carrying
`RegisterArg`s** (the receiver/args of the direct call, produced by `dup()`). Those
`RegisterArg`s reference real `SSAVar`s but are never added to the use-lists. The resulting
use-list inconsistency makes the type-inference pass (which runs after this pass) compute a
wrong join for unrelated registers — `i2` widens to `Object`, so jadx emits `(Object) i2`.

`DeindirectionResolver.dup()` today:
```java
private static @Nullable InsnArg dup(InsnArg arg) {
    if (arg instanceof RegisterArg)  return ((RegisterArg) arg).duplicate();         // unbound dup
    if (arg.isInsnWrap())            return InsnArg.wrapInsnIntoArg(((InsnWrapArg) arg).getWrapInsn().copy()); // copy not rebound
    if (arg instanceof LiteralArg)   { ... }                                          // fine, no SSA
    return null;
}
```
`RegisterArg.duplicate()` makes a new `RegisterArg` pointing at the same `SSAVar` but does
**not** register the use; `InsnNode.copy()` likewise duplicates inner register args without
binding. Both rely on a later `rebindArgs()` that, in the wrapped path, never happens.

---

## 5. The fix

Core change (necessary and likely sufficient): **rebind the replacement in the wrapped
path**, exactly like `BlockUtils.replaceInsn` does for the top-level path.

In `StringDecryptPass.replaceWrapped`, after installing the wrapped replacement:

```java
if (repl != null) {
    InsnRemover.unbindArgUsage(mth, arg);
    InsnArg wrapped = InsnArg.wrapInsnIntoArg(repl);
    insn.setArg(i, wrapped);
    repl.rebindArgs();   // <-- bind the replacement's register args into their SSAVar use-lists
}
```

Notes / things to verify while implementing:

- `repl.rebindArgs()` recurses into nested `InsnWrapArg`s, so a de-indirected call that itself
  wraps sub-calls (the `Integer.valueOf(Integer.parseInt(str))` boxing form) gets fully bound
  in one call. Good.
- `rebindArgs()` also calls `ssaVar.setAssign(resArg)` for the **result** reg. A wrapped
  replacement should NOT have a result reg (its value flows through the wrap). Confirm
  `DeindirectionResolver` does not set a result on wrapped replacements — it currently copies
  metadata via `ReplacementFactory.copyReplacementMetadata`, which **does** set the result reg
  from the original. For the wrapped case that is wrong; the result reg belongs to the consumer.
  **Action:** split metadata handling — only copy the result reg when the replacement is
  installed at top level; for a wrapped replacement copy attributes/offset/metadata but NOT the
  result register. (Top-level `replaceInsn` already manages the result via its own logic, so
  copying it in the resolver may also be redundant/foot-gunny there — verify and prefer letting
  the install site own the result reg.)
- Keep `dup()` using `RegisterArg.duplicate()` / `InsnNode.copy()` — that part is fine **as long
  as** a subsequent `rebindArgs()` runs. Do not hand-manage `SSAVar.use(...)` in `dup()`; let
  `rebindArgs()` be the single binding point (avoids double-use entries).
- The OLD reflective invoke + its `Object[]` builder become dead after the rewrite. The pass's
  existing cleanup (`removeDeadPureInsns` / `removeDeadArrayBuilds` / `cleanupArrayBuild`) plus
  the `InsnRemover.unbindArgUsage(mth, arg)` on the replaced wrap should unbind their uses.
  Verify the net use-list for each moved register is exactly: (old use removed) + (new use
  added) = consistent. If a moved register ends up with a stale extra use, that is the next
  thing to chase.

After the core fix, re-enable the option (flip both the field default and the
`.defaultValue(...)` to `true`, drop "experimental" from the description), regenerate the
golden, and apply the §3 validation checklist.

---

## 6. After it's sound: finish the de-indirection feature

`DeindirectionResolver` already contains (dormant) the logic for the high-value cases; once
binding is correct, validate and extend in this order, regenerating + eyeballing the golden at
each step:

1. **Static targets, reference return** (already proven to transform correctly pre-bug):
   `Class.class.getMethod("forName"/"getField"/"getMethod").invoke(...)` → direct.
2. **`Method.invoke` meta-peel** (implemented, untested): the obfuscator double-wraps app calls
   as `Method.class.getMethod("invoke").invoke(realHandle, recv, argsArray)`. `deindirect()`
   detects the resolved handle being `java.lang.reflect.Method.invoke` and recurses on
   `realHandle`. Confirm the IR arg layout: outer invoke args are
   `[metaHandle, realHandle, metaArgsArray]` where `metaArgsArray` is a 2-element
   `FilledNewArrayNode {realReceiver, realArgsArray}`.
3. **Primitive-return boxing** (implemented, untested): `Integer.parseInt(str)` returns `int`
   but the reflective context is `Object`; wrap as `Integer.valueOf(Integer.parseInt(str))`.
   `box()` builds this. Validate the boxed `InvokeNode` binds cleanly.
4. **Instance methods** (not started): drop the `if (!target.isStatic) return null` guard and
   handle the receiver. The receiver arg is typed `Object` in the reflective context, so the
   direct `VIRTUAL` `InvokeNode` may need an inserted `CHECK_CAST` to `target.declClass` (or rely
   on jadx inserting it — verify it does not produce inconsistent code). This is the riskiest
   layer; do it last and lean on the golden.
5. **Dead-scaffold cleanup** (separate, optional): once calls are direct, the
   `X.class.getMethod(...).setAccessible(true);` no-op statements and the
   `InvocationTargetException` unwrap try/catches are dead. Removing them is a readability win but
   has a minor soundness caveat (reflective lookups can in principle throw); gate carefully.

---

## 7. Architectural context (so the fix stays in-grain)

- The replacement layer was refactored in v1.4.1 into a **`Resolver` pipeline**
  (`Resolver` + `ResolveContext`, ordered list built in the `StringDecryptPass` constructor)
  with a single **`ReplacementFactory`** value→IR boundary. De-indirection is just the last
  `Resolver`. Keep new replacement strategies as resolvers; do not add branches to
  `tryReplace`.
- The same seam is the foundation for the planned `.jadx.kts` **script pipelines** (see
  `PIPELINE_SCRIPTING_PLAN.md`). A script resolver will also emit `InvokeNode`/register-bearing
  replacements, so **the wrapped-path rebind fix here is a prerequisite for that feature too** —
  fixing it once benefits both.
- Versioning (`AGENT.md`): bump `build.gradle.kts` `version` and
  `StringDecryptPlugin.VERSION` together. The binding fix + re-enable is a MINOR (new working
  capability): current line is `1.5.1`, so ship as `1.6.0`. Keep them in sync; BuildInfo is
  generated from `build.gradle.kts`.

---

## 8. Acceptance criteria

1. With `deindirect-reflection` ON, `AliCrackme1Test` passes and the golden has **zero**
   `(Object)`-cast artifacts and no `JADX ERROR/WARN/Inconsistent` markers.
2. Output is deterministic across repeated runs.
3. With the option OFF, output is unchanged from the current 641-line baseline.
4. The static + meta-peel + boxing cases visibly de-indirect (`Class.forName(...)`,
   `Integer.class`, `Integer.valueOf(Integer.parseInt(str))`, `BigDecimal.valueOf(...)`).
5. No regression in any other folding (the golden is the gate).
6. Version bumped and in sync; option un-gated (default true, "experimental" removed) only after
   1–5 hold.

## 9. Bottom line

The de-indirection engine is built and the resolver seam is right. The only thing standing
between "experimental, off" and "on by default" is **binding the synthesized call's register
args into their SSA use-lists in the wrapped-replacement path** (`repl.rebindArgs()` in
`StringDecryptPass.replaceWrapped`, plus not copying a result register onto a wrapped
replacement). Fix that, validate against the real-APK golden, then walk the §6 layers to reach
the app-dispatch calls.
