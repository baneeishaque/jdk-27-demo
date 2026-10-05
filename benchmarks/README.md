# Benchmarks

Two complementary layers:

1. `results/jvm-features.md` — single-run wall-clock observations of the
   JVM-level features (compact headers, G1 under a constrained CPU count,
   structured-concurrency pipeline). Regenerate with
   `scripts/bench-jvm-features.sh > benchmarks/results/jvm-features.md`.
2. `results/vector-jmh.txt` — the raw JMH report for the Vector API dot
   product. Regenerate with `scripts/bench-vector.sh`.

## Recorded results

Machine: Apple Silicon (arm64) on macOS, OpenJDK 27 (`27+35`), default flags
plus `--enable-preview --add-modules jdk.incubator.vector`.

### Compact object headers (JEP 534)

| Scenario | bytes per `Point(1,2)` |
| --- | --- |
| compact object headers (default) | 16 |
| `-XX:-UseCompactObjectHeaders` | 24 |

### G1 under a constrained CPU count (JEP 523)

`-XX:ActiveProcessorCount=1` still resolves to `UseG1GC = true`; on JDK 26
the same probe selected Serial.

### Structured concurrency order pipeline (JEP 533)

| Mode | 4 orders, wall clock |
| --- | --- |
| sequential service calls | ~570 ms |
| structured fan-out | ~258 ms |

### Vector API dot product (JEP 537, JMH)

| Size | Scalar | Vector | Speedup |
| --- | --- | --- | --- |
| 1,024 floats | 0.857 µs | 0.216 µs | ~4.0x |
| 65,536 floats | 59.146 µs | 14.797 µs | ~4.0x |

Both sizes land near the 4-lane `S_128_BIT` width of the preferred species on
this machine, which is why the speedup is flat across sizes.

## Methodology notes

- JMH settings: average time, 3 warmup + 3 measurement iterations of 1 s,
  2 forks, thread-scoped state, deterministic data (`Random(27)`).
- The JVM-feature numbers are single-run observations intended to show the
  delta, not statistically rigorous scores; rerun on your own machine to
  reproduce them.
