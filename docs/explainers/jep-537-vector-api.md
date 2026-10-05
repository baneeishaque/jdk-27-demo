# Explainer: JEP 537 — Vector API (twelfth incubator)

## What changed

The Vector API expresses data-parallel computation in Java so the JIT can compile it to
the widest SIMD instructions the CPU offers — NEON on Apple Silicon and modern ARM, AVX on
x64. JDK 27 is the twelfth incubator round: the API continues to incubate while the
supporting `jdk.incubator.vector` module and `FloatVector`/`IntVector` species abstraction
remain stable in shape.

## The demo

[`VectorApi`](../../src/main/java/io/github/baneeishaque/jdk27/vector/VectorApi.java) keeps
the scalar reference and the SIMD implementation side by side, both in production code, so
tests and benchmarks compare the same two functions users read:

```java
public static float dotScalar(float[] left, float[] right) {          // :29
    float sum = 0f;
    for (int index = 0; index < left.length; index++) {
        sum += left[index] * right[index];
    }
    return sum;
}

public static float dotVector(float[] left, float[] right) {          // :44
    VectorSpecies<Float> species = FloatVector.SPECIES_PREFERRED;
    int laneCount = species.length();
    float sum = 0f;
    int index = 0;
    for (; index + laneCount <= left.length; index += laneCount) {
        FloatVector leftVector = FloatVector.fromArray(species, left, index);
        FloatVector rightVector = FloatVector.fromArray(species, right, index);
        sum += leftVector.mul(rightVector).reduceLanes(VectorOperators.ADD);
    }
    for (; index < left.length; index++) {                            // tail
        sum += left[index] * right[index];
    }
    return sum;
}
```

Design points worth copying:

- `SPECIES_PREFERRED` adapts to the machine (4 lanes of float on 128-bit NEON); hard-coding
  a species would leave performance on the table or crash on smaller vectors.
- The tail loop handles lengths that are not a multiple of the lane count — the test suite
  uses 4,000,003 elements specifically to exercise it.
- `reduceLanes(ADD)` accumulates per vector; the summation order differs from the scalar
  loop, so results agree within floating-point tolerance, not bit-for-bit. The test asserts
  a 1% relative bound; the demo reports the actual error (~2e-3 for 4M random floats).

## What we measured

JMH, average time, 2 forks × (3 warmup + 3 measurement) seconds:

| Size | Scalar | Vector | Speedup |
| --- | --- | --- | --- |
| 1,024 floats | 0.857 µs | 0.216 µs | ~4.0x |
| 65,536 floats | 59.146 µs | 14.797 µs | ~4.0x |

The flat ~4x tracks the 4-lane width on this machine: dot product is a clean
multiply-reduce, so speedup is essentially the lane count.

## Notes for practitioners

- Incubator modules require `--add-modules jdk.incubator.vector` at compile **and** run
  time; the `WARNING: Using incubator modules` line on stderr is expected.
- Vectorize loops that are actually hot; for cold code the scalar version is smaller and
  just as fast in practice.
- Floating-point reductions change summation order — if you need bit-identical results
  across code paths, keep the scalar path or compensate.
- JMH setup is in [`bench/VectorDotBenchmark`](../../src/main/java/io/github/baneeishaque/jdk27/bench/VectorDotBenchmark.java);
  run it with `scripts/bench-vector.sh`.
