# X / Twitter threads

Three mini-threads (six posts each), one per article, published a week apart. The link to
the article goes in the final post of each thread.

## Thread 1 — structured concurrency (week 1)

**1/**
Structured concurrency is on its 7th preview in JDK 27 — and it is finally shaped like the
mental model.

One scope per request. One fork per remote call. The scope is the cancellation boundary
ExecutorService never was.

Six posts on what changed.

**2/**
The shape:

```text
try (var scope = StructuredTaskScope.open()) {
  var price = scope.fork(() -> fetch("price"));
  var stock = scope.fork(() -> fetch("stock"));
  scope.join();
}
```text

When anything fails, siblings are cancelled. No leaked tasks, ever.

**3/**
Timeouts moved into configuration — `joinUntil(Instant)` is gone:

```text
StructuredTaskScope.open(
  Joiner.<String>anySuccessfulOrThrow(),
  cfg -> cfg.withTimeout(Duration.ofMillis(300)))
```text

**4/**
Measured on a realistic pipeline — 3 service calls per quote:

- Sequential: 570 ms
- Structured: 258 ms

~2.2x. And the test asserts both paths return identical quotes. Faster and provably
correct.

**5/**
The gotcha that cost an hour: with the first-success-wins joiner, a timeout is NOT
CancelledByTimeoutException.

It arrives wrapped in ExecutionException. Your catch block never fires.

**6/**
Full article — API shapes, the exception chain, the migration recipe:

medium.com/@baneeishaque/jdk-27-structured-concurrency

## Thread 2 — performance (week 2)

**1/**
Two of JDK 27's performance JEPs cost you zero code changes. This is the cheapest upgrade
your service gets all year.

Six posts, all measured.

**2/**
G1 is now the default collector in every environment.

Before: fewer than 2 CPUs or under ~1.8 GB heap meant Serial GC — a silent behaviour
change between environments.

After: G1, always, unless you say otherwise.

**3/**
Compact object headers: 96 bits to 64.

A two-int record: 24 bytes → 16 bytes.

Measured with ThreadMXBean allocation counters, and the test asserts the delta — not a
hard-coded number.

**4/**
8 bytes off every object compounds fast. On a heap with millions of small objects that is
double-digit percentages of live set — for free.

**5/**
Vector API (12th incubator), JMH, 2 forks:

- 1,024 floats: 0.857 µs → 0.216 µs
- 65,536 floats: 59.146 µs → 14.797 µs

~4.0x, tracking the 4-lane width almost exactly.

**6/**
Full article — code, raw numbers, upgrade checklist:

medium.com/@baneeishaque/jdk-27-performance-defaults

## Thread 3 — security (week 3)

**1/**
Three JDK 27 security changes, ranked by how much code they delete from your codebase.

Closing the three-part series. Six posts.

**2/**
Post-quantum TLS is on by default: X25519MLKEM768 is the most-preferred group.

Zero configuration. Clients send two key shares so old servers skip the retry round-trip.

**3/**
JFR now redacts secrets before recordings reach disk.

The showcase proves it the hard way: scan the raw recording for the secret. Result:
absent.

No more scrubbing `-Ddb.password` out of bug-ticket attachments.

**4/**
The PEM API turns key serialization into one-liners — including encryption:

`PEMEncoder.of().withEncryption(pw).encodeToString(key)`

It deletes every hand-rolled BEGIN/END base64 wrapper you have.

**5/**
Two sharp edges from the article: the PEM byte[] constructor takes TEXTUAL content (not
DER), and PEM containers cannot be encrypted directly.

Both with tests in the repo.

**6/**
Full article + the complete series:

Part 3: medium.com/@baneeishaque/jdk-27-security-crypto
Part 1: medium.com/@baneeishaque/jdk-27-structured-concurrency
Part 2: medium.com/@baneeishaque/jdk-27-performance-defaults

## Posting notes

- One thread per week, the morning after each article goes live.
- Post 1–2 back-to-back, then one every 2–3 minutes; never dump the whole thread at once.
- Quote-post the measured-number post (4 in thread 1, 5 in thread 2, 3 in thread 3) an
  hour later as a standalone visual.
