package io.github.baneeishaque.jdk27.pem;

import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.PrivateKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JEP 538 — PEM round trips for public, private, and encrypted private keys.
 */
class PemEncodingTest {

    @Test
    void publicKeyRoundTrip() throws Exception {
        KeyPair keyPair = PemEncoding.generateKeyPair();
        String pem = java.security.PEMEncoder.of().encodeToString(keyPair.getPublic());
        assertEquals("-----BEGIN PUBLIC KEY-----", PemEncoding.header(pem));
        assertEquals(keyPair.getPublic(), PemEncoding.decodePublic(pem));
    }

    @Test
    void privateKeyRoundTrip() throws Exception {
        KeyPair keyPair = PemEncoding.generateKeyPair();
        String pem = PemEncoding.encodePrivate(keyPair.getPrivate());
        assertEquals("-----BEGIN PRIVATE KEY-----", PemEncoding.header(pem));
        assertEquals(keyPair.getPrivate(), PemEncoding.decodePrivate(pem));
    }

    @Test
    void encryptedPrivateKeyRoundTrip() throws Exception {
        KeyPair keyPair = PemEncoding.generateKeyPair();
        String pem = PemEncoding.encodeEncrypted(keyPair.getPrivate(), PemEncoding.PASSWORD);
        assertEquals("-----BEGIN ENCRYPTED PRIVATE KEY-----", PemEncoding.header(pem));
        PrivateKey decoded = PemEncoding.decodeEncrypted(pem, PemEncoding.PASSWORD);
        assertEquals(keyPair.getPrivate(), decoded);
        assertFalse(pem.contains("PRIVATE KEY-----\nM"),
                "encrypted body must not be a readable PKCS#8 payload");
    }

    @Test
    void wrongPasswordIsRejected() throws Exception {
        KeyPair keyPair = PemEncoding.generateKeyPair();
        String pem = PemEncoding.encodeEncrypted(keyPair.getPrivate(), PemEncoding.PASSWORD);
        try {
            PemEncoding.decodeEncrypted(pem, "wrong-password".toCharArray());
            assertTrue(false, "decryption with the wrong password must fail");
        } catch (RuntimeException expected) {
            assertTrue(expected.getMessage() == null || !expected.getMessage().isEmpty());
        }
    }
}
