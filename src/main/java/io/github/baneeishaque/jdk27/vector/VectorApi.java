package io.github.baneeishaque.jdk27.vector;

import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

import java.util.Random;

/**
 * JEP 537 — Vector API (twelfth incubator).
 *
 * <p>The Vector API expresses data-parallel computation so the JIT can compile
 * it to the widest SIMD instructions available on the machine. This demo
 * computes a dot product with a scalar loop and with {@link FloatVector}, then
 * compares correctness and throughput.
 */
public final class VectorApi {

    private VectorApi() {
    }

    /**
     * Computes the dot product of two arrays with a plain scalar loop.
     *
     * @param left  first operand
     * @param right second operand
     * @return the dot product
     */
    public static float dotScalar(float[] left, float[] right) {
        float sum = 0f;
        for (int index = 0; index < left.length; index++) {
            sum += left[index] * right[index];
        }
        return sum;
    }

    /**
     * Computes the dot product with the preferred SIMD species.
     *
     * @param left  first operand
     * @param right second operand
     * @return the dot product
     */
    public static float dotVector(float[] left, float[] right) {
        VectorSpecies<Float> species = FloatVector.SPECIES_PREFERRED;
        int laneCount = species.length();
        float sum = 0f;
        int index = 0;
        for (; index + laneCount <= left.length; index += laneCount) {
            FloatVector leftVector = FloatVector.fromArray(species, left, index);
            FloatVector rightVector = FloatVector.fromArray(species, right, index);
            sum += leftVector.mul(rightVector).reduceLanes(VectorOperators.ADD);
        }
        for (; index < left.length; index++) {
            sum += left[index] * right[index];
        }
        return sum;
    }

    /**
     * Produces the demo report with a small measured comparison.
     *
     * @return human-readable summary of species and throughput
     */
    public static String report() {
        VectorSpecies<Float> preferred = FloatVector.SPECIES_PREFERRED;
        int length = 4_000_000;
        float[] left = new float[length];
        float[] right = new float[length];
        Random random = new Random(27);
        for (int index = 0; index < length; index++) {
            left[index] = random.nextFloat();
            right[index] = random.nextFloat();
        }

        float scalarResult = 0f;
        float vectorResult = 0f;
        for (int round = 0; round < 5; round++) {
            scalarResult = dotScalar(left, right);
            vectorResult = dotVector(left, right);
        }
        int iterations = 20;
        long scalarStart = System.nanoTime();
        for (int round = 0; round < iterations; round++) {
            scalarResult = dotScalar(left, right);
        }
        long scalarNanos = (System.nanoTime() - scalarStart) / iterations;
        long vectorStart = System.nanoTime();
        for (int round = 0; round < iterations; round++) {
            vectorResult = dotVector(left, right);
        }
        long vectorNanos = (System.nanoTime() - vectorStart) / iterations;

        double relativeError = Math.abs(scalarResult - vectorResult) / Math.abs(scalarResult);
        return "preferred species: " + preferred + " (" + preferred.length() + " lanes)" + System.lineSeparator()
                + "max species: " + FloatVector.SPECIES_MAX.length() + " lanes" + System.lineSeparator()
                + "scalar: " + scalarNanos / 1_000 + " us/op" + System.lineSeparator()
                + "vector: " + vectorNanos / 1_000 + " us/op" + System.lineSeparator()
                + "speedup: " + String.format("%.2fx", (double) scalarNanos / vectorNanos) + System.lineSeparator()
                + "results agree (relative error " + String.format("%.2e", relativeError) + "): "
                + (relativeError < 1e-2);
    }
}
