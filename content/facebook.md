# Facebook post

One post for groups and the page. Conversational, no jargon walls — lead with the result,
put the link in the first comment (Facebook suppresses reach on link posts).

## Post

> JDK 27 shipped last month, and I spent the weekend putting it through its paces — not
> just reading the release notes, but measuring everything.
>
> The short version:
>
> - Four features are free wins: the G1 collector is now the default everywhere, object
>   headers got 25% smaller, TLS handshakes are post-quantum by default, and JFR recordings
>   redact passwords before they hit disk.
> - Five features change how you write code — structured concurrency, lazy constants,
>   primitive patterns, PEM key handling, and the Vector API.
>
> The numbers that surprised me most:
>
> - A two-int record now allocates 16 bytes instead of 24.
> - A dot product with the Vector API runs ~4x faster than the scalar loop.
> - An order-quoting pipeline with structured concurrency runs ~2.2x faster than
>   sequential — and the test proves both versions return identical results.
>
> I wrote every JEP up as a small, runnable demo with tests and benchmarks, and the repo
> also lists the gotchas the release notes skip. If your team is planning the JDK 27
> upgrade, this is a good place to start.
>
> Link in the comments.

**First comment:** `Repository (code + benchmarks + docs): https://github.com/Baneeishaque/jdk-27-demo?utm_source=facebook&utm_medium=social&utm_campaign=jdk27-launch`

## Posting notes

- Attach `shot-demo.png` — the terminal output is the hook.
- Post in 2–3 Java/backend groups, but space them an hour apart and answer every reply.
- Do not paste the link into the post body; keep it in the first comment.
