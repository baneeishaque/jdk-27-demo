# Three-article Medium series kit

A three-article deep-dive series on JDK 27, each article focused on one theme, each with
its own cross-platform push. This kit is separate from the flagship funnel
([`../funnel-medium/`](../funnel-medium/README.md)) — same repository, different editorial
angle: narrow and deep instead of one maximum-detail guide.

## The series

| # | Article | Theme | Medium URL placeholder |
| --- | --- | --- | --- |
| 1 | [`medium-structured-concurrency.md`](medium-structured-concurrency.md) | JEP 533 — structured concurrency in production shape | `https://medium.com/@baneeishaque/jdk-27-structured-concurrency` |
| 2 | [`medium-performance-defaults.md`](medium-performance-defaults.md) | JEP 523 + 534 + 537 — the free wins, measured | `https://medium.com/@baneeishaque/jdk-27-performance-defaults` |
| 3 | [`medium-security-crypto.md`](medium-security-crypto.md) | JEP 527 + 536 + 538 — PQ TLS, JFR redaction, PEM | `https://medium.com/@baneeishaque/jdk-27-security-crypto` |

## Asset map

| File | Platform | Format | Length |
| --- | --- | --- | --- |
| [`community-blog-post.md`](community-blog-post.md) | Community blog | Series companion post | ~1,200 words |
| [`linkedin.md`](linkedin.md) | LinkedIn | Three posts, one per article | 120–200 words each |
| [`x-thread.md`](x-thread.md) | X / Twitter | Three mini-threads (6 posts each) | 280 chars/post |
| [`facebook.md`](facebook.md) | Facebook | One post per article | ~170 words each |
| [`whatsapp.md`](whatsapp.md) | WhatsApp | Series broadcast + Status | 60–120 words |
| [`instagram.md`](instagram.md) | Instagram | Three carousels (6 slides each) + captions | Slides + caption |
| [`snapchat.md`](snapchat.md) | Snapchat | Three Spotlight scripts | ~35 seconds each |
| [`reels.md`](reels.md) | Reels / Shorts / TikTok | Three short-form scripts | 30–45 s each |

## Publishing order

Publish on a weekly cadence — one article per week, each with its own push wave. A
three-week drip keeps the series visible without burning the audience at once.

1. **Week 1 — Article 1 (structured concurrency).** Publish Tuesday. Community blog post
   and LinkedIn post 1 the same day; X mini-thread 1 the next morning; Reel 1 + Instagram
   carousel 1 midweek; Facebook and WhatsApp at the end of the week.
2. **Week 2 — Article 2 (performance).** Same pattern, shifted a week. LinkedIn post 2
   references article 1 as "last week's deep dive".
3. **Week 3 — Article 3 (security).** Same pattern. The final WhatsApp broadcast links all
   three articles as the complete series.

Every asset links to its own article first; the community post and the closing WhatsApp
broadcast link the series index.

## Conventions

- **Medium URL placeholders** are in the series table above. If the published URLs differ,
  find-and-replace in every file **before** posting anything.
- **UTM tags**: append `?utm_source=<platform>&utm_medium=social&utm_campaign=jdk27-series`
  to Medium links. The community blog post uses
  `utm_source=community-blog&utm_medium=referral`.
- **Repo URL** (used inside the articles and the community post):
  `https://github.com/Baneeishaque/jdk-27-demo`.
- **Verified numbers** quoted here: 9 JEPs, 33 unit tests, ~4.0x Vector API speedup (JMH,
  2 forks), 24 → 16 bytes per object, 570 ms → 258 ms order pipeline (~2.2x), 37 subtitle
  languages, 20 narration tracks. Re-check against `./mvnw verify`,
  `scripts/bench-vector.sh`, and `content/youtube/scenes.json` before posting.
- **Screenshots**: `shot-demo.png`, `shot-jmh.png`, `shot-tls.png`, `shot-headers.png` —
  same asset names as the flagship funnel; shoot once, reuse across kits.
