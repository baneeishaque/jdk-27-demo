# LinkedIn posts

Two posts, published 2–3 days apart. The first announces; the second teaches. Plain text —
LinkedIn rewards text posts over link posts, so put the link in the first comment.

## Post 1 — announcement (publish day 1)

> JDK 27 shipped nine JEPs last month. Most teams will read the release notes, nod, and
> change nothing.
>
> That is fine for four of them — they are free. G1 is now the default collector everywhere.
> Object headers are 25% smaller. TLS 1.3 handshakes are post-quantum by default. JFR
> redacts secrets before they reach disk.
>
> The other five change how you write code: structured concurrency, lazy constants,
> primitive patterns, PEM encodings, and the Vector API.
>
> I built a showcase that treats every JEP as a claim to verify — runnable code, a test that
> proves the behavior, and a benchmark that puts a number on it.
>
> Numbers from the run:
>
> - Vector API dot product: ~4.0x faster than scalar (JMH, 2 forks)
> - Order pipeline with structured fan-out: 570 ms to 258 ms (~2.2x)
> - Object header: 24 to 16 bytes per two-int record
> - 33 tests, all green, on JDK 27+35
>
> The repository also documents the gotchas the release notes skip — like scope timeouts
> arriving wrapped in ExecutionException, and LazyConstant failures being permanent.
>
> Link in the comments.
>
> #Java #JDK27 #Performance #Concurrency #SoftwareEngineering

**First comment:** `Code + docs: https://github.com/Baneeishaque/jdk-27-demo?utm_source=linkedin&utm_medium=social&utm_campaign=jdk27-launch`

## Post 2 — technical deep dive (day 3–4)

> The feature I have waited years for is structured concurrency. JDK 27 is its seventh
> preview — and the API finally reads like the mental model.
>
> The old shape made you manage the deadline yourself:
>
> `scope.joinUntil(Instant.now().plusSeconds(1));`
>
> JDK 27 moved timeouts into configuration:
>
> `try (var scope = StructuredTaskScope.open(Joiner.<String>anySuccessfulOrThrow(),`
> `cfg -> cfg.withTimeout(Duration.ofMillis(300)))) { ... }`
>
> Three things that bit me while building the showcase — all documented in the repo:
>
> 1. A scope timeout does NOT throw CancelledByTimeoutException. It arrives as an
>    ExecutionException whose cause is CancelledByTimeoutException.
> 2. When one subtask fails, join() throws with the original cause and siblings are
>    cancelled — the scope is the cancellation boundary.
> 3. anySuccessfulOrThrow needs an explicit type witness:
>    `Joiner.<String>anySuccessfulOrThrow()`.
>
> I measured the payoff in a realistic shape — an order pipeline fanning out price, stock,
> and shipping calls: 570 ms sequential vs 258 ms structured. Same quotes, provably: the
> test asserts both modes return identical results.
>
> Full write-up in the repo.
>
> #Java #Concurrency #JDK27 #BackendEngineering

**First comment:** `Full case study: https://github.com/Baneeishaque/jdk-27-demo/blob/main/content/blog-post.md?utm_source=linkedin&utm_medium=social&utm_campaign=jdk27-launch`

## Posting notes

- Post 1 between 08:00–10:00 on a Tuesday–Thursday; Post 2 the same slot 2–3 days later.
- Attach one image to each: Post 1 → `shot-demo.png`; Post 2 → `shot-jmh.png` (the benchmark
  table is the visual).
- Do not edit the post in the first hour — edits suppress reach.
- Reply to every comment in the first two hours; that is where the distribution comes from.
