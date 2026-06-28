# Long-Chain De-indirection Plan — fully deobfuscate `k2015.a1.Check`

Self-contained handoff. Read top-to-bottom before editing. Builds directly on the now-shipped
v1.6.0 de-indirection (static reference-return + meta-peel + primitive-arg `valueOf`, all sound).
This plan takes it from "the easy calls de-indirect" to "the **whole reflective chain** collapses and
the dead scaffolding is gone," so `k2015.a1.Check` reads as ordinary Java.

The two fixes that un-gated v1.6.0 — SSA `rebindArgs()` in `StringDecryptPass.replaceWrapped`, and
stripping the inherited `METHOD_DETAILS` in `ReplacementFactory.copyReplacementMetadata` — are the
prerequisites everything here relies on. Don't regress them.

---

## STATUS (v1.8.0 — §§3.1–3.5 DONE; golden 608→550→277 lines)

Implemented and shipped (golden 608→550 lines, **every real-logic reflective call de-indirects**, 0
`(Object)` casts, deterministic):
- §3.1 instance methods — `VIRTUAL` invoke + receiver `CHECK_CAST` via `coerceToType`.
- §3.2 empty `new Object[0]` (NEW_ARRAY size 0) → no-arg calls.
- §3.3/§2.5 arg mapping — bare literals → primitive params; reference args cast-to-param;
  `unwrapValueOf` peels CHECK_CAST/CAST and trusts the resolved `valueOf(prim)` signature.
- §3.4 soundness — `coerceToType` + `TypeCompare.isNarrowOrEqual` decide casts; unresolved/odd shapes
  still return null (decline). No separate verify pass needed once coercion guarantees arg types.
- **Not in the original plan but required** (the actual remaining blockers, found via the §5 trace):
  (a) reflective-`getMethod` handle resolution — `MethodReflHandler.safeInvoke` now runs pure `Class`
  member lookups on the host (`ClassHandler.isReflectiveLookupAllowed`); (b) JDK Class-constant
  resolution — `ObjectEvaluator.resolveConstClass` + `ClassHandler.isSafeToLoad` accept any
  `java.`/`javax.` class (`initialize=false`) so `MathContext.class` resolves as a param type.

**§3.5 — dead reflective-scaffold cleanup — DONE (v1.8.0, golden 550→277 lines).** Option
`string-decrypt.cleanup-reflection` (default on; gated by `deindirect-reflection`). Implemented as a
*conservative, provably-no-op* sweep in `StringDecryptPass.removeDeadReflectiveScaffold` (fixpoint):
removes a pure class-literal member lookup (`X.class.getMethod/getField/getConstructor(...)`, never
`forName` — class-literals don't trigger class init) whose result is consumed only by direct, void
`setAccessible(...)` calls (or nothing), and only when the lookup **re-resolves** via the
`ObjectEvaluator` (proves no `NoSuchMethodException`). It removes the lookup + those `setAccessible`
statements together. Verified: every removed line is scaffolding (272 `…getMethod(…).setAccessible`
+ 1 now-unused import); zero real logic touched; off ⇒ byte-identical to the 550-line output.

Deliberately **kept** (removing them is not provably safe, per the no-risk bar):
- reflective `Method.invoke(...)` calls — the only statements that can throw
  `InvocationTargetException`; removing one could orphan a `catch (InvocationTargetException)`. This
  also keeps the Category-B clusters (`method = X.class.getMethod(...); method.invoke(target, true)`)
  intact.
- `Class.forName(...)`-rooted lookups (potential static-initializer side effect).
- the ITE try/catch wrappers and `e.getTargetException()` unwraps.

**Remaining (optional, not soundness):** a `Boxed.valueOf(x).primValue()` → `x` simplifier to strip the
obfuscator's redundant box/unbox that survives verbatim
(`Integer.valueOf((int) Integer.valueOf(...).intValue())`). Removing the kept scaffolding above would
need: proving forName targets are already-initialized, proving `setAccessible` targets are public, and
collapsing now-dead ITE catches — each a separate, riskier analysis.

---

## 1. Current state (measured on the golden, v1.6.0)

`src/test/resources/golden/k2015.a1.Check.java` is **608 lines**. De-indirection already fires ~294
times (static `Class.forName(...)`, `Integer.class`/`String.class`/`Object.class`, and primitive-arg
`Long.valueOf(i2)` / `Integer.valueOf((int)…)`). Output is sound: **zero `(Object)` casts, zero
`JADX ERROR/WARN/Inconsistent`**, deterministic, brace-balanced.

But it is still **~70 % dead reflective scaffolding**:

| Artifact | Count | What it is |
|---|---:|---|
| `setAccessible(true)` lines | 304 | no-ops on resolved handles |
| `X.class.getMethod(...).setAccessible(true);` standalone | 202 | pure dead lookups |
| residual `…​.invoke(…)` calls | ~60 | the real blockers |
| `Method methodN = (Method) …invoke(…)` handle lookups | 16 | dead once the call is direct |
| `InvocationTargetException` occurrences | 39 | obfuscator try/catch wrappers |

The "real logic" hidden underneath is tiny — e.g. lines 505-536 collapse to roughly:

```java
long jLongValue = bigDecimal3.scaleByPowerOfTen(7 + ((((1 + j) * j) * (2 + j)) % 6)).longValue();
```

and the parseInt chain (golden ~84-92) is just `int jIntValue = Integer.parseInt(str);`.

---

## 2. Why the long chains decline (taxonomy, with evidence)

All in `DeindirectionResolver.deindirect()` / its helpers. In priority order:

### 2.1 Instance methods are hard-declined — **THE #1 BLOCKER**
```java
if (!target.isStatic) {
    return null; // static targets only (no receiver re-typing yet)
}
```
The residual chain is dominated by instance calls: `BigDecimal.{scaleByPowerOfTen, add, divide,
longValue}`, `String.{equals, hashCode, toString}`, `Long.longValue`, `Integer.intValue`. Every one
declines here. Until this branch exists, the chains cannot collapse no matter what else is fixed.

Evidence: `method82.invoke((Long) method81.invoke(method78, (BigDecimal) method80.invoke(method75,
bigDecimal3, new Object[]{…}), new Object[0]), new Object[0])` — `method75=BigDecimal.scaleByPowerOfTen`,
`method78=BigDecimal.longValue`, `method82=Long.longValue`, all instance.

### 2.2 No-arg / non-`FilledNewArray` argument arrays decline
```java
if (!(prod instanceof FilledNewArrayNode)) {
    return null;
}
```
A no-arg instance method (`longValue()`, `hashCode()`, `toString()`) is invoked with `new Object[0]`,
which is an `InsnType.NEW_ARRAY`, not a `FilledNewArrayNode`. `arrayElements` returns null → decline.
`ObjectEvaluator.resolveVarargsAllowingNull` already shows the pattern to copy (NEW_ARRAY of const
size + `APUT` walk). Needed for the whole longValue/hashCode/toString family.

### 2.3 A still-reflective sibling argument blocks the outer call
`(BigDecimal) method55.invoke(method37, null, new Object[]{Long.valueOf(i2), (Integer)
method54.invoke(null, 0)})` — `method37=BigDecimal.valueOf(long,int)` is **static and would
de-indirect**, but its 2nd element `method54.invoke(null, 0)` is itself an un-de-indirected reflective
call, so `mapArg`'s `unwrapValueOf` fails and the whole outer call declines. Today the inner arg is
only de-indirected by the depth-first side effect of `replaceWrapped`; that is not happening for these
elements (see 2.4). The resolver must **recursively de-indirect an element as part of mapping it**, so
a chain resolves in one deterministic pass independent of traversal order.

### 2.4 `method54.invoke(null, 0)` single-element-varargs shape (investigate)
`method54 = Integer.class.getMethod("valueOf", Integer.TYPE)` is a *direct* handle, yet
`method54.invoke(null, 0)` does not de-indirect to `Integer.valueOf(0)`. The rendered `0` (not
`new Object[]{0}` / `Integer.valueOf(0)`) suggests `getArg(2)` is not the `FilledNewArrayNode`
`arrayElements` expects. Confirm the real IR shape (see §5 diagnostic) and broaden `arrayElements`
accordingly — this is likely the same root as 2.2.

### 2.5 Primitive arg supplied as a reference (boxed) value
`mapArg` only unwraps a literal `Boxed.valueOf(prim)`. Once 2.3 lets a nested call resolve, a primitive
param may receive a *reference-typed* boxed result (e.g. an `Integer` from a de-indirected nested call).
`mapArg` must then emit the unbox accessor (`intValue()`/`longValue()`/…) or accept the box where the
param is the matching wrapper. Keep it type-checked (§4), not best-effort.

---

## 3. The plan (layers — each lands behind the golden, regenerated + eyeballed)

Order matters: 3.1 unlocks the most, 3.5 is what makes the file actually *read* as deobfuscated.

### 3.1 Instance-method de-indirection (core unlock)
Replace the static-only guard with an instance branch in `deindirect()`:

- Resolve `target` as today. If `!target.isStatic`:
  - Build `InvokeNode direct = new InvokeNode(mi, InvokeType.VIRTUAL, 1 + callArgs.size())`.
  - `direct.addArg(receiverExpr)` **first** (VIRTUAL → `getArg(0)` is the instance, `getFirstArgOffset()==1`),
    then the mapped args.
  - **Receiver typing:** `receiverExpr = dup(receiverArg)`, and if `receiverArg.getType()` is not
    already assignable to `target.declClass.getType()`, wrap it in a `CHECK_CAST` to that type
    (`new IndexInsnNode(InsnType.CHECK_CAST, declClassType, 1)` + `AFlag.EXPLICIT_CAST`, wrapped). The
    reflective receiver is typed `Object`, so this cast is normally required and is sound (the runtime
    object *is* that class — the obfuscator looked the method up on it).
  - Primitive-return instance methods (`longValue()`, `hashCode()`) reuse the existing `box()` path so
    the result still fits the surrounding `Object` slot.
- Decline (return null) if `declClass` is unknown/unresolved or the receiver can't be produced — never
  emit an unchecked virtual call.

jadx renders `((BigDecimal) recv).scaleByPowerOfTen(…)` and elides the cast when `recv` is already
that type. With `METHOD_DETAILS` stripped (already done), `MethodInvokeVisitor` types the args from the
real `mi`, so no spurious casts.

### 3.2 No-arg & aput-built argument arrays
Generalize `arrayElements(InsnArg)`:
- `FilledNewArrayNode` → as today.
- `NEW_ARRAY` with const size 0 → empty list (enables no-arg calls).
- `NEW_ARRAY` of const size N populated by `APUT` → collect elements by index (mirror
  `ObjectEvaluator.resolveVarargsAllowingNull`, but return `InsnArg`s, refusing on a missing index).
This alone turns the entire `longValue()/hashCode()/toString()` family resolvable.

### 3.3 Deterministic recursive argument resolution
Add `@Nullable InsnArg resolveArg(ResolveContext ctx, InsnArg elem, ArgType param, int guard)` used by
`mapArg`:
1. If `elem` is a wrapped reflective `Method.invoke`, attempt `deindirect(...)` on it first (guarded
   recursion, reuse `MAX_PEEL`/a separate budget). On success continue with the synthesized node.
2. Then apply the existing unwrap/box/cast logic against `param`.
This removes reliance on `replaceWrapped`'s traversal order → **deterministic**: a call resolves iff its
transitive args resolve, regardless of where the pass visits first. Fixes 2.3.

### 3.4 Soundness verification gate (the correctness guarantee)
Before returning any synthesized call, run `verifyDirectCall(target, receiverExprType, mappedArgTypes)`:
- receiver assignable to `target.declClass` (after the inserted cast);
- each mapped arg assignable/primitive-convertible to its `target.params[i]`;
- `target.returnType` assignable to the reflective context's expected type (the cast jadx had on the
  old `(T) handle.invoke(...)`).
Any failure → **return null** (leave reflective). This is what keeps "resolve longer chains" from ever
degrading into "emit code that doesn't compile." Use `root.getTypeCompare()` /
`TypeCompare.compareTypes` for the checks (same engine `MethodInvokeVisitor` uses).

### 3.5 Dead reflective-scaffold cleanup (new option `string-decrypt.cleanup-reflection`, default on)
A post-de-indirection sweep over the method (a fixpoint, like `removeDeadPureInsns`) that removes the
now-dead obfuscator scaffolding:
- **Dead handle lookups**: `Class.getMethod/getDeclaredMethod/getField/getConstructor/forName` (and the
  `Method`/`Field`/`Constructor` results) whose result SSAVar has no remaining real use. These are
  `INVOKE`s, so `isDeadCandidate`/`removeDeadPureInsns` won't touch them — add an explicit, *allowlisted*
  remover keyed on the known reflective-lookup `MethodInfo`s.
- **`setAccessible(true)` no-ops**: both `methodN.setAccessible(true)` and the standalone
  `X.class.getMethod(...).setAccessible(true);` forms — pure no-ops on resolved/public members.
- **`try { … } catch (InvocationTargetException e) { throw e.getCause(); }` collapse**: once the try
  body no longer contains a `Method.invoke`, the ITE catch is unreachable; unwrap to the body. This is
  the **riskiest** sub-step — do it last, behind its own sub-gate, and validate that exceptions the body
  can still throw are preserved.

Soundness: only remove a lookup whose target we **resolved** (the member provably exists → the lookup
can't throw `NoSuchMethodException`). Refuse on anything still referenced or unresolved.

---

## 4. What to add (concrete checklist)

In `DeindirectionResolver`:
- [ ] instance branch in `deindirect()` (VIRTUAL invoke + receiver `CHECK_CAST`) — §3.1
- [ ] `checkCast(InsnArg recv, ArgType declType)` helper
- [ ] generalize `arrayElements` for NEW_ARRAY (empty + aput) — §3.2
- [ ] `resolveArg(...)` recursive arg de-indirection + broaden `mapArg` unbox — §3.3 / §2.5
- [ ] `verifyDirectCall(...)` soundness gate — §3.4

In `StringDecryptPass` (or a small new `ReflectionScaffoldCleanup` helper invoked from `visit`):
- [ ] allowlisted dead reflective-lookup remover + `setAccessible` no-op remover — §3.5
- [ ] ITE-wrapper collapse (sub-gated) — §3.5

In `StringDecryptOptions`:
- [ ] `cleanup-reflection` bool option (default true), getter, wired into the pass.

In `StringDecryptPlugin` / `build.gradle.kts`:
- [ ] version bump (MINOR — new working capability): `1.6.0` → `1.7.0`, keep `VERSION` and gradle
  `version` in sync.

No new "technology" is strictly required — this is all the existing syntactic-resolver + jadx IR
(`InvokeNode`/`IndexInsnNode`/`TypeCompare`). The one genuinely new piece is the **scaffold-DCE**
(§3.5), which is a small specialized dead-code sweep, not a new framework.

---

## 5. Diagnostic harness (do this FIRST — it's how the v1.6.0 bug was actually found)

Don't guess why a call declines. Add a temporary, property-gated trace:
- A `@Nullable InsnNode resolve(...)` that, when `-Dddbg` is set, logs *why* each candidate
  `Method.invoke` declined (handle unresolved / instance / args shape / sibling unresolved / verify
  failed), and a debug `JadxDecompilePass` `.after("MethodInvokeVisitor")` dumping the final
  `MethodUtils.getMethodDetails(inv)` + arg types for the calls of interest. (Forward `-Dddbg` to the
  test JVM via `build.gradle.kts` `systemProperty`.) Remove before shipping.

This converts "why doesn't chain X resolve" from speculation into a one-line ground-truth answer, and
will immediately settle the 2.4 single-element-varargs question.

## 6. Validation & acceptance

Harness unchanged: `RealApkDeobfTestBase` + `AliCrackme1Test`, golden = `k2015.a1.Check.java`.
```bash
export GRADLE_USER_HOME=/home/arsylk/.gradle
./gradlew test --rerun-tasks --console=plain                 # assert
./gradlew test -DupdateGolden=true --rerun-tasks             # regen after an intended change
```
Per layer: regenerate, then verify the golden has **zero** new `(Object)` casts, **zero**
`JADX ERROR/WARN/Inconsistent`, is brace/paren-balanced, and is byte-deterministic across two
`--rerun-tasks` runs. With the master option OFF, output must stay byte-identical to the v1.6.0 641-line
OFF baseline (criterion-3 style neutrality check, exactly as in the prior fix).

**Mechanical correctness gate (recommended new "technology"):** add a CI/test step that compiles the
regenerated golden with `javac` against `android.jar` + JDK. A clean compile is a hard, deterministic
proof of correctness — far stronger than eyeballing `(Object)` casts. Worth adding even before this
work as a guard for all de-indirection.

**Done when** `k2015.a1.Check`:
1. has **no residual `…​.invoke(`**, **no** `Method/Field/Constructor` handle lookups, **no** dead
   `setAccessible`, **no** ITE wrapper scaffolding;
2. shows the real logic directly (`bigDecimal3.scaleByPowerOfTen(…).longValue()`,
   `Integer.parseInt(str)`, `BigDecimal.valueOf(i, 0)`, `str.equals(…)`, …);
3. compiles with `javac`;
4. is deterministic and free of JADX markers;
5. is dramatically shorter (expect ~150-250 lines, down from 608).

---

## 7. Risk / sequencing notes

- 3.1 + 3.2 + 3.3 + 3.4 are the sound core; ship them first (one MINOR), validate the golden collapses,
  *then* tackle 3.5 cleanup (which changes far more lines and carries the ITE-collapse soundness caveat).
- Keep the static path exactly as-is; the instance branch is additive.
- The `Resolver`/`ReplacementFactory` seam (v1.4.1) and the `.jadx.kts` pipelines
  (`PIPELINE_SCRIPTING_PLAN.md`) both benefit: a script resolver emitting instance calls will reuse the
  receiver-cast + verify helpers added here.
