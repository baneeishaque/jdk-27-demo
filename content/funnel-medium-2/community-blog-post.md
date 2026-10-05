# Community blog post

Series companion for the community site. One post that frames all three articles and links
to them as the full write-ups.

## Post

## Three JDK 27 deep dives: concurrency, performance, and security — all measured

JDK 27 shipped nine JEPs, and my first instinct was the same as yours: read the release
notes, nod, and get back to the ticket queue. Instead I built a showcase — every JEP as
runnable code with a test — and then wrote three deep dives on the parts that change
decisions, not just the parts that change flags.

**Part 1: Structured concurrency.** The seventh preview, and the first one whose API reads
like the mental model. One scope per request, one fork per remote call, and the scope is
the cancellation boundary `ExecutorService` never was. I measured it on a realistic shape —
an order pipeline fanning out three service calls — and got ~2.2x (570 ms to 258 ms) with
the test proving both paths return identical quotes. The article also covers the gotcha
that cost me an hour: with the first-success-wins joiner, timeouts arrive wrapped in
`ExecutionException`, so the obvious catch block never fires.

**Part 2: The free performance wins.** Two of JDK 27's performance JEPs need zero code
changes: G1 is now the default collector in every environment, and object headers shrank
from 96 to 64 bits — 24 to 16 bytes per two-int record, measured with allocation counters.
The third, the Vector API, is still incubating and still ~4x on a dot product, tracking the
SIMD lane count almost exactly.

**Part 3: Security.** Post-quantum TLS is on by default — `X25519MLKEM768`, with clients
sending two key shares so old servers do not pay a retry round trip. JFR now redacts
secrets before recordings reach disk; the showcase proves it by scanning the raw recording
for the secret and finding nothing. And the PEM API turns key serialization into one-liners
including encryption — with two sharp edges worth knowing before you ship.

Each article is self-contained, with API shapes, the measured numbers, a migration
checklist, and the gotchas. All nine demos, 33 tests, and the benchmark reports are in the
repository below. If your team is planning the JDK 27 upgrade, start with part two — it is
the cheapest win — then read part one before you touch a single endpoint.

***

*Repository: `https://github.com/Baneeishaque/jdk-27-demo`*
*Part 1: `https://medium.com/@baneeishaque/jdk-27-structured-concurrency`*
*Part 2: `https://medium.com/@baneeishaque/jdk-27-performance-defaults`*
*Part 3: `https://medium.com/@baneeishaque/jdk-27-security-crypto`*

## Posting notes

- Publish the same day as article 1; update the post as articles 2 and 3 go live (or
  publish a "part 2 is live" follow-up each week — the links are already in place).
- Append `?utm_source=community-blog&utm_medium=referral&utm_campaign=jdk27-series` to the
  Medium links when posting.
- Keep the four links at the end on their own lines.
