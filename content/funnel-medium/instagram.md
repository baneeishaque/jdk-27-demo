# Instagram

An 8-slide carousel (the format that performs for technical content) plus a Reel caption,
driving traffic to the Medium deep dive (bio link, since captions cannot carry clickable
links).

## Carousel: slides and copy

Design: dark background (`#0f172a`), white text, one screenshot per slide where indicated.
Slide 1 is the hook — it must work with zero context.

| # | On-slide text | Visual |
| - | ------------- | ------ |
| 1 | JDK 27 shipped 9 JEPs. I measured all of them. Full guide in bio. | `run-demo.sh` terminal output on a dark background |
| 2 | 4 features, zero code changes: G1 default everywhere, headers -25%, post-quantum TLS, JFR redaction. | Four checkmark rows |
| 3 | Same record. Same JVM. 24 bytes → 16 bytes per object. That is 8 bytes off every live object. | Two boxes, one visibly slimmer |
| 4 | 3 service calls per quote: 570 ms sequential, 258 ms structured. And the test proves identical results. | Timeline bars, sequential vs parallel |
| 5 | Dot product, 65,536 floats: 59.146 µs scalar, 14.797 µs vector. ~4.0x — exactly the 4-lane width. | JMH summary screenshot |
| 6 | Scope timeout? It does NOT throw CancelledByTimeoutException. It arrives wrapped in ExecutionException. | Exception chain diagram |
| 7 | The guide catalogues 10 gotchas the release notes skip — each with a test in the repo. | Numbered list on a dark card |
| 8 | JDK 27, JEP by JEP: the maximum-detail guide. 7,000 words. Every API shape. Every number. Link in bio. | Article hero + repository banner |

## Caption (Reel)

JDK 27, measured: 9 JEPs, 33 tests, real benchmarks. G1 by default everywhere, headers
24→16 bytes, post-quantum TLS, structured concurrency ~2.2x, Vector API ~4x. Full
JEP-by-JEP guide in bio.

\#java #jdk27 #coding #programming #softwareengineer #backend #performance

## Posting notes

- Carousel Tuesday–Thursday 11:00–13:00; Reel the same evening.
- Bio link: `https://medium.com/@baneeishaque/jdk-27-jep-by-jep?utm_source=instagram&utm_medium=social&utm_campaign=jdk27-medium`.
- Slide 1 must read at thumbnail size — short lines, big type.
