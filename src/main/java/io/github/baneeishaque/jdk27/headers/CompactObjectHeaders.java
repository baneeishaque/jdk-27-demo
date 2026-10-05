package io.github.baneeishaque.jdk27.headers;

import com.sun.management.HotSpotDiagnosticMXBean;

import java.lang.management.ManagementFactory;
import java.util.function.Supplier;

/**
 * JEP 534 — Compact Object Headers by Default.
 *
 * <p>JDK 27 ships the 64-bit compact object header as the default layout on
 * x64 and AArch64: the mark word and the (22-bit compressed) class pointer
 * share one 64-bit word instead of occupying 96 bits across two words. This
 * class exposes the flag state and a per-allocation footprint measurement so
 * the difference can be measured on any machine — with
 * {@code -XX:-UseCompactObjectHeaders} as the control.
 */
public final class CompactObjectHeaders {

    private CompactObjectHeaders() {
    }

    /** A two-int value object; the legacy header costs 24 bytes, compact 16. */
    public record Point(int x, int y) {
    }

    /**
     * Reports whether compact object headers are enabled in this VM.
     *
     * @return the resolved value of the {@code UseCompactObjectHeaders} VM option
     */
    public static boolean isEnabled() {
        HotSpotDiagnosticMXBean bean = ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean.class);
        return Boolean.parseBoolean(bean.getVMOption("UseCompactObjectHeaders").getValue());
    }

    /**
     * Measures the average allocated bytes per object produced by a factory.
     *
     * <p>Uses {@code com.sun.management.ThreadMXBean#getCurrentThreadAllocatedBytes}
     * so the result is independent of heap sizing and GC timing. The first
     * factory call happens before the measurement window to exclude any
     * class-loading or TLAB warm-up cost.
     *
     * @param factory object factory under measurement
     * @param count   number of objects to allocate
     * @return average allocated bytes per object
     */
    public static long bytesPerAllocation(Supplier<Object> factory, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("count must be positive");
        }
        com.sun.management.ThreadMXBean threadBean =
                (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();
        Object[] keepAlive = new Object[count];
        factory.get();
        long before = threadBean.getCurrentThreadAllocatedBytes();
        for (int index = 0; index < count; index++) {
            keepAlive[index] = factory.get();
        }
        long after = threadBean.getCurrentThreadAllocatedBytes();
        if (keepAlive[count - 1] == null) {
            throw new AssertionError("allocation elided");
        }
        return (after - before) / count;
    }

    /**
     * Produces the demo report.
     *
     * @return human-readable summary of the flag state and a footprint sample
     */
    public static String report() {
        long pointBytes = bytesPerAllocation(() -> new Point(1, 2), 1_000_000);
        return "UseCompactObjectHeaders = " + isEnabled() + System.lineSeparator()
                + "Point(1,2) allocation: " + pointBytes + " bytes/object"
                + " (control: -XX:-UseCompactObjectHeaders)";
    }
}
