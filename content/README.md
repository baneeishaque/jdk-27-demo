# Content kit

Everything needed to present this project: one canonical long-form article,
platform-specific adaptations, and a multilingual video pipeline.

## Asset map

| File | Platform | Format | Length |
| --- | --- | --- | --- |
| [`blog-post.md`](blog-post.md) | Own blog / dev.to / Hashnode | Canonical article (source of truth) | ~2,000 words |
| [`medium.md`](medium.md) | Medium | Adapted article + metadata | ~1,500 words |
| [`linkedin.md`](linkedin.md) | LinkedIn | Two posts: announcement + deep dive | 130–220 words |
| [`x-thread.md`](x-thread.md) | X / Twitter | 12-post thread | 280 chars/post |
| [`facebook.md`](facebook.md) | Facebook | Group/Page post | ~180 words |
| [`whatsapp.md`](whatsapp.md) | WhatsApp | Community broadcast + Status | 60–120 words |
| [`instagram.md`](instagram.md) | Instagram | 10-slide carousel + Reel caption | Slides + caption |
| [`snapchat.md`](snapchat.md) | Snapchat | Spotlight script | 35 seconds |
| [`reels.md`](reels.md) | Reels / Shorts / TikTok | Three short-form scripts | 30–45 s each |
| [`youtube/`](youtube/) | YouTube | Full video pipeline (37 locales) | ~3 minutes |
| [`funnel-medium/`](funnel-medium/README.md) | Medium + social | Flagship funnel to the maximum-detail deep dive | 10 assets |
| [`funnel-medium-2/`](funnel-medium-2/README.md) | Medium + social | Three-article series + per-article funnels | 12 assets |

## Publishing order

1. **Publish the repository first** — every post links to it.
2. **Blog post** on the canonical domain (or dev.to/Hashnode), then syndicate to Medium with a
   `rel=canonical` pointing at the original.
3. **LinkedIn announcement** the same day; the deep-dive post 2–3 days later.
4. **X thread** the next morning; quote-post the best benchmark table.
5. **Instagram carousel + Reels** midweek; Snapchat Spotlight the same day as Reels.
6. **Facebook** and **WhatsApp** last — they reach people who already know you; give them the
   polished artifact, not the announcement.
7. **YouTube video** whenever the multilingual cut is ready; it is the only asset that shows
   all 37 locales in one artifact.

The flagship funnel targets the **maximum-detail Medium deep dive** instead of the
repository: see [`funnel-medium/`](funnel-medium/README.md) for the article, the community
blog companion, and the social adaptations that drive traffic to it.

The series kit publishes a three-article Medium series (structured concurrency in
production, performance defaults, and security & crypto), each article with its own
cross-platform push: [`funnel-medium-2/`](funnel-medium-2/README.md).

## Conventions

- **Repo URL** used throughout: `https://github.com/Baneeishaque/jdk-27-demo`.
  If the published URL differs, find-and-replace before posting.
- **UTM tags**: append `?utm_source=<platform>&utm_medium=social&utm_campaign=jdk27-launch`
  to links (the blog post uses `utm_source=blog&utm_medium=referral`).
- **Numbers** quoted in the kit (9 JEPs, 33 tests, ~4.0x vector speedup, 16 vs 24 bytes per
  `Point`, ~2.2x order pipeline speedup, 37 video locales) are verified by `./mvnw test`
  and the scripts under `scripts/`; re-check before posting if the project changes.
- **Screenshots** for carousel/X posts: the demo terminal output, the JMH summary table,
  the TLS group list, and the compact-headers comparison. Suggested filenames:
  `shot-demo.png`, `shot-jmh.png`, `shot-tls.png`, `shot-headers.png`.

## Video pipeline (YouTube)

`youtube/` contains a scripted pipeline rather than a finished upload:

- `translations/<locale>.json` — per-locale slide titles and narrations (37 files; the
  community-reviewable layer).
- `build-scenes.py` — owns scene order, on-screen code, the locale roster, and voices;
  assembles `scenes.json`.
- `scenes.json` — generated single source of truth consumed by the renderer and builder.
- `render-slides.mjs` — renders slide images with headless Chromium (per-script fonts, RTL,
  Nastaliq line boxes).
- `build-video.py` — generates narration with macOS `say`, assembles per-locale videos, muxes
  the master multi-audio MKV (20 audio + 37 subtitle tracks), and exports subtitles.
- `subtitles/` — generated SRT/VTT files for all 37 locales, committed as deliverables.
- `metadata.md` — titles, descriptions, tags, and chapters per locale.

See [`youtube/README.md`](youtube/README.md) for the full pipeline, the locale roster, and
the 17 subtitle-only locales.
