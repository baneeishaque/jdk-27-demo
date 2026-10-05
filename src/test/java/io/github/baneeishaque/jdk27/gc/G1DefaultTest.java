package io.github.baneeishaque.jdk27.gc;

import io.github.baneeishaque.jdk27.testutil.JvmProbe;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JEP 523 — G1 must be the default collector regardless of machine size.
 */
class G1DefaultTest {

    @Test
    void g1IsTheDefaultCollectorInThisVm() {
        assertTrue(G1Default.isG1(), () -> "collectors: " + G1Default.collectorNames());
    }

    @Test
    void g1IsSelectedEvenOnAConstrainedMachine() {
        String output = JvmProbe.runJvm(List.of(
                "-XX:ActiveProcessorCount=1", "-XX:+PrintFlagsFinal", "-version"));
        assertTrue(output.contains("UseG1GC") && output.contains("= true"),
                () -> "expected UseG1GC=true on a single-CPU VM, got: " + flagLine(output, "UseG1GC"));
    }

    @Test
    void explicitCollectorFlagsStillWin() {
        String output = JvmProbe.runJvm(List.of(
                "-XX:+UseSerialGC", "-XX:+PrintFlagsFinal", "-version"));
        assertTrue(flagLine(output, "UseSerialGC").contains("= true"),
                () -> "expected UseSerialGC=true when requested, got: " + flagLine(output, "UseSerialGC"));
    }

    private static String flagLine(String output, String flag) {
        return output.lines()
                .filter(line -> line.contains(flag))
                .findFirst()
                .orElse("<missing " + flag + ">");
    }
}
