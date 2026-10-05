# JDK 27's free performance wins, measured

Three of JDK 27's nine JEPs are about performance, and two of them cost you nothing: G1 is
now the default collector in every environment (JEP 523) and object headers shrank by 25%
(JEP 534). The third — the Vector API (JEP 537) — still needs code changes, but the payoff
is the largest number in the release.

All measurements below ran on Apple Silicon with OpenJDK 27 (`27+35`); the harness and raw
reports are in the repository.

## JEP 523: G1 everywhere, even where it was never selected

Before JDK 27, ergonomic GC selection had a hidden cliff: machines with fewer than two
CPUs or under roughly 1.8 GB of heap got the Serial collector instead. In containerized
deployments, a CPU-limit tweak could silently change your collector between staging and
production.

JDK 27 removes the cliff. G1 is always the ergonomic default:

```text
$ java -XX:ActiveProcessorCount=1 -XX:+PrintFlagsFinal -version | grep UseG1GC
bool UseG1GC = true {product} {ergonomic}
```

The `{ergonomic}` tag is the evidence: a one-CPU machine, no flags, and G1 was chosen.

**What it means for you.** Upgrade and re-run your latency tests — you are very likely
moving from Serial to G1 without asking. If you pinned `-XX:+UseG1GC` defensively, delete
the flag. If you deliberately run Serial on tiny instances, keep your explicit choice; it
still wins over the ergonomic default.

## JEP 534: compact object headers, 8 bytes per object

The object header — mark word plus class pointer — went from 96 bits to 64 bits. Every
object gets smaller, and the savings scale with object count, which is exactly where
managed runtimes hurt.

**How the showcase measures it.** Absolute header size is an implementation detail; what
matters is allocation cost per object. The demo uses
`com.sun.management.ThreadMXBean#getCurrentThreadAllocatedBytes` around a single
allocation:

```java
long before = allocatedBytes();
Point p = new Point(1, 2); // record Point(int x, int y)
long after = allocatedBytes();
// after - before == 16 on JDK 27 (24 with -XX:-UseCompactObjectHeaders)
```

| Layout | bytes per `Point(1,2)` |
| --- | --- |
| compact (default) | 16 |
| `-XX:-UseCompactObjectHeaders` | 24 |

Eight bytes, every object, no code changes. On a heap with millions of small objects, that
is a double-digit percentage of live set.

**The engineering detail worth copying.** The test asserts the *delta* between the two
layouts, not a hard-coded 16 or 24 — alignment effects differ across platforms, and a
brittle assertion would be deleted by the first person it inconvenienced.

**One audit item.** Code that reaches into header internals via `sun.misc.Unsafe` (some
serialization frameworks and off-heap libraries) should be verified against the new
layout. Pure Java code needs nothing.

## JEP 537: the Vector API, still incubating, still ~4x

Twelfth incubator round. The API is stable enough to build on, the flag requirement is the
same, and the numbers have not changed: SIMD works.

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

**JMH results** (2 forks, 3 × 1 s warmup, 3 × 1 s measurement):

| Size | Scalar | Vector | Speedup |
| --- | --- | --- | --- |
| 1,024 floats | 0.857 µs | 0.216 µs | ~4.0x |
| 65,536 floats | 59.146 µs | 14.797 µs | ~4.0x |

Preferred species on this machine: `Species[float, 4, S_128_BIT]` — four lanes. The flat
~4x tracks the lane count almost exactly, which is what a clean multiply-reduce should do.
No super-linear surprises, no size-dependent cliffs.

**Production rules.**

1. `--add-modules jdk.incubator.vector` at compile *and* run time.
2. Keep a scalar fallback selected at startup — never reflect into the hot path.
3. Test with a tolerance: summation order changes, so results agree within floating-point
   tolerance, not bit-for-bit (`rel err 1.85e-03` vs scalar in the showcase; in-process
   speedup 3.80x).

## The combined upgrade checklist

1. Upgrade the toolchain; re-run tests. G1 and compact headers need zero changes.
2. Measure your live-set and allocation rate before/after — compact headers show up in
   allocation profiles immediately.
3. Watch for collector-behaviour changes in small containers; that is G1 arriving, not a
   regression.
4. Vectorize one hot loop behind the incubator flag, with the scalar fallback and a
   tolerance test.
5. Audit `Unsafe`-based header access.

## Run it

```bash
git clone https://github.com/Baneeishaque/jdk-27-demo
cd jdk-27-demo
./mvnw verify
./scripts/bench-jvm-features.sh  # G1 + compact headers evidence
./scripts/bench-vector.sh       # the JMH numbers above
```

Raw reports ship in `benchmarks/results/`. This article is part two of a three-part JDK 27
series; part one covers structured concurrency and part three covers the security features.
