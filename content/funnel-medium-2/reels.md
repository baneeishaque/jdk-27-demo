# Reels / Shorts / TikTok

Three vertical scripts, one per article, one week apart. Same footage works on Instagram
Reels, YouTube Shorts, and TikTok; captions burned in, hooks in the first two seconds.

## Script 1 — structured concurrency (week 1)

| Time | Visual | On-screen text |
| --- | --- | --- |
| 0–4 s | Code editor: one method, three fetches | `This pattern leaks threads. This one doesn't.` |
| 4–12 s | Sequential timeline bars | `Sequential: 130 ms per quote` |
| 12–20 s | Parallel bars fanning out | `Structured: 60 ms per quote` |
| 20–30 s | Full pipeline result | `570 ms → 258 ms (~2.2x)` |
| 30–38 s | Test output | `Identical results — proven by test` |
| 38–45 s | Article card | `Full deep dive — link in bio` |

**Caption text for the post:**

Structured concurrency in JDK 27: 2.2x on a real order pipeline, identical results proven
by test. Part 1 of the series.

```text
#java #concurrency #jdk27 #programming
```

## Script 2 — performance (week 2)

| Time | Visual | On-screen text |
| --- | --- | --- |
| 0–4 s | Terminal | `2 JDK 27 upgrades. 0 code changes.` |
| 4–12 s | Flag output | `G1 is now the default GC everywhere` |
| 12–20 s | Two boxes, one slimmer | `24 → 16 bytes per object` |
| 20–30 s | JMH summary | `Vector API: ~4.0x` |
| 30–38 s | Upgrade checklist | `Upgrade. Re-run tests. Done.` |
| 38–45 s | Article card | `Full article — link in bio` |

**Caption text for the post:**

JDK 27's free performance wins: G1 everywhere, headers 24→16 bytes, Vector API ~4x.
Measured. Part 2 of the series.

```text
#java #performance #jdk27 #jvm
```

## Script 3 — security (week 3)

| Time | Visual | On-screen text |
| --- | --- | --- |
| 0–4 s | Lock + circuit | `3 JDK 27 security changes` |
| 4–12 s | TLS group list | `Post-quantum TLS — zero config` |
| 12–20 s | Raw recording scan | `Secret in recording: absent` |
| 20–28 s | PEM one-liner | `Keys in one line — encryption included` |
| 28–36 s | Three article cards | `Three-part series complete` |
| 36–45 s | Article card | `Full series — link in bio` |

**Caption text for the post:**

JDK 27 security: PQ TLS by default, JFR redaction verified by raw scan, PEM one-liners.
Series complete.

```text
#java #security #jdk27 #tls
```

## Posting notes

- 9:16, 1080×1920; one idea per cut — muted viewers read.
- One Reel per week, midweek, matching the article release.
- Bio link: the week's article with
  `?utm_source=reels&utm_medium=social&utm_campaign=jdk27-series`.
