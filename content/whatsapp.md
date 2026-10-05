# WhatsApp messages

Two formats: a community broadcast for Java/developer groups, and a short Status update.
WhatsApp is a favour to communities, not a broadcast channel — post rarely, and only after
the artifact is polished.

## Community broadcast (60–120 words)

> JDK 27 is out, and I built a showcase that measures every JEP instead of just describing
> it: runnable demos, 33 tests, and benchmarks.
>
> Highlights: G1 is now the default GC everywhere, object headers shrank 25%, TLS is
> post-quantum by default, and structured concurrency makes parallel service calls ~2.2x
> faster (570 ms → 258 ms) with provably identical results.
>
> Full code, benchmarks, and the gotchas the release notes skip:
> <https://github.com/Baneeishaque/jdk-27-demo>

## Status update (under 60 words)

> JDK 27, measured: 9 JEPs as runnable demos. G1 default everywhere, headers 16 vs 24 bytes,
> TLS post-quantum by default, structured concurrency ~2.2x on a real pipeline. 33 tests
> green. Code + benchmarks:
> <https://github.com/Baneeishaque/jdk-27-demo>

## Posting notes

- Append `?utm_source=whatsapp&utm_medium=social&utm_campaign=jdk27-launch` to links when
  posting in public groups; drop the tags in Status (it truncates).
- Never DM the link to people who did not ask; group context or Status only.
- One message per group. If someone replies with a question, answer in the group — that is
  the second wave of reach.
