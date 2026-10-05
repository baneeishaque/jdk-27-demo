package io.github.baneeishaque.jdk27.headers;

import io.github.baneeishaque.jdk27.testutil.JvmProbe;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JEP 534 — compact object headers on by default, and measurably smaller.
 */
class CompactObjectHeadersTest {

    private static final Pattern ALLOCATION = Pattern.compile("allocation: (\\d+) bytes/object");

    @Test
    void compactHeadersAreEnabledByDefault() {
        assertTrue(CompactObjectHeaders.isEnabled());
    }

    @Test
    void compactHeadersShrinkPerObjectAllocation() {
        long withCompact = CompactObjectHeaders.bytesPerAllocation(
                () -> new CompactObjectHeaders.Point(1, 2), 1_000_000);
        assertTrue(withCompact <= 16, () -> "expected <= 16 bytes/Point, got " + withCompact);

        String output = JvmProbe.runMain(
                List.of("--enable-preview", "--add-modules", "jdk.incubator.vector",
                        "-XX:-UseCompactObjectHeaders"),
                "io.github.baneeishaque.jdk27.Main", "headers");
        Matcher matcher = ALLOCATION.matcher(output);
        assertTrue(matcher.find(), () -> "no allocation line in: " + output);
        long withoutCompact = Long.parseLong(matcher.group(1));
        assertTrue(withoutCompact >= withCompact + 8,
                () -> "legacy layout " + withoutCompact + " vs compact " + withCompact);
    }
}
