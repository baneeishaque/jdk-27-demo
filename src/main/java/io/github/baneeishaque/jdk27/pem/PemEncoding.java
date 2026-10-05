package io.github.baneeishaque.jdk27.pem;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PEMDecoder;
import java.security.PEMEncoder;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * JEP 538 — PEM Encodings of Cryptographic Objects (third preview).
 *
 * <p>{@link PEMEncoder} and {@link PEMDecoder} read and write the PEM textual
 * format directly: keys, certificates, CSRs, CRLs, and PKCS#8 encrypted
 * private keys. The example below round-trips an EC key pair, including a
 * password-encrypted private key.
 */
public final class PemEncoding {

    /** Password used for the encrypted-private-key round trip. */
    public static final char[] PASSWORD = "changeit".toCharArray();

    private PemEncoding() {
    }

    /**
     * Generates a fresh EC P-256 key pair.
     *
     * @return the key pair
     * @throws Exception when the generator is unavailable
     */
    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
        generator.initialize(256);
        return generator.generateKeyPair();
    }

    /**
     * Encodes a private key as an unencrypted PKCS#8 PEM document.
     *
     * @param privateKey key to encode
     * @return the PEM text
     */
    public static String encodePrivate(PrivateKey privateKey) {
        return PEMEncoder.of().encodeToString(privateKey);
    }

    /**
     * Encodes a private key as a password-encrypted PKCS#8 PEM document.
     *
     * @param privateKey key to encode
     * @param password   encryption password
     * @return the PEM text
     */
    public static String encodeEncrypted(PrivateKey privateKey, char[] password) {
        return PEMEncoder.of().withEncryption(password).encodeToString(privateKey);
    }

    /**
     * Decodes an unencrypted PKCS#8 PEM document.
     *
     * @param pem PEM text
     * @return the private key
     */
    public static PrivateKey decodePrivate(String pem) {
        return PEMDecoder.of().decode(pem, PrivateKey.class);
    }

    /**
     * Decodes a password-encrypted PKCS#8 PEM document.
     *
     * @param pem      PEM text
     * @param password decryption password
     * @return the private key
     */
    public static PrivateKey decodeEncrypted(String pem, char[] password) {
        return PEMDecoder.of().withDecryption(password).decode(pem, PrivateKey.class);
    }

    /**
     * Decodes a public key PEM document.
     *
     * @param pem PEM text
     * @return the public key
     */
    public static PublicKey decodePublic(String pem) {
        return PEMDecoder.of().decode(pem, PublicKey.class);
    }

    /**
     * Returns the first line of a PEM document, its type header.
     *
     * @param pem PEM text
     * @return the {@code -----BEGIN ...-----} line
     */
    public static String header(String pem) {
        return pem.lines().findFirst().orElse("");
    }

    /**
     * Produces the demo report.
     *
     * @return human-readable summary of the three round trips
     */
    public static String report() {
        try {
            KeyPair keyPair = generateKeyPair();
            String publicPem = PEMEncoder.of().encodeToString(keyPair.getPublic());
            String privatePem = encodePrivate(keyPair.getPrivate());
            String encryptedPem = encodeEncrypted(keyPair.getPrivate(), PASSWORD);

            boolean privateRoundTrip = decodePrivate(privatePem).equals(keyPair.getPrivate());
            boolean encryptedRoundTrip = decodeEncrypted(encryptedPem, PASSWORD)
                    .equals(keyPair.getPrivate());
            boolean publicRoundTrip = decodePublic(publicPem).equals(keyPair.getPublic());

            return "public key PEM type: " + header(publicPem) + System.lineSeparator()
                    + "private key PEM type: " + header(privatePem) + System.lineSeparator()
                    + "encrypted key PEM type: " + header(encryptedPem) + System.lineSeparator()
                    + "encrypted body is opaque: "
                    + !encryptedPem.contains(privatePem.lines().skip(1).findFirst().orElse("!"))
                    + System.lineSeparator()
                    + "public round trip: " + publicRoundTrip + System.lineSeparator()
                    + "private round trip: " + privateRoundTrip + System.lineSeparator()
                    + "encrypted round trip: " + encryptedRoundTrip;
        } catch (Exception failure) {
            return "PEM demo failed: " + failure;
        }
    }
}
