# JDK 27 security: post-quantum TLS, JFR redaction, and PEM

Three of JDK 27's nine JEPs touch security, and they split cleanly: two are final and free
(post-quantum TLS, JFR redaction), and one is a preview that deletes a surprising amount of
boilerplate (PEM encodings). All three were verified on OpenJDK 27 (`27+35`) with runnable
code — including a real loopback TLS 1.3 handshake and a raw-recording secret scan.

## JEP 527: post-quantum key exchange, on by default

`X25519MLKEM768` — a hybrid of X25519 and ML-KEM-768 (FIPS 203) — is now the most
preferred TLS 1.3 key exchange group. The word to notice is *hybrid*: the session key
depends on both the classical and the post-quantum exchange, so the handshake is no weaker
than X25519 today, and no easier to harvest-and-decrypt later.

**The evidence.** The demo starts a loopback TLS 1.3 server and client with JSSE debug
tracing and parses the trace:

```text
default supported groups: [X25519MLKEM768, x25519, secp256r1, secp384r1, secp521r1, ...]
hybrid is most preferred by default: true
pinned to x25519: [x25519]
```

**Two details the release notes skip.**

1. The debug trace is JSON-style. `"named groups": [...]` (plural) is the supported list;
   `"named group": X25519MLKEM768` (singular) is an actual key share.
2. The client sends **two key shares** by default — hybrid and classic X25519. A server
   that ignores the hybrid entry still completes without a HelloRetryRequest. It is a
   deliberate latency trade: a slightly larger first flight for zero extra round trips.

**Migration notes.** Zero code changes for clients. Audit two categories: tooling that
parses the JSSE debug trace (format changed), and servers with strict group allow-lists
that now reject the hybrid entry — extend the list instead of disabling the default.

## JEP 536: JFR redaction, in-process

JFR has always been a data-exfiltration risk: recordings capture system properties,
environment variables, and event fields, and a `-Ddb.password=...` ends up in a file you
attach to a bug ticket.

JDK 27 redacts sensitive values **inside the JVM**, before the recording is written. The
built-in rules cover system properties and environment variables whose names match common
secret patterns; an API covers custom event fields.

**The evidence.** The showcase spawns a child VM with a secret system property, records
startup events, then scans the raw recording bytes for the secret:

```text
jdk.InitialSystemProperty[db.password] = [[REDACTED]]
raw secret present anywhere: false
```

The second line is the important one — the check is not "does the event look redacted" but
"does the secret exist anywhere in the file". It does not.

**The gotcha.** The child JVM needs `--enable-preview` when the classes it loads were
compiled with preview features — a spawned process without it fails at class load, not
with a warning. The demo wires the flag through explicitly, which is the pattern to copy
for any tooling that launches child JVMs.

**Migration notes.** Zero code changes for the built-in patterns. Audit custom event fields
carrying credentials and add redaction annotations where the defaults do not reach. Re-check
any monitoring pipeline that parses JFR recordings — redacted fields change shape.

## JEP 538: PEM encodings, finally a one-liner

Third preview. PEM (RFC 7468) is the format keys travel in — TLS certificates, SSH keys,
Kubernetes secrets — and until now the JDK made you assemble it by hand: base64 the DER,
wrap `BEGIN`/`END` lines, hope you got the line width right.

**The API.**

```java
String pem = PEMEncoder.of().encodeToString(privateKey);
PrivateKey back = PEMDecoder.of().decode(pem, PrivateKey.class);

String enc = PEMEncoder.of().withEncryption(password).encodeToString(privateKey);
PrivateKey dec = PEMDecoder.of().withDecryption(password).decode(enc, PrivateKey.class);
```

`PEMEncoder.of()` / `PEMDecoder.of()` handle public keys, private keys, encrypted private
keys, certificates, and the `PEM` container. `AsymmetricKey` extends `Key` and
`BinaryEncodable`, so `PublicKey` and `PrivateKey` objects encode directly — no
`getEncoded()` juggling, no spec wrappers.

**The evidence.** The showcase round-trips all three forms — public, private, and
encrypted private — and verifies with `Key#equals`, not string comparison.

**Two sharp edges.**

1. The `PEM(String, byte[])` constructor takes *textual* PEM content, not DER bytes.
   Passing `key.getEncoded()` (which is DER) produces a container that fails on decode —
   use the key objects or `PKCS8EncodedKeySpec`/`X509EncodedKeySpec` instead.
2. `PEM` container objects cannot be encrypted directly; encrypt the key material.

**Migration notes.** Replace hand-rolled `BEGIN/END` builders and base64 wrappers. The
decode side is where bugs hide today — malformed line lengths, missing trailing newlines —
and the API removes that entire class of failure.

## The combined checklist

1. Upgrade and re-run the TLS suite; confirm the hybrid group appears in your debug traces.
2. Extend strict server group allow-lists to accept `X25519MLKEM768`.
3. Confirm JFR recordings still parse in your monitoring pipeline; update custom-field
   redaction.
4. Move key serialization to the PEM API; delete base64 wrappers; keep the round-trip
   tests.

## Run it

```bash
git clone https://github.com/Baneeishaque/jdk-27-demo
cd jdk-27-demo
./mvnw verify
./scripts/run-demo.sh tls
./scripts/run-demo.sh jfr
./scripts/run-demo.sh pem
```

This article is part three of a three-part JDK 27 series; parts one and two cover
structured concurrency and the free performance wins.
