package io.github.baneeishaque.jdk27.tls;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;

/**
 * Child VM for the JEP 527 demo: performs a loopback TLS 1.3 handshake against
 * a throwaway self-signed certificate so the parent process can inspect the
 * handshake trace. The mode argument optionally pins the client's named groups.
 */
public final class TlsHandshakeChild {

    private TlsHandshakeChild() {
    }

    /**
     * Runs one loopback handshake and exits.
     *
     * @param args optional mode: {@code "x25519"} pins the classic group
     * @throws Exception when keystore generation or the handshake fails
     */
    public static void main(String[] args) throws Exception {
        String mode = args.length > 0 ? args[0] : "default";
        Path directory = Files.createTempDirectory("jdk27-tls");
        Path keyStorePath = directory.resolve("demo.p12");
        generateCertificate(keyStorePath);

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (var input = Files.newInputStream(keyStorePath)) {
            keyStore.load(input, "changeit".toCharArray());
        }
        KeyManagerFactory keyManagers = KeyManagerFactory.getInstance("SunX509");
        keyManagers.init(keyStore, "changeit".toCharArray());
        TrustManagerFactory trustManagers = TrustManagerFactory.getInstance("SunX509");
        trustManagers.init(keyStore);

        SSLContext serverContext = SSLContext.getInstance("TLS");
        serverContext.init(keyManagers.getKeyManagers(), null, null);
        SSLContext clientContext = SSLContext.getInstance("TLS");
        clientContext.init(null, trustManagers.getTrustManagers(), null);

        try (SSLServerSocket server = (SSLServerSocket) serverContext.getServerSocketFactory()
                .createServerSocket(0)) {
            server.setEnabledProtocols(new String[]{"TLSv1.3"});
            Thread serverThread = Thread.ofVirtual().start(() -> {
                try (SSLSocket accepted = (SSLSocket) server.accept()) {
                    accepted.startHandshake();
                } catch (IOException expectedWhenClientPins) {
                    System.out.println("server: " + expectedWhenClientPins);
                }
            });
            try (SSLSocket client = (SSLSocket) clientContext.getSocketFactory()
                    .createSocket("localhost", server.getLocalPort())) {
                client.setEnabledProtocols(new String[]{"TLSv1.3"});
                if ("x25519".equals(mode)) {
                    SSLParameters parameters = client.getSSLParameters();
                    parameters.setNamedGroups(new String[]{"X25519"});
                    client.setSSLParameters(parameters);
                }
                client.startHandshake();
                System.out.println("client: " + client.getSession().getProtocol()
                        + " " + client.getSession().getCipherSuite());
            }
            serverThread.join();
        }
    }

    private static void generateCertificate(Path keyStorePath) throws IOException, InterruptedException {
        String keytool = Path.of(System.getProperty("java.home"), "bin", "keytool").toString();
        Process process = new ProcessBuilder(
                keytool,
                "-genkeypair",
                "-alias", "demo",
                "-keyalg", "EC",
                "-groupname", "secp256r1",
                "-dname", "CN=localhost",
                "-keystore", keyStorePath.toString(),
                "-storetype", "PKCS12",
                "-storepass", "changeit",
                "-keypass", "changeit",
                "-validity", "1")
                .redirectErrorStream(true)
                .start();
        String output = new String(process.getInputStream().readAllBytes());
        int exit = process.waitFor();
        if (exit != 0) {
            throw new IOException("keytool exited with " + exit + System.lineSeparator() + output);
        }
    }
}
