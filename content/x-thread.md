# X / Twitter thread

Twelve posts, each under 280 characters. Publish in one sitting; attach `shot-jmh.png` to
post 11 and `shot-demo.png` to post 1.

## The thread

**1/**
JDK 27 shipped nine JEPs last month. Release notes tell you what changed. They do not tell
you what it measures — or where the sharp edges are.

So I built a showcase: every JEP as runnable code, a test that proves it, a benchmark that
numbers it.

Thread.

**2/**
First, the four free wins (final JEPs, zero code changes):

- G1 is now the default GC everywhere
- Compact object headers
- Post-quantum TLS 1.3
- JFR secret redaction

Upgrade, re-run your tests, done.

**3/**
Compact object headers, measured: a two-int record is 16 bytes on JDK 27 vs 24 with
`-XX:-UseCompactObjectHeaders`.

8 bytes off every object, straight off your live set. Verified with ThreadMXBean allocation
counters — no GC timing involved.

**4/**
Post-quantum TLS: X25519MLKEM768 is the most-preferred group by default. Zero config.

Detail from the JSSE trace: the client sends TWO key shares (hybrid + X25519) so servers
that ignore the hybrid entry skip the retry round-trip.

**5/**
JFR redaction: a child VM launched with `-Ddb.password=hunter2...` records it as
`[[REDACTED]]`.

Redaction happens in-process, before the recording touches disk. Verified by scanning the
raw recording for the secret — absent.

**6/**
The previews worth spiking: structured concurrency, lazy constants, primitive patterns, PEM
encodings.

The shape that matters:

```text
try (var scope = StructuredTaskScope.open()) {
  var a = scope.fork(() -> fetch("price"));
  scope.join();
}
```text

**7/**
Structured concurrency, measured: an order pipeline fanning out three service calls
(40/60/30 ms) goes from 570 ms sequential to 258 ms with scopes — ~2.2x.

The test asserts identical quotes in both modes. Behaviour-preserving by proof, not by hope.

**8/**
Gotcha: with `anySuccessfulOrThrow()`, a scope timeout is wrapped.

You get `ExecutionException`, whose cause is `CancelledByTimeoutException`. Catching the
latter directly never fires.

Documented and tested in the repo.

**9/**
Lazy constants: at-most-once init, and failure is permanent.

If the supplier throws, every later `get()` throws `NoSuchElementException` naming the
original failure. Retries must happen INSIDE the supplier.

**10/**
Primitive patterns match only on exact conversion:

`42.0` takes `case int`
`5_000_000_000.0` falls to `case long`
`0.1` to `case double`

No silent truncation, no manual range checks.

**11/**
Vector API (12th incubator), JMH, 2 forks, dot product:

- 1,024 floats: 0.857 µs scalar vs 0.216 µs vector (~4.0x)
- 65,536 floats: 59.146 µs vs 14.797 µs (~4.0x)

The speedup tracks the 4-lane width almost exactly.

**12/**
All nine JEPs with code, tests, benchmarks, and line-by-line explainers:

github.com/Baneeishaque/jdk-27-demo

Plus a 37-language video walkthrough. If you are planning the JDK 27 migration, start here.

## Posting notes

- Post 1 and 2 back-to-back, then one post every 2–3 minutes — do not dump all twelve at once.
- Quote-post post 11 an hour later with `shot-jmh.png` as the standalone visual.
- Pin the thread for a week.
