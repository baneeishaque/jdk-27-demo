package io.github.baneeishaque.jdk27.testutil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Runs throwaway child JVMs from tests and returns their combined output.
 *
 * <p>Several JDK 27 features are configured at VM startup (collector
 * selection, compact headers, flight recording, TLS debug), so their
 * behaviour can only be observed by launching a fresh VM.
 */
public final class JvmProbe {

    private JvmProbe() {
    }

    /**
     * Runs a main class in a child VM with the test classpath.
     *
     * @param jvmArgs   arguments placed before {@code -cp}
     * @param mainClass main class to launch
     * @param args      program arguments
     * @return combined stdout and stderr of the child
     */
    public static String runMain(List<String> jvmArgs, String mainClass, String... args) {
        List<String> command = new ArrayList<>();
        command.add(javaBinary());
        command.addAll(jvmArgs);
        command.add("-cp");
        command.add(classpath());
        command.add(mainClass);
        command.addAll(List.of(args));
        return run(command);
    }

    /**
     * Runs a child VM with only JVM arguments, for example {@code -version}
     * probes that need no application classes.
     *
     * @param jvmArgs arguments for the child VM
     * @return combined stdout and stderr of the child
     */
    public static String runJvm(List<String> jvmArgs) {
        List<String> command = new ArrayList<>();
        command.add(javaBinary());
        command.addAll(jvmArgs);
        return run(command);
    }

    /**
     * Returns the classpath the current test JVM is running with.
     *
     * @return surefire's recorded test classpath, falling back to {@code java.class.path}
     */
    public static String classpath() {
        return System.getProperty("surefire.test.class.path", System.getProperty("java.class.path"));
    }

    private static String javaBinary() {
        return Path.of(System.getProperty("java.home"), "bin", "java").toString();
    }

    private static String run(List<String> command) {
        try {
            Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            int exit = process.waitFor();
            if (exit != 0) {
                throw new IllegalStateException("child exited with " + exit + System.lineSeparator() + output);
            }
            return output;
        } catch (IOException failure) {
            throw new IllegalStateException("cannot run " + command, failure);
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("interrupted while running " + command, interrupted);
        }
    }
}
