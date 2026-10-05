# Reels / Shorts / TikTok

Three vertical scripts, 30–45 seconds each. Same footage works on Instagram Reels, YouTube
Shorts, and TikTok; captions burned in, hooks in the first two seconds.

## Script 1 — JDK 27 in 45 seconds (overview)

| Time | Visual | On-screen text |
| --- | --- | --- |
| 0–3 s | `run-demo.sh` output scrolling | `JDK 27: 9 JEPs in 45 seconds` |
| 3–9 s | Four checkmarks animating in | `G1 default · headers -25% · PQ TLS · JFR redaction` |
| 9–15 s | Two boxes, one slimmer | `24 → 16 bytes per object` |
| 15–22 s | Lock + circuit | `X25519MLKEM768 by default` |
| 22–30 s | Parallel timeline bars | `570 ms → 258 ms` |
| 30–38 s | JMH table | `Vector API: ~4.0x` |
| 38–45 s | Repo URL card | `github.com/Baneeishaque/jdk-27-demo` |

**Caption text for the post:**

JDK 27, measured: 9 JEPs, 33 tests, real benchmarks. Full code in bio.

```text
#java #jdk27 #programming #coding #softwareengineer
```

## Script 2 — the concurrency feature (structured concurrency)

| Time | Visual | On-screen text |
| --- | --- | --- |
| 0–3 s | Code editor: one method, three fetches | `3 service calls, 1 request` |
| 3–10 s | Sequential timeline: 40+60+30 ms | `Sequential: 130 ms per quote` |
| 10–18 s | Three bars fanning out | `Structured: max(40,60,30) = 60 ms` |
| 18–28 s | `StructuredTaskScope.open()` on screen | `try (var scope = StructuredTaskScope.open()) { ... }` |
| 28–38 s | Exception chain diagram | `Timeout? Wrapped in ExecutionException` |
| 38–45 s | Test output: identical quotes | `Both modes: identical results. Proven by test.` |

**Caption text for the post:**

Structured concurrency in JDK 27 (7th preview): 2.2x on a real order pipeline — and the
test proves the fast path returns identical quotes.

```text
#java #concurrency #jdk27 #backend
```

## Script 3 — 4x faster loops (Vector API)

| Time | Visual | On-screen text |
| --- | --- | --- |
| 0–3 s | Scalar loop code, profiler flame | `Your hot loop is leaving 4x on the table` |
| 3–12 s | Scalar timeline vs vector timeline | `1024 floats: 0.857 µs → 0.216 µs` |
| 12–22 s | Species banner | `Species[float, 4, S_128_BIT] — 4 lanes` |
| 22–32 s | JMH summary table zoom | `65536 floats: 59.146 µs → 14.797 µs` |
| 32–40 s | Warning card | `Incubator: --add-modules jdk.incubator.vector` |
| 40–45 s | Repo URL card | `github.com/Baneeishaque/jdk-27-demo` |

**Caption text for the post:**

Vector API (12th incubator): dot product ~4x faster than scalar, measured with JMH.
Code + full benchmark report in bio.

```text
#java #performance #jdk27 #simd
```

## Posting notes

- 9:16, 1080×1920; keep on-screen text to one idea per cut — muted viewers read, not listen.
- Post Script 1 on launch day, Script 2 midweek, Script 3 the week after — stagger so each
  gets its own algorithmic window.
- Append `?utm_source=reels&utm_medium=social&utm_campaign=jdk27-launch` to the bio link.
- Reuse Script 2's code footage as the LinkedIn Post 2 video if you prefer video over image.
