package io.github.baneeishaque.jdk27.jfr;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * JEP 536 — JFR In-Process Data Redaction.
 *
 * <p>JDK 27 redacts sensitive command-line arguments, environment-variable
 * values, and system-property values inside the JVM before a Flight Recording
 * reaches disk, a temp directory, or a remote stream. Matches become the
 * literal string {@code [REDACTED]}; the filter lists are configurable through
 * {@code -XX:FlightRecorderOptions}.
 *
 * <p>Because redaction is configured at JVM startup, the demo launches a
 * child VM that starts a recording immediately, then inspects the recording
 * with the unchanged {@code jdk.jfr.consumer} API.
 */
public final class JfrRedaction {

    /** The secret planted in the child's system property and environment. */
    public static final String SECRET = "hunter2-super-secret";

    private JfrRedaction() {
    }

    /**
     * Records a short-lived child VM that carries a secret on its command line
     * and in its environment, then returns the recording file.
     *
     * @param directory directory to write the recording into
     * @return path of the produced {@code .jfr} file
     * @throws IOException          when the child cannot be started or the file is missing
     * @throws InterruptedException when the calling thread is interrupted while waiting
     */
    public static Path recordChild(Path directory) throws IOException, InterruptedException {
        Files.createDirectories(directory);
        Path recording = directory.resolve("redaction-demo.jfr");
        Files.deleteIfExists(recording);
        String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        Process process = new ProcessBuilder(
                java,
                "--enable-preview",
                "-XX:StartFlightRecording:filename=" + recording,
                "-Ddb.password=" + SECRET,
                "-cp", System.getProperty("java.class.path"),
                JfrRedactionChild.class.getName())
                .redirectErrorStream(true)
                .start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        int exit = process.waitFor();
        if (exit != 0) {
            throw new IOException("child exited with " + exit + System.lineSeparator() + output);
        }
        if (!Files.exists(recording)) {
            throw new IOException("no recording produced at " + recording);
        }
        return recording;
    }

    /**
     * Reads the values JFR captured for a system property from a recording.
     *
     * @param recording  recording file to read
     * @param property   system-property key to look up
     * @return every recorded value for that key (normally one)
     * @throws IOException when the recording cannot be read
     */
    public static List<String> recordedSystemProperty(Path recording, String property) throws IOException {
        return jdk.jfr.consumer.RecordingFile.readAllEvents(recording).stream()
                .filter(event -> event.getEventType().getName().equals("jdk.InitialSystemProperty"))
                .filter(event -> property.equals(event.getString("key")))
                .map(event -> event.getString("value"))
                .toList();
    }

    /**
     * Produces the demo report by running the child and inspecting its recording.
     *
     * @return human-readable summary of what the recording contains
     */
    public static String report() {
        try {
            Path recording = recordChild(Path.of("target", "jfr-demo"));
            List<String> values = recordedSystemProperty(recording, "db.password");
            return "recording: " + recording + System.lineSeparator()
                    + "jdk.InitialSystemProperty[db.password] = " + values + System.lineSeparator()
                    + "raw secret present anywhere: " + containsRawSecret(recording);
        } catch (IOException | InterruptedException failure) {
            throw new IllegalStateException("JFR redaction demo failed", failure);
        }
    }

    /**
     * Checks whether the raw secret leaked into any recorded event.
     *
     * @param recording recording file to scan
     * @return {@code true} when the secret string appears in the recording
     * @throws IOException when the recording cannot be read
     */
    public static boolean containsRawSecret(Path recording) throws IOException {
        return jdk.jfr.consumer.RecordingFile.readAllEvents(recording).stream()
                .anyMatch(event -> event.toString().contains(SECRET));
    }
}
