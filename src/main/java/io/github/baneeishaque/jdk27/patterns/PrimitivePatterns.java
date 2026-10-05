package io.github.baneeishaque.jdk27.patterns;

/**
 * JEP 532 — Primitive Types in Patterns, instanceof, and switch (fifth preview).
 *
 * <p>Primitive type patterns extend record deconstruction and switch to
 * primitive component types. A pattern such as {@code JsonNumber(int i)}
 * matches only when the underlying {@code long} value is exactly representable
 * as an {@code int}; the wider fallback patterns then take over.
 */
public final class PrimitivePatterns {

    private PrimitivePatterns() {
    }

    /** A minimal JSON value hierarchy to pattern-match over. */
    public sealed interface JsonValue {

        /** A number stored as a {@code double}, JSON's only number type. */
        record JsonNumber(double value) implements JsonValue {
        }

        /** A string value. */
        record JsonString(String value) implements JsonValue {
        }

        /** A boolean value. */
        record JsonBool(boolean value) implements JsonValue {
        }
    }

    /**
     * Describes a JSON value by matching on primitive component types.
     *
     * @param value value to describe
     * @return a description that names the exactness class of the match
     */
    public static String describe(JsonValue value) {
        return switch (value) {
            case JsonValue.JsonNumber(int i) -> "int-exact number: " + i;
            case JsonValue.JsonNumber(long l) -> "long number: " + l;
            case JsonValue.JsonNumber(double d) -> "double-rounded number: " + d;
            case JsonValue.JsonString(String s) -> "string of length " + s.length();
            case JsonValue.JsonBool(boolean b) -> "boolean: " + b;
        };
    }

    /**
     * Reports whether a JSON number fits exactly in an {@code int}.
     *
     * @param value value to test
     * @return {@code true} when the number matched the int pattern
     */
    public static boolean fitsInInt(JsonValue value) {
        return value instanceof JsonValue.JsonNumber(int i) && i == i;
    }

    /**
     * Produces the demo report.
     *
     * @return human-readable summary of exactness-driven matching
     */
    public static String report() {
        JsonValue small = new JsonValue.JsonNumber(42);
        JsonValue large = new JsonValue.JsonNumber(5_000_000_000d);
        JsonValue fractional = new JsonValue.JsonNumber(0.1);
        return describe(small) + System.lineSeparator()
                + describe(large) + System.lineSeparator()
                + describe(fractional) + System.lineSeparator()
                + "42 fits in int: " + fitsInInt(small) + System.lineSeparator()
                + "5_000_000_000 fits in int: " + fitsInInt(large);
    }
}
