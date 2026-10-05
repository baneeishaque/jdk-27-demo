# Instagram

Three carousels (six slides each), one per article, one week apart. Bio link carries the
UTM tag; captions cannot.

## Carousel 1 — structured concurrency (week 1)

**Slide 1:** `Structured concurrency is on its 7th preview in JDK 27.` + visual: scope code.

**Slide 2:** `One scope per request. One fork per remote call.` + visual: three bars fanning
out.

**Slide 3:** `570 ms sequential → 258 ms structured` + visual: timeline comparison.

**Slide 4:** `The test proves both paths return identical quotes.` + visual: test output.

**Slide 5:** `Gotcha: the timeout arrives wrapped in ExecutionException.` + visual:
exception chain.

**Slide 6:** `Full deep dive — link in bio.` + visual: article card.

Caption: `Structured concurrency in JDK 27: 2.2x on a real pipeline, proven identical
results. Full deep dive in bio. #java #concurrency #jdk27 #backend #programming`

## Carousel 2 — performance (week 2)

**Slide 1:** `JDK 27's free performance wins.` + visual: demo terminal.

**Slide 2:** `G1 is now the default GC everywhere. Zero config.` + visual: flag output.

**Slide 3:** `Object headers: 24 → 16 bytes per two-int record.` + visual: two boxes.

**Slide 4:** `Vector API: 59.146 µs → 14.797 µs (65,536 floats).` + visual: JMH table.

**Slide 5:** `~4.0x — exactly the 4-lane width.` + visual: species banner.

**Slide 6:** `Full article — link in bio.` + visual: article card.

Caption: `JDK 27 free performance wins: headers -25%, G1 everywhere, Vector API ~4x.
Measured, not quoted. Full article in bio. #java #performance #jdk27 #jvm #programming`

## Carousel 3 — security (week 3)

**Slide 1:** `Three JDK 27 security changes.` + visual: lock + circuit.

**Slide 2:** `Post-quantum TLS is on by default. X25519MLKEM768.` + visual: group list.

**Slide 3:** `JFR redacts secrets before recordings reach disk.` + visual: `[[REDACTED]]`
output.

**Slide 4:** `I scanned the raw recording for the secret. Absent.` + visual: scan output.

**Slide 5:** `PEM API: keys in one line, encryption included.` + visual: code snippet.

**Slide 6:** `Series complete — all three deep dives in bio.` + visual: three article
cards.

Caption: `JDK 27 security: PQ TLS by default, JFR redaction proven by raw-recording scan,
PEM one-liners. Series complete — links in bio. #java #security #jdk27 #tls #programming`

## Posting notes

- One carousel per week, Tuesday–Thursday 11:00–13:00, matching the article release.
- Bio link per week: the week's article URL with
  `?utm_source=instagram&utm_medium=social&utm_campaign=jdk27-series`.
- Slide 1 must read at thumbnail size — short lines, big type.
