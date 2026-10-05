# Instagram

A 10-slide carousel (the format that performs for technical content) plus a Reel caption.
Slides are square (1080×1080); each row is slide text plus the visual direction.

## Carousel: slides and copy

Design: dark background (`#0f172a`), white text, one screenshot per slide where indicated.
Slide 1 is the hook — it must work with zero context.

| # | On-slide text | Visual |
| - | ------------- | ------ |
| 1 | JDK 27 shipped 9 JEPs. I measured all of them. Here is what actually changed. | Terminal screenshot of `run-demo.sh` output on a dark background |
| 2 | 4 features need zero code changes: G1 default everywhere, headers -25%, post-quantum TLS, JFR redaction. | Four checkmark rows, one highlighted |
| 3 | Same record. Same JVM. 24 bytes → 16 bytes per object. One flag proves it. | Two boxes, one visibly slimmer, with byte counts |
| 4 | X25519MLKEM768 is now the default key exchange. Quantum-resistant handshakes, zero configuration. | Lock icon over a circuit pattern; group list from the trace |
| 5 | Fan out 3 service calls: 570 ms sequential, 258 ms structured. ~2.2x — and the test proves identical results. | Timeline bars, sequential vs parallel |
| 6 | A scope timeout does NOT throw CancelledByTimeoutException. It arrives wrapped in ExecutionException. Your catch block never fires. | Exception chain diagram: ExecutionException → CancelledByTimeoutException |
| 7 | Lazy constants: at-most-once init, and failure is permanent. Retries belong inside the supplier. | State diagram: uninitialized → computing → value / failed |
| 8 | Dot product, 65,536 floats: 59.146 µs scalar, 14.797 µs vector. ~4.0x — the 4-lane width almost exactly. | JMH summary screenshot |
| 9 | 9 JEPs. 33 tests, all green. JMH benchmarks, 2 forks. Line-by-line explainers per JEP. | Repository file tree |
| 10 | All code, benchmarks, and the gotchas the release notes skip. Save this for your JDK 27 migration. | Repository banner; link in bio |

## Caption (Reel)

JDK 27, measured: 9 JEPs as runnable demos. G1 default everywhere, headers 24→16 bytes,
post-quantum TLS by default, structured concurrency ~2.2x on a real order pipeline.
33 tests green. Full code + benchmarks in bio.

\#java #jdk27 #coding #programming #softwareengineer #backend #performance

## Posting notes

- Post the carousel Tuesday–Thursday, 11:00–13:00 local; the Reel the same evening.
- The first slide must read at thumbnail size — keep it under eight words per line.
- Add `?utm_source=instagram&utm_medium=social&utm_campaign=jdk27-launch` to the bio link,
  not the caption (captions do not carry clickable links).
