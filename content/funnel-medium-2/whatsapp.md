# WhatsApp messages

A series broadcast for Java/developer groups (updated weekly) and a closing Status update.
One message per week per group; the series ends with a single "complete series" broadcast.

## Week 1 — structured concurrency broadcast (60–120 words)

> JDK 27 deep dive #1: structured concurrency. One scope per request, one fork per remote
> call, and the scope cancels siblings when anything fails — the guarantee ExecutorService
> never gave us.
>
> Measured on a realistic order pipeline: 570 ms → 258 ms (~2.2x), with a test proving
> identical results.
>
> Full article (plus a timeout gotcha that cost me an hour):
> <https://medium.com/@baneeishaque/jdk-27-structured-concurrency>

## Week 2 — performance broadcast (60–120 words)

> JDK 27 deep dive #2: the free performance wins. G1 is now the default collector
> everywhere, and object headers shrank 25% — a two-int record is 16 bytes instead of 24.
> Zero code changes for both.
>
> Plus the Vector API: still incubating, still ~4x faster than scalar on a dot product
> (JMH, 2 forks).
>
> Full article with raw numbers:
> <https://medium.com/@baneeishaque/jdk-27-performance-defaults>

## Week 3 — security broadcast (60–120 words)

> JDK 27 deep dive #3: security. Post-quantum TLS is on by default, JFR redacts secrets
> before recordings reach disk, and the PEM API turns key handling into one-liners.
>
> That completes the three-part series — concurrency, performance, security, all measured:
> <https://medium.com/@baneeishaque/jdk-27-structured-concurrency>
> <https://medium.com/@baneeishaque/jdk-27-performance-defaults>
> <https://medium.com/@baneeishaque/jdk-27-security-crypto>

## Status update (under 60 words)

> Three JDK 27 deep dives, all measured: structured concurrency ~2.2x, free performance
> wins (headers 24→16 bytes), post-quantum TLS by default. Full series:
> <https://medium.com/@baneeishaque/jdk-27-structured-concurrency>

## Posting notes

- Append `?utm_source=whatsapp&utm_medium=social&utm_campaign=jdk27-series` in public
  groups; drop tags in Status.
- One message per group per week; answer questions in the group.
- The week-3 broadcast is the only one that links all three articles — it is the series
  closer.
