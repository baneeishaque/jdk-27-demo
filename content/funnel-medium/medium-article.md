# JDK 27, JEP by JEP: the maximum-detail guide

JDK 27 shipped on 2026-09-15 with nine JEPs. Four are final and change defaults under your
feet. Four are previews that reshape concurrency, data handling, and crypto code. One is
the Vector API's twelfth incubator round.

This is the long version: every JEP with the API shape, the code that proves it, the
measurement, the gotcha, and the migration note. Everything was run on Apple Silicon
(aarch64, macOS) with OpenJDK 27 build `27+35`, and everything lives in a public repository
with tests and benchmark reports.

## The nine JEPs at a glance

| JEP | Feature | Status | Verdict |
| --- | --- | --- | --- |
| 523 | G1 becomes the default collector in all environments | Final | Free win |
| 527 | Post-quantum hybrid key exchange in TLS 1.3 | Final | Free win |
| 531 | Lazy constants | Third preview | Adopt early |
| 532 | Primitive types in patterns, `instanceof`, and `switch` | Fifth preview | Adopt early |
| 533 | Structured concurrency | Seventh preview | The headline |
| 534 | Compact object headers | Final | Free win |
| 536 | JFR in-process data redaction | Final | Free win |
| 537 | Vector API | Twelfth incubator | Spike hot loops |
| 538 | PEM encodings of cryptographic objects | Third preview | Adopt early |

## How I measured

- **JVM:** OpenJDK 27, build `27+35`, Apple Silicon, macOS.
- **Tests:** 33 JUnit 5 tests across 10 classes; each feature has at least one assertion
  that would fail on JDK 26.
- **JMH:** 2 forks, 3 × 1 s warmup, 3 × 1 s measurement, two input sizes (1,024 and 65,536
  floats), `SPECIES_PREFERRED`.
- **Wall-clock demos:** single run of the integration pipeline, 4 orders; the point is the
  ratio, not the absolute millisecond.

***

## Part 1 — The four final features

### JEP 523: G1 becomes the default collector everywhere

**What changed.** Ergonomic GC selection previously handed small machines — fewer than two
CPUs or under roughly 1.8 GB of heap — to the Serial collector. That made the collector a
function of your deployment environment: a container with a low CPU limit could silently
switch GC behaviour between environments.

JDK 27 always selects G1 unless you explicitly choose otherwise.

**The proof.**

```text
$ java -XX:ActiveProcessorCount=1 -XX:+PrintFlagsFinal -version | grep UseG1GC
bool UseG1GC = true {product} {ergonomic}
```

The `{ergonomic}` tag matters: no flag forced G1, the JVM chose it even on a one-CPU
machine.

**Migration note.** If your deployment carried `-XX:+UseG1GC` as a defensive flag, you can
drop it. If you intentionally run Serial or Parallel on small instances, keep your explicit
flag — it still wins.

### JEP 534: Compact object headers

**What changed.** The object header — mark word plus class pointer — drops from 96 bits to
64 bits on 64-bit platforms. Every object gets 8 bytes smaller; the savings compound across
arrays of small objects.

**The code that proves it.** The showcase measures per-allocation bytes with
`com.sun.management.ThreadMXBean#getCurrentThreadAllocatedBytes`, so the number is
independent of GC timing and heap state:

```java
long before = allocatedBytes();
Point p = new Point(1, 2);
long after = allocatedBytes();
// after - before == 16 on JDK 27 (24 with -XX:-UseCompactObjectHeaders)
```

**The measurement.**

| Layout | bytes per `Point(1,2)` |
| --- | --- |
| compact (default) | 16 |
| `-XX:-UseCompactObjectHeaders` | 24 |

**The gotcha.** The test asserts the *delta* between the two layouts, not a hard-coded
16/24. Allocation counters include per-allocation alignment effects, and hard-coding the
absolute number would make the test brittle across platforms. Run
`-XX:-UseCompactObjectHeaders` to see the pre-27 layout.

**Migration note.** Zero code changes. If you use `sun.misc.Unsafe` to poke at header
internals (some serialization and off-heap libraries do), verify against the new layout.

### JEP 527: Post-quantum hybrid key exchange in TLS 1.3

**What changed.** `X25519MLKEM768` — a hybrid of X25519 and ML-KEM-768 (FIPS 203) — becomes
the most preferred key exchange group for TLS 1.3. Clients offer quantum-resistant key
establishment by default; the handshake stays secure even if a future quantum computer
breaks X25519, and stays fast because ML-KEM is cheap.

**The proof.** The demo starts a loopback TLS 1.3 server and client with JSSE debug tracing,
then parses the trace:

```text
default supported groups: [X25519MLKEM768, x25519, secp256r1, secp384r1, secp521r1, ...]
hybrid is most preferred by default: true
pinned to x25519: [x25519]
```

**Two details the release notes skip.**

1. The JSSE debug trace is JSON-style: `"named groups": [...]` is the supported list, and
   `"named group": X25519MLKEM768` (singular) is an actual key share.
2. The client sends **two key shares** by default — hybrid and classic X25519 — so a server
   that ignores the hybrid entry still completes the handshake without a HelloRetryRequest.
   That doubles the initial flight slightly; it is a deliberate latency trade.

**Migration note.** Zero code changes. Two audiences need attention: tools that parse the
JSSE debug trace (format changed), and servers with strict group pinning that now reject
the hybrid entry — update the allow-list rather than disabling the client default.

### JEP 536: JFR in-process data redaction

**What changed.** Sensitive values are redacted *inside the JVM*, before a recording is
written. JDK 27 ships built-in redaction for common secrets — system properties and
environment variables whose names match patterns like `password`, `secret`, `token` — and
an API for custom redaction.

**The proof.** The showcase spawns a child VM with a secret system property, records
startup events, and then scans the raw recording bytes for the secret:

```text
jdk.InitialSystemProperty[db.password] = [[REDACTED]]
raw secret present anywhere: false
```

**The gotcha.** The child JVM needs the same `--enable-preview` flag as the parent when the
classes it loads were compiled with preview features — a spawned process without it fails
at class load, not with a warning. The demo wires the flag through explicitly.

**Migration note.** Zero code changes for the built-in patterns. Audit custom event fields
that carry credentials; add `Event#setRedacted` or the `jdk.jfr.Redacted` annotation where
the defaults do not reach.

***

## Part 2 — The four previews

### JEP 533: Structured concurrency

The headline of the release, seventh preview, and finally shaped like the mental model.

**The API in JDK 27.**

```java
try (var scope = StructuredTaskScope.open()) {
    Subtask<String> price  = scope.fork(() -> fetch("price", 40));
    Subtask<String> stock  = scope.fork(() -> fetch("stock", 60));
    Subtask<String> ship   = scope.fork(() -> fetch("shipping", 30));
    scope.join();
    return price.get() + stock.get() + ship.get();
}
```

`StructuredTaskScope<T, R, R_X extends Throwable>` now takes three type parameters.
Factory shapes: `open()`, `open(Joiner)`, `open(Joiner, UnaryOperator<Configuration>)`, and
`open(UnaryOperator<Configuration>)`.

Joiners: `allSuccessfulOrThrow()` (the default) and `anySuccessfulOrThrow()` — note the
name; there is no `anySuccessfulResultOrThrow`. A generic race needs an explicit type
witness:

```java
try (var scope = StructuredTaskScope.open(Joiner.<String>anySuccessfulOrThrow())) {
    scope.fork(() -> fetch("primary"));
    scope.fork(() -> fetch("replica"));
    return scope.join(); // first success wins; the loser is cancelled
}
```

**Timeouts moved into configuration.** `joinUntil(Instant)` is gone:

```java
try (var scope = StructuredTaskScope.open(
        Joiner.<String>anySuccessfulOrThrow(),
        cfg -> cfg.withTimeout(Duration.ofMillis(300)))) {
    ...
}
```

**Failure semantics.** When one subtask fails, `join()` throws with the original cause and
siblings are cancelled. The scope is the cancellation boundary — the same guarantee
`ExecutorService` never gave you.

**The gotcha that cost me an hour.** With `anySuccessfulOrThrow()`, a scope timeout does
**not** throw `CancelledByTimeoutException`. It arrives as an `ExecutionException` whose
*cause* is `CancelledByTimeoutException` (a `RuntimeException`). Catching the timeout
exception directly never fires; unwrap first.

**The measurement.** The showcase's order pipeline quotes four orders through three
simulated service calls (40 ms price, 60 ms stock, 30 ms shipping):

| Mode | Wall clock, 4 orders |
| --- | --- |
| sequential | ~570 ms |
| structured fan-out | ~258 ms |

One quote costs 130 ms sequentially but `max(40, 60, 30) = 60 ms` fanned out — ~2.2x end to
end. The test asserts both modes produce **identical quotes**, so the concurrency is
provably behaviour-preserving.

**Migration note.** Spike one endpoint: one scope per request, one `fork` per remote call,
`join()` before assembly. The mechanical change is small; the cancellation semantics are
the win. Keep a sequential fallback path behind a flag while you build confidence.

### JEP 531: Lazy constants

**The API.** The holder-class idiom becomes a first-class construct:

```java
static final LazyConstant<Services> SERVICES = LazyConstant.of(Services::new);
...
Services s = SERVICES.get(); // computed at most once
```

`LazyConstant<T> extends Supplier<T>`; the factories are `LazyConstant.of(Supplier)` and
`LazyConstant.ofLazy(...)` variants on `List`, `Set`, and `Map`.

**The gotcha the release notes bury: failure is permanent.** If the initial computation
throws, every subsequent `get()` throws `NoSuchElementException` whose message names the
original failure — the cause chain is not attached:

```text
java.util.NoSuchElementException: Unable to access the constant because
java.lang.IllegalStateException: database unavailable was thrown at initial computation
```

A null result from the supplier is wrapped the same way. Retryable work must be retried
*inside* the supplier; a failed constant never recomputes.

**Migration note.** Replace holder-class singletons and double-checked locking. Audit every
initializer for failure modes you previously assumed could be retried.

### JEP 532: Primitive types in patterns, `instanceof`, and `switch`

**What changed.** Pattern matching now converts primitive values when — and only when — the
conversion is exact:

```java
sealed interface JsonValue permits JsonNumber {}
record JsonNumber(double value) implements JsonValue {}

static String classify(JsonValue v) {
    return switch (v) {
        case JsonNumber(int i)    -> "int-exact number: " + i;
        case JsonNumber(long l)   -> "long number: " + l;
        case JsonNumber(double d) -> "double-rounded number: " + d;
    };
}
```

`42.0` takes the first case, `5_000_000_000.0` the second, `0.1` the third. No silent
truncation, no manual range checks.

**Two sharp edges.**

1. Case ordering encodes exactness: `int` before `long` before `double`. Put a `double`
   case after a `long` case on a `long` component and the compiler flags it unreachable —
   the conversion is never exact in that direction.
2. Nested types in case labels must be qualified (`JsonValue.JsonNumber(...)`), even inside
   the same file.

**Migration note.** Adopt in parsing and validation code first — anywhere you currently
write `instanceof Integer` followed by casts. The compiler's dominance checks will find
dead branches you did not know you had.

### JEP 538: PEM encodings of cryptographic objects

**The API.** Key material becomes a one-liner:

```java
String pem = PEMEncoder.of().encodeToString(privateKey);
PrivateKey back = PEMDecoder.of().decode(pem, PrivateKey.class);

String enc = PEMEncoder.of().withEncryption(password).encodeToString(privateKey);
PrivateKey dec = PEMDecoder.of().withDecryption(password).decode(enc, PrivateKey.class);
```

`PEMEncoder.of()` / `PEMDecoder.of()` cover public keys, private keys, encrypted private
keys, certificates, and the `PEM` container. `AsymmetricKey` extends `Key` and
`BinaryEncodable`, so `PublicKey` and `PrivateKey` objects encode directly — no
`getEncoded()` juggling.

**Two sharp edges.**

1. The `PEM(String, byte[])` constructor takes *textual* PEM content, not DER bytes. Passing
   `key.getEncoded()` (DER) produces a container that fails on decode.
2. `PEM` container objects cannot be encrypted directly; encrypt keys or
   `PKCS8EncodedKeySpec`/`X509EncodedKeySpec` instead.

**Migration note.** Replace base64 wrapper utilities and hand-rolled `BEGIN/END` builders.
Round trips are verified with `Key#equals` in the showcase — public, private, and encrypted.

***

## Part 3 — The incubator: Vector API (JEP 537)

Twelfth round, same story: SIMD works, and the API is stable enough to build on.

```java
static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;

static float dotVector(float[] a, float[] b) {
    var acc = FloatVector.zero(SPECIES);
    int i = 0;
    for (; i < SPECIES.loopBound(a.length); i += SPECIES.length()) {
        var va = FloatVector.fromArray(SPECIES, a, i);
        var vb = FloatVector.fromArray(SPECIES, b, i);
        acc = va.fma(vb, acc);
    }
    float sum = acc.reduceLanes(VectorOperators.ADD);
    for (; i < a.length; i++) sum += a[i] * b[i]; // tail
    return sum;
}
```

**The measurement (JMH).**

| Size | Scalar | Vector | Speedup |
| --- | --- | --- | --- |
| 1,024 floats | 0.857 µs | 0.216 µs | ~4.0x |
| 65,536 floats | 59.146 µs | 14.797 µs | ~4.0x |

Preferred species on this machine: `Species[float, 4, S_128_BIT]` — four lanes. The flat
~4x tracks the lane count almost exactly, which is what a clean multiply-reduce should do.

**Three rules for production use.**

1. `--add-modules jdk.incubator.vector` at compile *and* run time; it is an incubator, so
   the package may change between releases.
2. Keep a scalar fallback; select at startup, not with reflection in the hot path.
3. Test with a floating-point tolerance, not bit-equality — summation order changes
   (`rel err 1.85e-03` vs the scalar reference in the showcase; in-process speedup 3.80x).

***

## Part 4 — All together: the order pipeline

The integration demo is deliberately shaped like production code, not a feature parade:

- a `LazyConstant<Services>` registry (JEP 531) holds the three service simulators;
- each quote fans the three calls out in a `StructuredTaskScope` (JEP 533);
- the result must equal the sequential path, and the test asserts it.

| Mode | 4 orders, wall clock |
| --- | --- |
| sequential | ~570 ms |
| structured | ~258 ms |

This is the pattern I would take into a real codebase first: one scope per request, fork
per remote call, join before assembly, timeout in configuration, unwrap
`ExecutionException` before inspecting the cause.

***

## Part 5 — Migration playbook

1. **Upgrade the toolchain and re-run tests on the final features.** G1 (523), compact
   headers (534), PQ TLS (527), and JFR redaction (536) need zero code changes. Watch for
   tooling that parses JSSE traces or JFR recordings, and for `Unsafe`-based header access.
2. **Spike structured concurrency (533) on one endpoint.** One scope per request. Build the
   timeout path first — the wrapped-exception gotcha is the one that bites in review.
3. **Replace holder-class singletons with lazy constants (531)** and audit initializers
   that can fail; failure permanence changes retry behaviour.
4. **Adopt primitive patterns (532) in parsers and validators**, where they delete cast
   ladders and surface dead branches.
5. **Move key handling to the PEM API (538)** and delete base64 wrapper code.
6. **Vectorize (537) only the hot loops**, behind the incubator flag, with a scalar fallback
   and a tolerance-based test.
7. **Keep preview flags paired.** Compile and run must agree; every child JVM your tooling
   spawns needs `--enable-preview` too.

## Part 6 — The gotcha catalogue

1. Scope timeouts with `anySuccessfulOrThrow()` arrive as `ExecutionException` wrapping
   `CancelledByTimeoutException`.
2. `joinUntil(Instant)` is gone; timeouts live in `Configuration#withTimeout`.
3. `LazyConstant` failures are permanent, wrapped in `NoSuchElementException`; the original
   exception is named in the message, not attached as a cause.
4. `RecordingFile.readAllEvents(Path)` is static; `open(...)` + instance method is gone.
5. The JSSE debug trace is JSON-style: `"named group"` (share) vs `"named groups"`
   (supported list).
6. JDK 23+ requires explicit `annotationProcessorPaths` — JMH and friends no longer run
   implicitly.
7. Preview class files need `--enable-preview` at run time in every process that loads them.
8. `PEM(String, byte[])` takes textual content; `PEM` containers cannot be encrypted.
9. Primitive pattern case order encodes exactness; a trailing `double` case after `long` on
   a `long` component is unreachable and fails compilation.
10. Generic `anySuccessfulOrThrow()` needs an explicit type witness:
    `Joiner.<String>anySuccessfulOrThrow()`.

## Part 7 — Run it yourself

```bash
git clone https://github.com/Baneeishaque/jdk-27-demo
cd jdk-27-demo
mise install              # JDK 27 + Maven 3.10 + Node 22
./mvnw verify             # compile + 33 tests
./scripts/run-demo.sh     # every demo, printed report
./scripts/bench-vector.sh # the JMH numbers above
```

Repository tour:

- `src/main/java/io/github/baneeishaque/jdk27/` — one package per JEP, plus the order
  pipeline and the JMH benchmark.
- `docs/explainers/` — a line-by-line explainer per JEP, including the order pipeline.
- `benchmarks/results/` — the recorded reports (JVM features + JMH).
- `docs/upgrade-guide.md` — the migration checklist in longer form.

## Closing

JDK 27 is a rare release: four final features that cost nothing to adopt, four previews
that are genuinely ready for production spikes, and an incubator that has been quietly
compounding. The upgrade is cheap; the wins are measurable. Take the numbers above, run
them on your hardware, and start with structured concurrency — it is the one your services
will feel immediately.

Code, tests, benchmarks, and the full content kit:
`https://github.com/Baneeishaque/jdk-27-demo`.
