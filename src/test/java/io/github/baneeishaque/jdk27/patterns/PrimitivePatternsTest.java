package io.github.baneeishaque.jdk27.patterns;

import io.github.baneeishaque.jdk27.patterns.PrimitivePatterns.JsonValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JEP 532 — primitive patterns match only when the conversion is exact.
 */
class PrimitivePatternsTest {

    @Test
    void intExactValueMatchesIntPattern() {
        assertEquals("int-exact number: 42",
                PrimitivePatterns.describe(new JsonValue.JsonNumber(42)));
    }

    @Test
    void largeIntegralValueMatchesLongPattern() {
        assertEquals("long number: 5000000000",
                PrimitivePatterns.describe(new JsonValue.JsonNumber(5_000_000_000d)));
    }

    @Test
    void fractionalValueMatchesDoublePattern() {
        assertEquals("double-rounded number: 0.1",
                PrimitivePatterns.describe(new JsonValue.JsonNumber(0.1)));
    }

    @Test
    void stringsAndBooleansStillMatch() {
        assertEquals("string of length 3",
                PrimitivePatterns.describe(new JsonValue.JsonString("abc")));
        assertEquals("boolean: true",
                PrimitivePatterns.describe(new JsonValue.JsonBool(true)));
    }

    @Test
    void fitsInIntFollowsPatternExactness() {
        assertTrue(PrimitivePatterns.fitsInInt(new JsonValue.JsonNumber(42)));
        assertFalse(PrimitivePatterns.fitsInInt(new JsonValue.JsonNumber(5_000_000_000d)));
        assertFalse(PrimitivePatterns.fitsInInt(new JsonValue.JsonNumber(0.1)));
    }
}
