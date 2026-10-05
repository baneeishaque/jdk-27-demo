# Architecture

This repository is a showcase first and a library second: every JEP lives in its own small
class with a `report()` method that returns a human-readable summary, and `Main` dispatches
to those classes. That shape keeps each feature independently runnable
(`scripts/run-demo.sh tls`) and independently testable, while `Main` gives a single
"run everything" entry point.

## Module layout

```text
Main                         CLI dispatcher (LinkedHashMap of id -> report supplier)
gc/G1Default                 reads the collector decision back from platform MXBeans
headers/CompactObjectHeaders flag state + per-allocation byte measurement
jfr/JfrRedaction             spawns a child VM with a recording, then inspects it
jfr/JfrRedactionChild        the child's main class
lazy/LazyConstants           lazy constant + lazy collection semantics
patterns/PrimitivePatterns   sealed JSON hierarchy matched with primitive patterns
concurrency/StructuredConcurrency   the three scope shapes + failure propagation
orders/OrderPipeline         integration: fan-out quoting over a lazy service registry
tls/PostQuantumTls           spawns a child VM with TLS debug and parses the trace
tls/TlsHandshakeChild        the child's main class (loopback TLS 1.3 handshake)
vector/VectorApi             scalar reference + SIMD implementation, side by side
pem/PemEncoding              encode/decode round trips for the PEM API
bench/VectorDotBenchmark     JMH comparison of the two dot-product implementations
```

## The child-VM probe pattern

Four of the nine JEPs are configured **at VM startup** and cannot be observed from a JVM
that is already running:

- JEP 523 — the collector is chosen during VM initialization.
- JEP 534 — the object layout is fixed before the first object is allocated.
- JEP 536 — recording redaction is installed before application code runs.
- JEP 527 — `javax.net.debug` is read when JSSE initializes.

For those, the demo classes launch a fresh JVM with the feature flag (or debug property)
set, let it do its work, and then parse the output or artifact. The pattern is uniform:

1. The parent builds a `ProcessBuilder` for `$JAVA_HOME/bin/java`.
2. The child gets `--enable-preview` (the demo classes are preview-compiled), the test
   classpath (`surefire.test.class.path` under Maven, `java.class.path` otherwise), and the
   feature flag under test.
3. The parent captures the merged output, checks the exit code, and extracts the evidence —
   a `PrintFlagsFinal` line, a byte count, a JFR event, or a regex match on a TLS trace.

The same pattern is reused by the tests through
[`testutil/JvmProbe`](../src/test/java/io/github/baneeishaque/jdk27/testutil/JvmProbe.java),
so a test can assert on behaviour that only exists at startup — for example that
`-XX:-UseCompactObjectHeaders` really grows `Point` allocations by 8 bytes.

## The integration pipeline

`orders/OrderPipeline` is the "all features together" demo:

- Each quote fans three simulated service calls out through a `StructuredTaskScope`
  (JEP 533). One quote takes `max(40, 60, 30)` ms instead of `40 + 60 + 30` ms.
- The service registry behind those calls is a `LazyConstant` (JEP 531): its constructor
  simulates an 80 ms connection warm-up that is paid once, on first use.
- `quoteSequentially` and `quoteConcurrently` produce identical `Quote` records; the test
  suite asserts that equality, so the concurrency is provably behaviour-preserving.

## Why `report()` returns strings

The demos must be useful in three places: a terminal (`scripts/run-demo.sh`), the test
suite (assertions on the same methods the report uses), and the video/article pipeline
(quotes taken from real output). Returning a string keeps the observable behaviour in one
place instead of scattering `System.out.println` calls that tests would have to capture.

## Build configuration notes

- `pom.xml` sets `--enable-preview --release 27` for compilation and
  `--enable-preview --add-modules jdk.incubator.vector` for test execution.
- JMH's annotation processor is wired through `annotationProcessorPaths`, which JDK 23+
  requires because implicit annotation processing from the classpath was removed.
- The Vector API is an incubator module; every entry point that touches it (scripts, JMH
  forks, test child VMs) passes `--add-modules jdk.incubator.vector`.
