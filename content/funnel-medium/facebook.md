# Facebook post

One post for groups and the page, driving traffic to the Medium deep dive. Link in the
first comment.

## Post

> JDK 27 is out, and instead of skimming the release notes I measured all nine JEPs —
> runnable demos, tests, and benchmarks.
>
> The four free wins: G1 is now the default collector everywhere, object headers shrank
> 25%, TLS is post-quantum by default, and JFR redacts secrets before they hit disk.
>
> The numbers that surprised me:
>
> - A two-int record allocates 16 bytes instead of 24.
> - An order-quoting pipeline with structured concurrency runs ~2.2x faster than
>   sequential — and the test proves both versions return identical results.
> - A dot product with the Vector API runs ~4x faster than the scalar loop.
>
> I also catalogued ten gotchas the release notes skip — like scope timeouts arriving
> wrapped in ExecutionException, and lazy-constant failures being permanent.
>
> The full JEP-by-JEP guide is on Medium. Link in the comments.

**First comment:** `JDK 27, JEP by JEP — the maximum-detail guide: https://medium.com/@baneeishaque/jdk-27-jep-by-jep?utm_source=facebook&utm_medium=social&utm_campaign=jdk27-medium`

## Posting notes

- Attach `shot-demo.png` — terminal output is the scroll-stopper.
- Space group posts an hour apart; answer every reply with substance, not "thanks".
- Keep the link in the comment; Facebook demotes link posts.
