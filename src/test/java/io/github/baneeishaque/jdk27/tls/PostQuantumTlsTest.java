package io.github.baneeishaque.jdk27.tls;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JEP 527 — the hybrid group is most preferred by default and can be pinned away.
 */
class PostQuantumTlsTest {

    @Test
    void hybridGroupIsMostPreferredByDefault() throws Exception {
        String trace = PostQuantumTls.handshakeTrace("default");
        List<String> supported = PostQuantumTls.supportedGroups(trace);
        assertEquals("X25519MLKEM768", supported.getFirst());
        assertTrue(PostQuantumTls.negotiatedGroups(trace).contains("X25519MLKEM768"));
    }

    @Test
    void pinningX25519RemovesTheHybridGroup() throws Exception {
        String trace = PostQuantumTls.handshakeTrace("x25519");
        assertEquals(List.of("x25519"), PostQuantumTls.supportedGroups(trace));
        assertTrue(PostQuantumTls.negotiatedGroups(trace).contains("x25519"));
        assertFalse(PostQuantumTls.negotiatedGroups(trace).contains("X25519MLKEM768"));
    }
}
