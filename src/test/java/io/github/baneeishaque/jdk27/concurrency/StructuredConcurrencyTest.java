package io.github.baneeishaque.jdk27.concurrency;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JEP 533 — the three canonical scope shapes and failure propagation.
 */
class StructuredConcurrencyTest {

    @Test
    void fanOutJoinsBothSubtasks() throws Exception {
        String result = StructuredConcurrency.fanOut();
        assertTrue(result.contains("profile (80ms)"), result);
        assertTrue(result.contains("orders (120ms)"), result);
    }

    @Test
    void racingKeepsFirstSuccessfulResult() throws Exception {
        assertEquals("fast (150ms)", StructuredConcurrency.firstSuccess());
    }

    @Test
    void timeoutCancelsTheScope() {
        assertEquals("scope cancelled after 100ms", StructuredConcurrency.timeout());
    }

    @Test
    void failedSubtaskCancelsSiblingsAndPropagates() {
        String result = StructuredConcurrency.failurePropagation();
        assertTrue(result.contains("payment declined"), result);
        assertTrue(result.contains("siblings cancelled"), result);
    }
}
