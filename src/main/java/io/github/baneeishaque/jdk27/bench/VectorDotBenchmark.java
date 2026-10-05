package io.github.baneeishaque.jdk27.bench;

import io.github.baneeishaque.jdk27.vector.VectorApi;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * JMH comparison of the scalar and Vector API dot products (JEP 537).
 *
 * <p>Run with {@code scripts/bench-vector.sh}. Both fork modes load the
 * incubator vector module and enable preview class files, which the JDK 27
 * preview-compiled demo classes require.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(value = 2, jvmArgsPrepend = {"--enable-preview", "--add-modules", "jdk.incubator.vector"})
public class VectorDotBenchmark {

    /** Array length under test. */
    @Param({"1024", "65536"})
    private int size;

    private float[] left;
    private float[] right;

    /** Fills both operands with deterministic pseudo-random data. */
    @Setup(Level.Trial)
    public void setup() {
        Random random = new Random(27);
        left = new float[size];
        right = new float[size];
        for (int index = 0; index < size; index++) {
            left[index] = random.nextFloat();
            right[index] = random.nextFloat();
        }
    }

    /**
     * Baseline: plain scalar loop.
     *
     * @return the dot product
     */
    @Benchmark
    public float scalarDot() {
        return VectorApi.dotScalar(left, right);
    }

    /**
     * Contender: preferred-species SIMD loop.
     *
     * @return the dot product
     */
    @Benchmark
    public float vectorDot() {
        return VectorApi.dotVector(left, right);
    }
}
