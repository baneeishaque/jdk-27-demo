# Testing

`./mvnw test` runs 33 JUnit 5 tests, one class per demo. The suite is designed so that
every claim the article and video make is backed by an assertion, including claims about
behaviour that only exists at VM startup.

## What each test class proves

| Test class | Tests | Proves |
| --- | --- | --- |
| `G1DefaultTest` | 3 | G1 is active in the test VM; `-XX:ActiveProcessorCount=1` still selects G1; explicit collector flags still win |
| `CompactObjectHeadersTest` | 2 | Compact headers are on by default; disabling them grows `Point` allocations by at least 8 bytes |
| `JfrRedactionTest` | 2 | `db.password` is recorded as `[REDACTED]`; the raw secret appears nowhere in the recording |
| `LazyConstantsTest` | 6 | At-most-once computation; per-index lazy lists; lazy set filter; per-key lazy map; failure permanence; null rejection |
| `PrimitivePatternsTest` | 5 | `int`/`long`/`double` pattern selection follows conversion exactness |
| `StructuredConcurrencyTest` | 4 | Fan-out joins both subtasks; racing keeps the first success; timeout cancels the scope; one failure cancels siblings and propagates |
| `OrderPipelineTest` | 2 | Quote arithmetic is consistent; sequential and structured modes produce identical quotes |
| `PostQuantumTlsTest` | 2 | `X25519MLKEM768` is the most preferred group by default; pinning `x25519` removes it |
| `VectorApiTest` | 3 | Known dot product; SIMD matches scalar on a large non-multiple array; empty arrays are zero |
| `PemEncodingTest` | 4 | Public/private/encrypted round trips; wrong password is rejected |

## The child-JVM probe

Tests that assert startup-configured behaviour go through
[`JvmProbe`](../src/test/java/io/github/baneeishaque/jdk27/testutil/JvmProbe.java), which
launches a child VM with the test classpath (`surefire.test.class.path`), merged output,
and an exit-code check. Examples:

- `G1DefaultTest` runs `-XX:ActiveProcessorCount=1 -XX:+PrintFlagsFinal -version` and greps
  the `UseG1GC` line — this is the *only* honest way to test an ergonomic default, because
  the test VM's own collector is already fixed.
- `CompactObjectHeadersTest` runs the `headers` demo in a child with
  `-XX:-UseCompactObjectHeaders` and parses the allocation line, then compares it with the
  in-process measurement.

Child VMs are launched with `--enable-preview` because every demo class is preview-compiled
(class files carry the preview minor version, which the JVM refuses to load otherwise).

## Why the allocation measurement uses thread-allocated bytes

`CompactObjectHeaders.bytesPerAllocation` reads
`com.sun.management.ThreadMXBean#getCurrentThreadAllocatedBytes` around a tight allocation
loop, with one warm-up call excluded. Compared to `Runtime` heap deltas this is immune to
GC timing, TLAB refills, and heap sizing; compared to adding a JOL dependency it keeps the
build dependency-free. The child-VM control run supplies the "without compact headers"
number so the test asserts a delta rather than a magic constant.

## Concurrency tests and flakiness

The structured-concurrency tests assert on *outcomes* (which result won, what the timeout
message is, which failure propagated) and not on wall-clock thresholds, so they are stable
on loaded machines. The only timing-sensitive numbers live in
`scripts/bench-jvm-features.sh`, which is explicitly documented as single-run observation,
while the statistically rigorous numbers come from JMH.

## Running a subset

```bash
./mvnw test -Dtest=PostQuantumTlsTest    # one class
./mvnw test -Dtest='LazyConstantsTest#nullResultsAreRejected'   # one method
```
