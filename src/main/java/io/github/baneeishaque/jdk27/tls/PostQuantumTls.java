package io.github.baneeishaque.jdk27.tls;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JEP 527 — Post-Quantum Hybrid Key Exchange in TLS 1.3.
 *
 * <p>JDK 27 makes the hybrid group {@code X25519MLKEM768} the most preferred
 * TLS 1.3 named group by default; {@code SecP256r1MLKEM768} and
 * {@code SecP384r1MLKEM1024} are also available. There is no public API that
 * exposes the group negotiated by a completed handshake, so this demo runs the
 * handshake in a child VM with {@code javax.net.debug=ssl:handshake} enabled
 * and reads the offered and negotiated groups from the debug trace.
 */
public final class PostQuantumTls {

    /** The first {@code "named groups": [...]} block is the client's supported list. */
    private static final Pattern SUPPORTED_GROUPS = Pattern.compile("\"named groups\": \\[([^\\]]+)\\]");

    /** Each key-share entry records the group it carries. */
    private static final Pattern NAMED_GROUP = Pattern.compile("\"named group\": ([A-Za-z0-9_]+)");

    private PostQuantumTls() {
    }

    /**
     * Runs a loopback TLS 1.3 handshake in a child VM and returns its debug trace.
     *
     * @param mode {@code "default"} for the default group list, {@code "x25519"}
     *             to pin the classic group with {@code SSLParameters#setNamedGroups}
     * @return combined stdout and stderr of the child, including the handshake trace
     * @throws IOException          when the child cannot be started
     * @throws InterruptedException when interrupted while waiting for the child
     */
    public static String handshakeTrace(String mode) throws IOException, InterruptedException {
        String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        Process process = new ProcessBuilder(
                java,
                "--enable-preview",
                "-Djavax.net.debug=ssl:handshake",
                "-cp", System.getProperty("java.class.path"),
                TlsHandshakeChild.class.getName(), mode)
                .redirectErrorStream(true)
                .start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        int exit = process.waitFor();
        if (exit != 0) {
            throw new IOException("handshake child exited with " + exit + System.lineSeparator() + output);
        }
        return output;
    }

    /**
     * Extracts the client's supported named groups, most preferred first.
     *
     * @param trace debug output of a handshake child
     * @return the client's supported group list, or an empty list when absent
     */
    public static List<String> supportedGroups(String trace) {
        Matcher matcher = SUPPORTED_GROUPS.matcher(trace);
        if (!matcher.find()) {
            return List.of();
        }
        return List.of(matcher.group(1).split(",\\s*"));
    }

    /**
     * Extracts the named groups carried by key shares in a handshake trace.
     *
     * @param trace debug output of a handshake child
     * @return distinct group names in first-seen order
     */
    public static List<String> negotiatedGroups(String trace) {
        Set<String> groups = new LinkedHashSet<>();
        Matcher matcher = NAMED_GROUP.matcher(trace);
        while (matcher.find()) {
            groups.add(matcher.group(1));
        }
        return List.copyOf(groups);
    }

    /**
     * Produces the demo report.
     *
     * @return human-readable summary of the default and pinned handshakes
     */
    public static String report() {
        try {
            String defaultTrace = handshakeTrace("default");
            List<String> defaultSupported = supportedGroups(defaultTrace);
            List<String> defaultNegotiated = negotiatedGroups(defaultTrace);
            String pinnedTrace = handshakeTrace("x25519");
            List<String> pinnedSupported = supportedGroups(pinnedTrace);
            List<String> pinnedNegotiated = negotiatedGroups(pinnedTrace);
            return "default supported groups: " + defaultSupported + System.lineSeparator()
                    + "hybrid is most preferred by default: "
                    + (!defaultSupported.isEmpty() && defaultSupported.getFirst().equals("X25519MLKEM768"))
                    + System.lineSeparator()
                    + "default negotiated groups: " + defaultNegotiated + System.lineSeparator()
                    + "pinned supported groups: " + pinnedSupported + System.lineSeparator()
                    + "pinned negotiated groups: " + pinnedNegotiated + System.lineSeparator()
                    + "hybrid group with pinned X25519: " + pinnedNegotiated.contains("X25519MLKEM768");
        } catch (IOException | InterruptedException failure) {
            if (failure instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return "TLS demo failed: " + failure;
        }
    }
}
