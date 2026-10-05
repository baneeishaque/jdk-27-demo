# X / Twitter thread

Ten posts driving traffic to the Medium deep dive. Publish in one sitting; the link goes in
the final post.

## The thread

**1/**
JDK 27 has nine JEPs.

Four are final and free. Four are previews that change how you write concurrency, parsing,
and crypto code. One keeps getting faster.

I measured all nine — code, tests, benchmarks — and wrote the maximum-detail guide.

**2/**
The four free wins (zero code changes):

- G1 is now the default GC everywhere
- Compact object headers: -8 bytes per object
- Post-quantum TLS 1.3 by default
- JFR redacts secrets before they reach disk

Upgrade, re-run tests, done.

**3/**
Measured, compact headers: a two-int record is 16 bytes on JDK 27 vs 24 with
`-XX:-UseCompactObjectHeaders`.

Verified with ThreadMXBean allocation counters — no GC timing involved. The test asserts
the delta, not a magic number.

**4/**
Structured concurrency (7th preview), measured on a real shape:

An order pipeline fanning out three service calls:

- Sequential: 570 ms
- Structured: 258 ms

And the test asserts both modes return identical quotes. Faster *and* provably
behaviour-preserving.

**5/**
The gotcha that cost me an hour:

With the "first success wins" joiner, a scope timeout is NOT CancelledByTimeoutException.

It arrives wrapped in ExecutionException. Your catch block never fires.

The guide has ten gotchas like this, each with a test.

**6/**
Lazy constants: at-most-once init — and failure is permanent.

If the supplier throws, every later get() throws NoSuchElementException naming the
original failure.

Retries belong INSIDE the supplier.

**7/**
Primitive patterns convert only when exact:

`42.0` takes `case int`
`5_000_000_000.0` falls to `case long`
`0.1` to `case double`

No silent truncation. Case order encodes exactness.

**8/**
Vector API (12th incubator), JMH, 2 forks:

- 1,024 floats: 0.857 µs scalar vs 0.216 µs vector
- 65,536 floats: 59.146 µs vs 14.797 µs

~4.0x — tracking the 4-lane width almost exactly.

**9/**
The full guide: every JEP with API shapes, code, measurements, a migration playbook, and
the ten-item gotcha catalogue.

7,000 words, zero hand-waving.

**10/**
JDK 27, JEP by JEP — the maximum-detail guide:

medium.com/@baneeishaque/jdk-27-jep-by-jep

Code + benchmarks:
github.com/Baneeishaque/jdk-27-demo

## Posting notes

- Post 1–2 back-to-back, then one every 2–3 minutes.
- Quote-post post 4 with `shot-jmh.png` an hour later.
- The thread's job is the click on post 10 — do not front-load links.
