# JDK 27: all nine JEPs, measured

JDK 27 shipped on 2026-09-15 with nine JEPs: four final features that change defaults under
your feet, four previews that reshape how you write concurrency, data handling, and crypto
code, and one incubator that keeps getting faster. Release notes tell you *what* changed.
They do not tell you what it measures on a real machine, or where the sharp edges are.

So I built a showcase that does: every JEP as runnable code with a test that proves the
claim, and a benchmark that puts a number on it. Everything below was measured on Apple
Silicon with OpenJDK 27 (`27+35`).

## The nine JEPs

| JEP | Feature | Status |
| --- | --- | --- |
| 523 | G1 becomes the default collector in all environments | Final |
| 527 | Post-quantum hybrid key exchange in TLS 1.3 | Final |
| 531 | Lazy constants | Third preview |
| 532 | Primitive types in patterns, `instanceof`, and `switch` | Fifth preview |
| 533 | Structured concurrency | Seventh preview |
| 534 | Compact object headers | Final |
| 536 | JFR in-process data redaction | Final |
| 537 | Vector API | Twelfth incubator |
| 538 | PEM encodings of cryptographic objects | Third preview |

## Four wins you get for free

**G1 everywhere (JEP 523).** The old ergonomic selection gave Serial GC to small machines —
fewer than two CPUs or under ~1.8 GB of memory. In containers, a CPU-limit change could
silently change your collector. JDK 27 always picks G1 unless you say otherwise. Verified:

```text
$ java -XX:ActiveProcessorCount=1 -XX:+PrintFlagsFinal -version | grep UseG1GC
bool UseG1GC = true {product} {ergonomic}
```

The `{ergonomic}` tag is the point: nothing forced it. If you carried `-XX:+UseG1GC` as a
defensive flag, you can drop it.

**Compact object headers (JEP 534).** The object header drops from 96 bits to 64 bits on
x64 and AArch64. Measured per allocation for a two-int record:

| Layout | bytes per `Point(1,2)` |
| --- | --- |
| compact (default) | 16 |
| `-XX:-UseCompactObjectHeaders` | 24 |

That is 8 bytes per object, straight off every live set. The measurement uses
`ThreadMXBean#getCurrentThreadAllocatedBytes`, so it is independent of GC timing — and the
test asserts the delta, not a hard-coded number.

**Post-quantum TLS (JEP 527).** `X25519MLKEM768` is now the most preferred TLS 1.3 group:

```text
default supported groups: [X25519MLKEM768, x25519, secp256r1, secp384r1, secp521r1, ...]
hybrid is most preferred by default: true
```

No configuration needed. JDK 27 clients offer post-quantum protection to every TLS 1.3
server and fall back automatically. One detail from the trace: the client sends *two* key
shares by default (hybrid + classic X25519) so servers that ignore the hybrid entry still
get a fast handshake without a retry.

**JFR redaction (JEP 536).** Secrets are redacted in-process, before a recording reaches
disk. A child VM started with `-Ddb.password=hunter2-super-secret` records:

```text
jdk.InitialSystemProperty[db.password] = [[REDACTED]]
raw secret present anywhere: false
```

## The previews worth spiking

**Structured concurrency (JEP 533)** is the headline. The JDK 27 shape:

```java
try (var scope = StructuredTaskScope.open()) {
    Subtask<String> profile = scope.fork(() -> fetch("profile", 80));
    Subtask<String> orders  = scope.fork(() -> fetch("orders", 120));
    scope.join();
    return profile.get() + ", " + orders.get();
}
```

The scope is the cancellation boundary: when one subtask fails, siblings are cancelled and
`join()` throws with the original cause. Racing (`Joiner.anySuccessfulOrThrow()`), scoped
timeouts (`configuration.withTimeout(...)`), and failure propagation all behave the way you
wish `ExecutorService` did.

**Lazy constants (JEP 531)** turn the holder-class idiom into an API:

```java
LazyConstant<Services> SERVICES = LazyConstant.of(Services::new);
```

At-most-once computation, and — the part release notes bury — **failure is permanent**:
if the initial computation throws, every later access throws a `NoSuchElementException`
naming the original failure. Retryable work must be retried *inside* the supplier.

**Primitive patterns (JEP 532)** match only when the conversion is exact:

```java
case JsonValue.JsonNumber(int i) -> "int-exact number: " + i;
case JsonValue.JsonNumber(long l) -> "long number: " + l;
case JsonValue.JsonNumber(double d) -> "double-rounded number: " + d;
```

`42.0` takes the first case, `5_000_000_000.0` the second, `0.1` the third. No silent
truncation, no manual range checks.

**PEM encodings (JEP 538)** finally make key material a one-liner:

```java
String pem = PEMEncoder.of().withEncryption(password).encodeToString(privateKey);
PrivateKey key = PEMDecoder.of().withDecryption(password).decode(pem, PrivateKey.class);
```

Public, private, and encrypted round trips all verified with `Key#equals`.

## The incubator keeps paying: Vector API (JEP 537)

Twelfth incubator round, and the numbers are the same story as always — SIMD works:

| Size | Scalar | Vector | Speedup |
| --- | --- | --- | --- |
| 1,024 floats | 0.857 µs | 0.216 µs | ~4.0x |
| 65,536 floats | 59.146 µs | 14.797 µs | ~4.0x |

JMH, 2 forks × (3 warmup + 3 measurement) seconds, `SPECIES_PREFERRED` on 128-bit NEON.
The flat ~4x is exactly the lane count; dot product is a clean multiply-reduce. Remember
`--add-modules jdk.incubator.vector` at compile *and* run time, and that summation order
changes — results agree within floating-point tolerance, not bit-for-bit.

## All together: a pipeline, not a feature list

The showcase's integration demo quotes an order book through three simulated service calls
(price, stock, shipping) behind a lazy service registry:

| Mode | 4 orders, wall clock |
| --- | --- |
| sequential service calls | ~570 ms |
| structured fan-out | ~258 ms |

One quote costs `40 + 60 + 30 = 130 ms` sequentially but `max(40, 60, 30) = 60 ms` when
the three calls fan out inside a scope — ~2.2x end to end. The test suite asserts that both
modes produce **identical quotes**, so the concurrency is provably behaviour-preserving.

## The gotchas the release notes don't mention

1. **Timeouts are wrapped.** With `anySuccessfulOrThrow()`, a scope timeout arrives as
   `ExecutionException` whose *cause* is `CancelledByTimeoutException`. Catching the latter
   directly never fires.
2. **`joinUntil(Instant)` is gone.** Timeouts moved into `Configuration#withTimeout`.
3. **`LazyConstant` failures are permanent** and wrapped in `NoSuchElementException`; the
   original exception is named in the message but not attached as a cause.
4. **JFR's `RecordingFile` API changed shape.** `readAllEvents` is now a static method
   taking a `Path`; the `open(...)` + instance-method pair no longer exists.
5. **The JSSE debug trace is JSON-style.** Groups appear as `"named group": X25519MLKEM768`
   (key shares) and `"named groups": [...]` (supported list).
6. **JDK 23+ killed implicit annotation processing.** JMH (or MapStruct, or any processor)
   needs explicit `annotationProcessorPaths` in Maven.
7. **Preview class files need the flag at run time too** — including every child JVM your
   tooling launches. A missing `--enable-preview` in a spawned process is a load-time
   failure, not a warning.

## Run it yourself

```bash
git clone https://github.com/Baneeishaque/jdk-27-demo
cd jdk-27-demo
mise install              # JDK 27 + Maven 3.10 + Node 22
./mvnw verify             # compile + 33 tests
./scripts/run-demo.sh     # every demo, printed report
./scripts/bench-vector.sh # the JMH numbers above
```

The repository ships line-by-line explainers for every JEP under `docs/explainers/`, an
upgrade guide, and the recorded benchmark reports under `benchmarks/`.

## What I would actually do in a migration

1. **Upgrade and re-run your tests on the final features first.** G1, compact headers,
   PQ TLS, and JFR redaction need zero code changes. Watch for tooling that parses JFR
   recordings or pins TLS groups.
2. **Spike structured concurrency on one endpoint.** One scope per request, `fork` per
   remote call. The mechanical migration is small; the cancellation semantics are the win.
3. **Replace holder-class singletons with lazy constants** and audit initializers that can
   fail — failure permanence changes retry behaviour.
4. **Vectorize only the hot loops**, behind the incubator flag, with a scalar fallback and a
   tolerance-based test.
5. **Move key handling to the PEM API** and delete the base64 wrapper code.

All nine JEPs, with code, tests, benchmarks, and the full content kit (including a
37-language video walkthrough), live in the repository:
`https://github.com/Baneeishaque/jdk-27-demo`.
