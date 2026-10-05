# Medium adaptation

Platform notes for publishing the canonical article on Medium.

## Metadata

- **Title:** JDK 27: all nine JEPs, measured
- **Subtitle:** G1 everywhere, compact headers, post-quantum TLS, structured concurrency,
  and the gotchas the release notes don't mention — with real numbers
- **Tags (max 5):** `java`, `jdk27`, `performance`, `concurrency`, `software-engineering`
- **Canonical link:** set Medium's "Import a story" flow (or
  Settings → Publishing → Import) with your blog URL so the canonical tag points at the
  original. If publishing directly, add at the top: `Originally published at <blog-url>`.

## Publication checklist

1. Import via **medium.com/p/import** using the blog URL — Medium keeps formatting and sets
   the canonical automatically.
2. Add the subtitle in the post's subtitle field (not as body text).
3. Verify the five tags are set exactly as above.
4. Replace the repository URL if it changed (find-and-replace
   `https://github.com/Baneeishaque/jdk-27-demo`).
5. Insert the terminal captures at the marked points below before publishing.
6. Publish, then paste the Medium URL back into the blog post as a "Also on Medium" line.

## Body

Use `blog-post.md` verbatim, with these Medium-specific adjustments:

- **Hero image** after the title (Medium shows a full-width lead image): use
  `shot-demo.png` — the full `run-demo.sh` terminal output.
- **Screenshot break 1** after the compact-headers table — insert `shot-headers.png` (the
  16 vs 24 bytes terminal capture) with the caption: *"Same record, same JVM, one flag:
  the header shrank by 8 bytes."*
- **Screenshot break 2** after the Vector API table — insert `shot-jmh.png` (the JMH
  summary) with the caption: *"JMH on Apple Silicon: the SIMD path tracks the 4-lane width
  almost exactly."*
- **Screenshot break 3** after the pipeline table — insert `shot-tls.png` (the TLS group
  list from the demo) with the caption: *"X25519MLKEM768 first in the default group list —
  post-quantum protection with zero configuration."*
- **Code blocks**: Medium renders fenced blocks as preformatted text; the Java snippets and
  terminal captures survive intact. Long lines may wrap — acceptable.
- **Tables**: Medium does not render markdown tables. Convert the four tables to images
  (screenshot) or bullet lists before importing; the JMH table is worth an image.

## Cross-promotion

- Add a footer line: *"This article is part of a full content kit — the repository's
  `content/` directory contains the LinkedIn, X, and short-form versions, plus a
  three-article deep-dive series."*
- Do not enable the Medium paywall for this piece; the goal is reach.
- After 48 hours, check the Medium stats and note the traffic split vs the canonical — if
  Medium outperforms by more than 3x, consider making it the canonical instead and point the
  blog at it.
