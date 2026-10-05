# jdk-27-demo

An industrial showcase of all nine JDK 27 JEPs: runnable demos, a structured-concurrency
order pipeline, a JUnit 5 test matrix, and a JMH benchmark harness with measured results.

The repository doubles as a reference implementation: every architectural decision is
documented in [`docs/`](docs/), every source file carries doc comments, and the
[`content/`](content/) directory ships the complete content-marketing kit that presents the
project (blog, social posts, and a YouTube pipeline that narrates the walkthrough in 20
languages with subtitles in 37 — see [`content/youtube/`](content/youtube/README.md)). The
kit includes a flagship Medium funnel ([`content/funnel-medium/`](content/funnel-medium/README.md))
and a three-article series ([`content/funnel-medium-2/`](content/funnel-medium-2/README.md));
[`events/`](events/webinar.md) ships the webinar, hands-on lab, and free/paid training
playbooks.

## The nine JEPs

| JEP | Title | Status | Demo | What it proves |
| --- | --- | --- | --- | --- |
| [523](https://openjdk.org/jeps/523) | Make G1 the Default Garbage Collector in All Environments | Final | [`gc/G1Default`](src/main/java/io/github/baneeishaque/jdk27/gc/G1Default.java) | G1 is selected even with `-XX:ActiveProcessorCount=1` |
| [527](https://openjdk.org/jeps/527) | Post-Quantum Hybrid Key Exchange in TLS 1.3 | Final | [`tls/PostQuantumTls`](src/main/java/io/github/baneeishaque/jdk27/tls/PostQuantumTls.java) | `X25519MLKEM768` is the most preferred group by default |
| [531](https://openjdk.org/jeps/531) | Lazy Constants | Third preview | [`lazy/LazyConstants`](src/main/java/io/github/baneeishaque/jdk27/lazy/LazyConstants.java) | At-most-once computation, lazy collections, failure permanence |
| [532](https://openjdk.org/jeps/532) | Primitive Types in Patterns, instanceof, and switch | Fifth preview | [`patterns/PrimitivePatterns`](src/main/java/io/github/baneeishaque/jdk27/patterns/PrimitivePatterns.java) | Exactness-driven matching (`int` → `long` → `double`) |
| [533](https://openjdk.org/jeps/533) | Structured Concurrency | Seventh preview | [`concurrency/StructuredConcurrency`](src/main/java/io/github/baneeishaque/jdk27/concurrency/StructuredConcurrency.java) | Fan-out, racing, timeout, failure propagation |
| [534](https://openjdk.org/jeps/534) | Compact Object Headers | Final | [`headers/CompactObjectHeaders`](src/main/java/io/github/baneeishaque/jdk27/headers/CompactObjectHeaders.java) | 16 vs 24 bytes per small object, measured |
| [536](https://openjdk.org/jeps/536) | JFR In-Process Data Redaction | Final | [`jfr/JfrRedaction`](src/main/java/io/github/baneeishaque/jdk27/jfr/JfrRedaction.java) | Secrets become `[REDACTED]` before they reach disk |
| [537](https://openjdk.org/jeps/537) | Vector API | Twelfth incubator | [`vector/VectorApi`](src/main/java/io/github/baneeishaque/jdk27/vector/VectorApi.java) | ~4x dot-product speedup, verified by JMH |
| [538](https://openjdk.org/jeps/538) | PEM Encodings of Cryptographic Objects | Third preview | [`pem/PemEncoding`](src/main/java/io/github/baneeishaque/jdk27/pem/PemEncoding.java) | PEM round trips, including encrypted private keys |

The showcase also ships an integration demo —
[`orders/OrderPipeline`](src/main/java/io/github/baneeishaque/jdk27/orders/OrderPipeline.java)
— that combines structured concurrency with a lazy service registry and quotes an order
book ~2.2x faster than the sequential baseline.

## Quickstart

```bash
mise install              # JDK 27 + Maven 3.10 + Node 22 (from mise.toml)
./mvnw verify             # compile + 33 tests
./scripts/run-demo.sh     # run every demo and print its report
./scripts/run-demo.sh tls # run a single demo by id
```

Requires JDK 27 (or let mise provide it). The preview APIs need
`--enable-preview --release 27` at compile time and `--enable-preview` at run time; the
Maven build and the scripts already pass the right flags.

## Scripts

| Script | Purpose |
| --- | --- |
| `./mvnw test` | Compile and run the JUnit 5 suite (33 tests) |
| `./scripts/run-demo.sh [id]` | Compile and run all demos (or one: `gc`, `headers`, `jfr`, `lazy`, `patterns`, `concurrency`, `orders`, `tls`, `vector`, `pem`) |
| `./scripts/bench-vector.sh` | JMH dot-product benchmark, writes `benchmarks/results/vector-jmh.txt` |
| `./scripts/bench-jvm-features.sh` | Emit the JVM-feature measurement report as markdown |

## Project structure

```text
src/main/java/io/github/baneeishaque/jdk27/
  Main.java              # CLI dispatcher: one demo id or all demos
  gc/                    # JEP 523 — G1 default
  headers/               # JEP 534 — compact object headers
  jfr/                   # JEP 536 — recording redaction (+ child VM)
  lazy/                  # JEP 531 — lazy constants
  patterns/              # JEP 532 — primitive patterns
  concurrency/           # JEP 533 — structured concurrency
  orders/                # integration pipeline (JEP 533 + 531)
  tls/                   # JEP 527 — post-quantum TLS (+ child VM)
  vector/                # JEP 537 — Vector API
  pem/                   # JEP 538 — PEM encodings
  bench/                 # JMH benchmark
src/test/java/           # JUnit 5 suite, one test class per demo
scripts/                 # run + benchmark scripts
benchmarks/results/      # recorded measurements (committed)
content/                 # content-marketing kit (see content/README.md)
events/                  # webinar, hands-on, free/paid training playbooks
docs/                    # architecture, guides, trade-offs, explainers
```

## Measured results

Recorded on Apple Silicon (arm64), OpenJDK 27 (`27+35`), default flags. Full report:
[`benchmarks/README.md`](benchmarks/README.md).

| Feature | Result |
| --- | --- |
| Compact object headers (JEP 534) | `Point(1,2)`: 16 bytes vs 24 with `-XX:-UseCompactObjectHeaders` |
| G1 default (JEP 523) | `UseG1GC = true` even with `-XX:ActiveProcessorCount=1` |
| Structured concurrency (JEP 533) | 4-order pipeline: ~258 ms vs ~570 ms sequential |
| Vector API (JEP 537) | Dot product ~4.0x faster (JMH, both 1K and 64K floats) |

## Documentation

| Document | Contents |
| --- | --- |
| [`docs/architecture.md`](docs/architecture.md) | Module layout, child-VM probe pattern, integration pipeline |
| [`docs/testing.md`](docs/testing.md) | Test strategy, what each test proves, how to extend |
| [`docs/trade-offs.md`](docs/trade-offs.md) | Decisions taken and the alternatives that lost |
| [`docs/upgrade-guide.md`](docs/upgrade-guide.md) | Practical notes for moving a service to JDK 27 |
| [`docs/explainers/`](docs/explainers/) | Walkthroughs of every demo, JEP by JEP |
| [`content/README.md`](content/README.md) | Content kit map, publishing order, funnel conventions |
| [`events/webinar.md`](events/webinar.md) | Event playbooks: webinar, hands-on session, free and paid training |

## License

[MIT](LICENSE)
