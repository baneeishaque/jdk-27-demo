package io.github.baneeishaque.jdk27.vector;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * JEP 537 — the vector path must agree with the scalar reference.
 */
class VectorApiTest {

    @Test
    void smallKnownDotProduct() {
        float[] left = {1, 2, 3};
        float[] right = {4, 5, 6};
        assertEquals(32f, VectorApi.dotScalar(left, right));
        assertEquals(32f, VectorApi.dotVector(left, right));
    }

    @Test
    void vectorPathMatchesScalarOnLargeArrays() {
        Random random = new Random(27);
        float[] left = new float[4_000_003];
        float[] right = new float[4_000_003];
        for (int index = 0; index < left.length; index++) {
            left[index] = random.nextFloat();
            right[index] = random.nextFloat();
        }
        float scalar = VectorApi.dotScalar(left, right);
        float vector = VectorApi.dotVector(left, right);
        assertEquals(scalar, vector, Math.abs(scalar) * 1e-2);
    }

    @Test
    void emptyArraysAreZero() {
        assertEquals(0f, VectorApi.dotVector(new float[0], new float[0]));
    }
}
