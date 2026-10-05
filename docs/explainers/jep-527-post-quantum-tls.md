# Explainer: JEP 527 — post-quantum hybrid key exchange

## What changed

TLS 1.3 key exchange on JDK 27 defaults to the hybrid group `X25519MLKEM768`: a classical
X25519 exchange combined with ML-KEM-768, so the session is safe if *either* primitive
holds. `SecP256r1MLKEM768` and `SecP384r1MLKEM1024` are also available. The default group
list now starts with the hybrid group, which means JDK 27 clients offer post-quantum
protection to every TLS 1.3 server they meet, and fall back automatically when the server
does not support it.

## The demo

The interesting problem is observability: no public API reports the group a completed
handshake negotiated. `SSLSession` does not expose it, and
`SSLParameters#getNamedGroups` only echoes what you configured. So
[`PostQuantumTls`](../../src/main/java/io/github/baneeishaque/jdk27/tls/PostQuantumTls.java)
does what an operator would: runs the handshake in a child VM with
`javax.net.debug=ssl:handshake` and parses the trace.

```java
Process process = new ProcessBuilder(                        // :42
        java, "--enable-preview",
        "-Djavax.net.debug=ssl:handshake",
        "-cp", System.getProperty("java.class.path"),
        TlsHandshakeChild.class.getName(), mode) ...
```

The trace in JDK 27 is JSON-style, and two shapes carry the answer:

- `"named groups": [X25519MLKEM768, x25519, ...]` — the client's supported list, most
  preferred first. The first block in the trace is the client's.
- `"named group": X25519MLKEM768` — one per key-share entry; the first-seen set is what
  actually went on the wire.

`supportedGroups` (`:66`) takes the first plural block; `negotiatedGroups` (`:80`)
collects the singular entries in order. The child
([`TlsHandshakeChild`](../../src/main/java/io/github/baneeishaque/jdk27/tls/TlsHandshakeChild.java))
generates a throwaway EC P-256 certificate with `keytool`, starts a loopback TLS 1.3
server on an ephemeral port, and handshakes against it — with an optional mode that pins
the client's groups via `SSLParameters#setNamedGroups`.

## What we measured

```text
default supported groups: [X25519MLKEM768, x25519, secp256r1, secp384r1, secp521r1, x448, ffdhe2048, ffdhe3072, ffdhe4096]
hybrid is most preferred by default: true
default negotiated groups: [X25519MLKEM768, x25519]
pinned supported groups: [x25519]
pinned negotiated groups: [x25519]
hybrid group with pinned X25519: false
```

Two details worth noticing:

- The client sends **two key shares** by default (hybrid plus classic X25519) so a server
  that ignores the hybrid entry still gets a fast, non-retried handshake. The server picks
  `X25519MLKEM768`, the first entry.
- Pinning `x25519` via `setNamedGroups` shrinks both the offered and negotiated sets to the
  classic group — useful for interop debugging and for proving the default is what changed.

## Notes for practitioners

- Nothing to configure for normal client/server use; the default is on.
- If you pin `jdk.tls.namedGroups` (system property) or call `setNamedGroups`, you override
  the post-quantum preference — audit those pins.
- Handshake size grows with ML-KEM; if you operate middleboxes that parse ClientHello
  aggressively, test with real traffic.
