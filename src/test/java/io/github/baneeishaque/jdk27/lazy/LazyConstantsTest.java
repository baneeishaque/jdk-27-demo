package io.github.baneeishaque.jdk27.lazy;

import org.junit.jupiter.api.Test;

import java.lang.LazyConstant;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JEP 531 — lazy constants compute at most once and remember failures.
 */
class LazyConstantsTest {

    @Test
    void supplierRunsAtMostOnce() {
        AtomicInteger calls = new AtomicInteger();
        LazyConstant<String> constant = LazyConstants.counting(calls);
        assertEquals(0, calls.get());
        assertEquals("computed", constant.get());
        assertEquals("computed", constant.get());
        assertEquals(1, calls.get());
    }

    @Test
    void listElementsAreComputedPerIndexOnFirstAccess() {
        AtomicInteger calls = new AtomicInteger();
        List<String> list = LazyConstants.lazyList(3, calls);
        assertEquals(0, calls.get());
        assertEquals("element-1", list.get(1));
        assertEquals("element-1", list.get(1));
        assertEquals("element-2", list.get(2));
        assertEquals(2, calls.get());
    }

    @Test
    void setViewFiltersOnFirstTraversal() {
        assertEquals(Set.of("banana"), LazyConstants.lazySet(Set.of("apple", "banana")));
    }

    @Test
    void mapValuesAreComputedPerKeyOnFirstLookup() {
        AtomicInteger calls = new AtomicInteger();
        Map<String, String> map = LazyConstants.lazyMap(Set.of("alpha", "beta"), calls);
        assertEquals("ALPHA", map.get("alpha"));
        assertEquals("ALPHA", map.get("alpha"));
        assertEquals(1, calls.get());
        assertEquals("BETA", map.get("beta"));
        assertEquals(2, calls.get());
    }

    @Test
    void failureIsRememberedAcrossAccesses() {
        LazyConstant<String> failing = LazyConstant.of(() -> {
            throw new IllegalStateException("initial computation failed");
        });
        NoSuchElementException first = assertThrows(NoSuchElementException.class, failing::get);
        NoSuchElementException second = assertThrows(NoSuchElementException.class, failing::get);
        assertTrue(first.getMessage().contains("IllegalStateException"), first.getMessage());
        assertTrue(second.getMessage().contains("IllegalStateException"), second.getMessage());
    }

    @Test
    void nullResultsAreRejected() {
        LazyConstant<String> nullConstant = LazyConstant.of(() -> null);
        NoSuchElementException failure = assertThrows(NoSuchElementException.class, nullConstant::get);
        assertTrue(failure.getMessage().contains("NullPointerException"));
    }
}
