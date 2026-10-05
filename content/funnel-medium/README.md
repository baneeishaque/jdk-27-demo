# Medium funnel kit

Traffic funnel for the **flagship maximum-detail Medium deep dive** —
[`medium-article.md`](medium-article.md) — which lives separately from the
repository-announcement kit in the parent [`content/`](../README.md) folder.

Everything here points readers at one destination: the Medium article. The
article then funnels readers to the repository.

## Asset map

| File | Platform | Format | Length |
| --- | --- | --- | --- |
| [`medium-article.md`](medium-article.md) | Medium | Flagship deep dive: all 9 JEPs, code, measurements | ~7,000 words |
| [`community-blog-post.md`](community-blog-post.md) | Community blog | Companion post, warmer voice | ~1,200 words |
| [`linkedin.md`](linkedin.md) | LinkedIn | Two posts: hook + engineering story | 120–200 words |
| [`x-thread.md`](x-thread.md) | X / Twitter | 10-post thread | 280 chars/post |
| [`facebook.md`](facebook.md) | Facebook | Group/Page post | ~170 words |
| [`whatsapp.md`](whatsapp.md) | WhatsApp | React-group messages + etiquette | 60–120 words |
| [`instagram.md`](instagram.md) | Instagram | 8-slide carousel + Reel caption | Slides + caption |
| [`snapchat.md`](snapchat.md) | Snapchat | Spotlight script | ~35 seconds |
| [`reels.md`](reels.md) | Reels / Shorts / TikTok | Three short-form scripts | 30–45 s each |

## Publishing order

1. **Publish the Medium article first** — every asset below links to it, so it
   must resolve before anything else goes out.
2. **Community blog post** the same day (it is the soft-launch companion; it
   links to the Medium piece as "the full write-up").
3. **LinkedIn post 1** the next morning; post 2 two to three days later.
4. **X thread** the same day as LinkedIn post 1, a few hours later.
5. **Reels + Instagram carousel + Snapchat** midweek, all in one push — the
   short-form video drives the most cold traffic.
6. **Facebook** the day after the short-form push.
7. **WhatsApp React groups** last and least often — treat it as a favour to
   communities, not a broadcast channel.

## Conventions

- **Medium URL placeholder** used throughout:
  `https://medium.com/@baneeishaque/jdk-27-jep-by-jep`.
  If the published URL differs, find-and-replace it in every file here
  **before** posting anything.
- **UTM tags**: append
  `?utm_source=<platform>&utm_medium=social&utm_campaign=jdk27-medium`
  to Medium links. The community blog post uses
  `utm_source=community-blog&utm_medium=referral`.
- **Repo URL** (used inside the Medium article and the community post):
  `https://github.com/Baneeishaque/jdk-27-demo`.
- **Verified numbers** quoted here: 9 JEPs, 33 unit tests, ~4.0x Vector API speedup
  (JMH, 2 forks), 24 → 16 bytes per object with compact headers, 570 ms → 258 ms
  order pipeline (~2.2x), 37 subtitle languages, and 20 narration tracks. Re-check
  against `./mvnw verify`, `scripts/bench-vector.sh`, and
  `content/youtube/scenes.json` before posting if the project changes.
- **Screenshots** for carousel/short-form assets: the demo terminal output, the
  JMH summary, the TLS group list, and the compact-headers comparison. Suggested
  filenames: `shot-demo.png`, `shot-jmh.png`, `shot-tls.png`, `shot-headers.png`.
