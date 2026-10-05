# Upgrading a service to JDK 27

Practical notes distilled from building this showcase. None of these require code changes
on day one — the headline features are either final and automatic or opt-in previews.

## What you get for free on day one

- **G1 everywhere (JEP 523).** Services that run in small containers previously fell back
  to Serial GC. On JDK 27 they get G1 automatically. If you had `-XX:+UseG1GC` in your
  flags to force it, you can drop it; if you had `-XX:+UseSerialGC` for a reason, keep it —
  explicit flags still win.
- **Compact object headers (JEP 534).** The 64-bit object header shrinks from 96 to 64
  bits, typically 10–20% smaller live sets for object-dense heaps. The flag
  `-XX:-UseCompactObjectHeaders` reverts to the legacy layout if a tool misbehaves.
- **Post-quantum TLS (JEP 527).** `X25519MLKEM768` is the most preferred TLS 1.3 group.
  No configuration needed for clients and servers that use the JDK provider; handshakes
  with peers that do not support it fall back to classic groups.
- **JFR redaction (JEP 536).** Command-line, environment, and system-property values are
  redacted in recordings by default. If your tooling depended on reading those values from
  a recording, pin the filter lists via `-XX:FlightRecorderOptions`.

## Preview features worth a spike

- **Structured concurrency (JEP 533).** Replace `ExecutorService` + `Future.get()` fan-out
  with `StructuredTaskScope`. The migration is mechanical for request-scoped work: one
  scope per unit of work, `fork` per remote call, `join` once. You get cancellation of
  siblings for free when one call fails.
- **Lazy constants (JEP 531).** For expensive singletons, `LazyConstant.of(...)` replaces
  the holder-class idiom and signals intent to the JIT. Lazy collections
  (`List.ofLazy`, `Map.ofLazy`, `Set.ofLazy`) shine for computed views.
- **Primitive patterns (JEP 532).** `case int i` inside record patterns removes manual
  range checks when matching numeric payloads; the exactness rules do the bounds work.
- **PEM encodings (JEP 538).** `PEMEncoder`/`PEMDecoder` replace hand-rolled base64
  wrapping and `openssl` shell-outs for key material handling.

## Flags and build changes checklist

1. Compile with `--release 27`; add `--enable-preview` while any preview API is in use
   (the flag is per-compilation and per-execution — both sides must agree).
2. Vector API users: add `--add-modules jdk.incubator.vector` at compile time and run time.
3. JDK 23+ removed implicit annotation processing from the classpath; configure
   `annotationProcessorPaths` explicitly (see this repository's `pom.xml` for the JMH
   pattern).
4. Preview class files carry a minor version the JVM refuses to load without
   `--enable-preview`; this bites child processes and tooling that launches new JVMs —
   pass the flag everywhere.

## Things that bit us

- `LazyConstant.get()` wraps a failed initial computation in `NoSuchElementException`; the
  original cause is named in the message but not attached as a cause chain.
- A timeout inside a scope surfaces as `ExecutionException` whose cause is
  `StructuredTaskScope.CancelledByTimeoutException` when the joiner's failure type is
  `ExecutionException`.
- `RecordingFile` in JDK 27 reads via the static `RecordingFile.readAllEvents(Path)`; the
  older `open(...)` + `readAllEvents()` instance pair no longer exists.
- The JSSE debug trace is JSON-style now: group names appear as `"named group": ...`
  (singular, key shares) and `"named groups": [...]` (plural, supported list).
