# Storyboard

Generated from [`scenes.json`](scenes.json) by `build-video.py --docs`.
Timings are the English narration timeline; other locales shift slightly.

| # | Time | Slide title (en) | On-screen code | Narration (en) |
| - | ---- | ---------------- | -------------- | -------------- |
| 1 | 0.4–8.3s | Nine JEPs. One showcase. | `jdk 27 · 9 jeps · openjdk 27+35` | JDK 27 shipped nine JEPs. I built a runnable demo, a test, and a benchmark for every single one. Here is what they measure. |
| 2 | 10.1–18.0s | G1 is now the default collector everywhere | `bool UseG1GC = true {product} {ergonomic}` | The G1 collector is now the ergonomic default in every environment — even on a single CPU. No more surprise collector changes in small containers. |
| 3 | 19.8–26.2s | Compact object headers, measured | `new Point(1, 2) · 24 B → 16 B` | Object headers shrank from ninety-six bits to sixty-four. A two-int record now allocates sixteen bytes instead of twenty-four. |
| 4 | 28.0–36.7s | Post-quantum TLS by default | `X25519MLKEM768 · most preferred by default` | X25519MLKEM768 is now the most preferred TLS 1.3 key exchange group — quantum-resistant handshakes with zero configuration. |
| 5 | 38.5–46.1s | JFR redacts secrets before disk | `jdk.InitialSystemProperty[db.password] = [[REDACTED]]` | JDK 27 redacts sensitive values inside the JVM. A password recorded in a startup event never reaches the recording file. |
| 6 | 47.9–56.1s | Lazy constants, at most once | `LazyConstant.of(Services::new) · at most once` | Lazy constant replaces the holder-class idiom. Computation happens at most once — and failure is permanent, so retries belong inside the supplier. |
| 7 | 57.9–66.3s | Primitive patterns, exact by design | `42.0 → int · 5e9 → long · 0.1 → double` | Primitive patterns convert only when the conversion is exact. Forty-two takes the int case, five billion falls to long, and zero point one to double. |
| 8 | 68.1–75.5s | Structured concurrency, finally shaped right | `StructuredTaskScope.open() · fork · join` | One scope per request, one fork per remote call. The scope is the cancellation boundary — when anything fails, its siblings are cancelled. |
| 9 | 77.3–86.5s | The measured pipeline | `4 orders · 570 ms → 258 ms · ~2.2x` | Four orders through three service calls: five hundred seventy milliseconds sequential, two hundred fifty-eight structured — about two point two times faster, with identical results. |
| 10 | 88.3–95.2s | Vector API: four times faster | `1024 floats · 0.857 µs → 0.216 µs · ~4.0x` | A dot product with the Vector API runs about four times faster than the scalar loop, tracking the four SIMD lanes almost exactly. |
| 11 | 97.0–102.8s | PEM encodings, one line per key | `PEMEncoder.of().withEncryption(pw).encodeToString(key)` | The PEM API encodes keys in one line — including encryption — and deletes every hand-rolled base64 wrapper. |
| 12 | 104.6–112.4s | The receipts | `9 jeps · 33 tests · jmh 2 forks · 37 locales` | Nine JEPs, thirty-three tests, real benchmarks on JDK 27 build thirty-five. All code and numbers are in the repository. |
| 13 | 114.2–120.5s | Code, tests, benchmarks | `github.com/Baneeishaque/jdk-27-demo` | Clone the repository, run the tests, and reproduce every number on your own hardware. Thanks for watching. |
