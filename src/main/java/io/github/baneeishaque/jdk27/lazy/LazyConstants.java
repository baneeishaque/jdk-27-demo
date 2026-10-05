package io.github.baneeishaque.jdk27.lazy;

import java.lang.LazyConstant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * JEP 531 — Lazy Constants (third preview).
 *
 * <p>A {@link LazyConstant} is a value that is computed at most once, on first
 * use, and is then eligible for the same JVM constant-folding treatment as a
 * {@code static final} field. Lazy collections compute elements on demand and
 * cache them, so expensive views can be created cheaply.
 */
public final class LazyConstants {

    private LazyConstants() {
    }

    /**
     * Creates a lazy constant backed by a supplier that counts its invocations.
     *
     * @param calls counter incremented once per supplier invocation
     * @return a lazy constant producing {@code "computed-<nanoTime>"}
     */
    public static LazyConstant<String> counting(AtomicInteger calls) {
        return LazyConstant.of(() -> {
            calls.incrementAndGet();
            return "computed";
        });
    }

    /**
     * Creates a lazy list where every index is computed on first access.
     *
     * @param size              list size
     * @param computingFunction value function per index
     * @param calls             counter incremented once per computed element
     * @return the lazy list
     */
    public static List<String> lazyList(int size, AtomicInteger calls) {
        return List.ofLazy(size, index -> {
            calls.incrementAndGet();
            return "element-" + index;
        });
    }

    /**
     * Creates a lazy set that filters its candidates on first traversal.
     *
     * @param candidates candidate elements
     * @return the lazy filtered set
     */
    public static Set<String> lazySet(Set<String> candidates) {
        return Set.ofLazy(candidates, candidate -> candidate.length() > 5);
    }

    /**
     * Creates a lazy map that computes values on first lookup.
     *
     * @param keys              the fixed key set
     * @param computingFunction value function per key
     * @param calls             counter incremented once per computed value
     * @return the lazy map
     */
    public static Map<String, String> lazyMap(Set<String> keys, AtomicInteger calls) {
        return Map.ofLazy(keys, key -> {
            calls.incrementAndGet();
            return key.toUpperCase();
        });
    }

    /**
     * Shows what happens when the initial computation throws: the failure is
     * remembered, so later accesses do not retry the supplier.
     *
     * @return first-access and second-access exception descriptions
     */
    public static String failurePermanence() {
        LazyConstant<String> failing = LazyConstant.of(() -> {
            throw new IllegalStateException("initial computation failed");
        });
        String first = describeAccess(failing);
        String second = describeAccess(failing);
        return "first access: " + first + System.lineSeparator()
                + "second access: " + second;
    }

    private static String describeAccess(LazyConstant<String> constant) {
        try {
            constant.get();
            return "no exception";
        } catch (RuntimeException failure) {
            return failure.getClass().getSimpleName() + ": " + failure.getMessage();
        }
    }

    /**
     * Produces the demo report.
     *
     * @return human-readable summary of constant and collection laziness
     */
    public static String report() {
        AtomicInteger constantCalls = new AtomicInteger();
        LazyConstant<String> constant = counting(constantCalls);
        String beforeUse = "supplier calls before first get(): " + constantCalls.get();
        constant.get();
        constant.get();
        String afterUse = "supplier calls after two get() calls: " + constantCalls.get();

        AtomicInteger listCalls = new AtomicInteger();
        List<String> list = lazyList(3, listCalls);
        String listBefore = "list elements computed before any access: " + listCalls.get();
        list.get(1);
        list.get(1);
        list.get(2);
        String listAfter = "list elements computed after get(1) x2 and get(2): " + listCalls.get();

        AtomicInteger mapCalls = new AtomicInteger();
        Map<String, String> map = lazyMap(Set.of("alpha", "beta"), mapCalls);
        map.get("alpha");
        map.get("alpha");
        String mapAfter = "map values computed after get(alpha) x2: " + mapCalls.get();

        return beforeUse + System.lineSeparator()
                + afterUse + System.lineSeparator()
                + listBefore + System.lineSeparator()
                + listAfter + System.lineSeparator()
                + "lazy set view: " + lazySet(Set.of("apple", "banana")) + System.lineSeparator()
                + mapAfter + System.lineSeparator()
                + "map: " + map + System.lineSeparator()
                + failurePermanence();
    }
}
