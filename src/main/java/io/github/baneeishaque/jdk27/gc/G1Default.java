package io.github.baneeishaque.jdk27.gc;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * JEP 523 — Make G1 the Default Garbage Collector in All Environments.
 *
 * <p>Before JDK 27 the VM picked Serial on constrained machines (fewer than
 * two processors or less than ~1.8 GB of memory) and G1 elsewhere. JDK 27
 * always selects G1 unless the command line says otherwise. This class reads
 * the decision back from the platform MXBeans so tests can assert it.
 */
public final class G1Default {

    private G1Default() {
    }

    /**
     * Returns the names of the garbage collectors active in this VM.
     *
     * @return collector bean names, for example {@code [G1 Young Generation, G1 Old Generation]}
     */
    public static List<String> collectorNames() {
        return ManagementFactory.getGarbageCollectorMXBeans().stream()
                .map(GarbageCollectorMXBean::getName)
                .toList();
    }

    /**
     * Reports whether the default collector selection landed on G1.
     *
     * @return {@code true} when a G1 collector bean is present
     */
    public static boolean isG1() {
        return collectorNames().stream().anyMatch(name -> name.startsWith("G1"));
    }

    /**
     * Produces the demo report.
     *
     * @return human-readable summary of the active collectors
     */
    public static String report() {
        return "active collectors: " + collectorNames() + System.lineSeparator()
                + "G1 selected by default: " + isG1();
    }
}
