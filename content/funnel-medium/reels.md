# Reels / Shorts / TikTok

Three vertical scripts driving traffic to the Medium deep dive. Same footage works on
Instagram Reels, YouTube Shorts, and TikTok; captions burned in, hooks in the first two
seconds.

## Script 1 — JDK 27 measured in 45 seconds

| Time | Visual | On-screen text |
| --- | --- | --- |
| 0–3 s | `run-demo.sh` output | `JDK 27: 9 JEPs, measured` |
| 3–10 s | Four checkmarks | `G1 default · headers -25% · PQ TLS · JFR redaction` |
| 10–17 s | Two boxes, one slimmer | `24 → 16 bytes per object` |
| 17–25 s | Parallel timeline bars | `570 ms → 258 ms` |
| 25–33 s | JMH table | `Vector API: ~4.0x` |
| 33–40 s | Gotcha card | `10 gotchas the release notes skip` |
| 40–45 s | Article card | `Full guide — link in bio` |

**Caption text for the post:**

JDK 27, measured: 9 JEPs, 33 tests, real benchmarks. Full JEP-by-JEP guide in bio.

```text
#java #jdk27 #programming #coding #softwareengineer
```

## Script 2 — the timeout that never fires

| Time | Visual | On-screen text |
| --- | --- | --- |
| 0–4 s | Code: scope with 300 ms timeout | `This catch block is dead code` |
| 4–12 s | `catch (CancelledByTimeoutException e)` greyed out | `Catching the timeout directly never fires` |
| 12–22 s | Exception chain animates | `ExecutionException → CancelledByTimeoutException` |
| 22–32 s | Test output showing unwrap | `Unwrap first: cause() instanceof CancelledByTimeoutException` |
| 32–40 s | Ten-gotcha list scrolling | `10 gotchas, each with a test` |
| 40–45 s | Article card | `Full guide — link in bio` |

**Caption text for the post:**

The structured-concurrency timeout gotcha: it arrives wrapped in ExecutionException. One
of ten JDK 27 gotchas, each with a test. Full guide in bio.

```text
#java #concurrency #jdk27
```

## Script 3 — 4x loops, honestly measured

| Time | Visual | On-screen text |
| --- | --- | --- |
| 0–3 s | Scalar loop + profiler flame | `Your hot loop is leaving 4x on the table` |
| 3–12 s | Scalar vs vector timeline | `1,024 floats: 0.857 µs → 0.216 µs` |
| 12–20 s | Species banner | `Species[float, 4, S_128_BIT]` |
| 20–30 s | JMH summary zoom | `65,536 floats: 59.146 µs → 14.797 µs` |
| 30–38 s | Warning card | `Incubator: --add-modules jdk.incubator.vector` |
| 38–45 s | Article card | `Full guide — link in bio` |

**Caption text for the post:**

Vector API (12th incubator): ~4x on dot products, measured with JMH. Full JDK 27 guide
in bio.

```text
#java #performance #jdk27 #simd
```

## Posting notes

- 9:16, 1080×1920; one idea per cut — muted viewers read.
- Post Script 1 launch day, Script 2 midweek, Script 3 the week after.
- Bio link: `https://medium.com/@baneeishaque/jdk-27-jep-by-jep?utm_source=reels&utm_medium=social&utm_campaign=jdk27-medium`.
