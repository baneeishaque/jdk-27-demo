# Explainer: JEP 538 — PEM encodings of cryptographic objects

## What changed

JDK 27 (third preview) adds `PEMEncoder` and `PEMDecoder` to `java.security`: direct
read/write of the PEM textual format for keys, certificates, CSRs, CRLs, and encrypted
private keys. The preview replaces hand-rolled base64 wrapping, shell-outs to `openssl`,
and the old dance of `Base64.getMimeEncoder()` plus manual header lines.

The API shape is a builder with two carrier types:

```java
PEMEncoder.of()                       // unencrypted
PEMEncoder.of().withEncryption(pw)    // PKCS#8 encrypted output
PEMDecoder.of()
PEMDecoder.of().withDecryption(pw)
```

What you pass in is a `BinaryEncodable` — and in JDK 27 that interface is implemented by
`AsymmetricKey` (so `PublicKey`/`PrivateKey` directly), `X509EncodedKeySpec`,
`PKCS8EncodedKeySpec`, `EncryptedPrivateKeyInfo`, `X509Certificate`, and `PEM` itself.

## The demo

[`PemEncoding`](../../src/main/java/io/github/baneeishaque/jdk27/pem/PemEncoding.java)
wraps the four operations that matter and round-trips them:

```java
public static String encodePrivate(PrivateKey privateKey) {          // :44
    return PEMEncoder.of().encodeToString(privateKey);
}

public static String encodeEncrypted(PrivateKey privateKey, char[] password) {   // :55
    return PEMEncoder.of().withEncryption(password).encodeToString(privateKey);
}

public static PrivateKey decodePrivate(String pem) {                 // :65
    return PEMDecoder.of().decode(pem, PrivateKey.class);
}

public static PrivateKey decodeEncrypted(String pem, char[] password) {   // :76
    return PEMDecoder.of().withDecryption(password).decode(pem, PrivateKey.class);
}
```

Note the decode call: `decode(pem, PrivateKey.class)` names the target type and returns it
directly — no intermediate spec object. (`PrivateKey` is `BinaryEncodable` through
`AsymmetricKey`, which is why the type parameter accepts it.)

## What we measured

```text
public key PEM type: -----BEGIN PUBLIC KEY-----
private key PEM type: -----BEGIN PRIVATE KEY-----
encrypted key PEM type: -----BEGIN ENCRYPTED PRIVATE KEY-----
encrypted body is opaque: true
public round trip: true
private round trip: true
encrypted round trip: true
```

The round trips compare `Key#equals`, so the DER content survived the text format exactly.
The "opaque" check verifies the encrypted body is not the readable PKCS#8 payload.

## Notes for practitioners

- Preview API: `--enable-preview --release 27` to compile, `--enable-preview` to run.
- `PEMEncoder.encode` returns `byte[]`, `encodeToString` returns `String`; both produce
  the full armored document.
- Decoding without a target class (`decode(pem)`) returns a `BinaryEncodable` — a `PEM`
  object when the type has no dedicated class, or a spec/certificate for known types.
- Wrong password: decryption failures surface as runtime exceptions; catch broadly around
  `withDecryption(...).decode(...)` and never log the PEM body.
- Keep `char[]` passwords clearable; the API takes `char[]` for a reason.
