# LinkedIn posts

Three posts, one per article, one week apart. Each is self-contained; each links to its
article in the first comment.

## Post 1 — structured concurrency (week 1)

> Structured concurrency is on its seventh preview in JDK 27. I think this is the round
> where it stops being a preview feature and starts being a habit.
>
> The shape:
>
> `try (var scope = StructuredTaskScope.open()) {`
> `var price = scope.fork(() -> fetch("price"));`
> `var stock = scope.fork(() -> fetch("stock"));`
> `scope.join();`
> `}`
>
> The scope is the cancellation boundary. When anything fails, siblings are cancelled.
> That guarantee is the whole feature — `ExecutorService` never gave it to you.
>
> I measured it on a realistic pipeline (three service calls per quote): 570 ms sequential
> to 258 ms structured, ~2.2x — and the test asserts both paths return identical quotes.
>
> The article also documents the gotcha that cost me an hour: with the first-success-wins
> joiner, timeouts arrive wrapped in ExecutionException, so the obvious catch block never
> fires.
>
> Link in the comments.
>
> #Java #Concurrency #JDK27 #BackendEngineering

**First comment:** `Part 1 — Structured concurrency in JDK 27: https://medium.com/@baneeishaque/jdk-27-structured-concurrency?utm_source=linkedin&utm_medium=social&utm_campaign=jdk27-series`

## Post 2 — performance (week 2)

> Two of JDK 27's performance JEPs cost you nothing. This is the cheapest upgrade your
> service will get all year.
>
> - G1 is now the default collector in every environment — no more surprise Serial GC in
>   small containers.
> - Object headers shrank from 96 to 64 bits: a two-int record now allocates 16 bytes
>   instead of 24. Eight bytes off every live object.
>
> Measured, not quoted: the allocation numbers come from ThreadMXBean counters, and the
> test asserts the delta between the two layouts instead of hard-coding 16/24.
>
> And the third performance JEP — the Vector API — is still incubating and still ~4x on a
> dot product, tracking the SIMD lane count almost exactly (JMH, 2 forks).
>
> The article has the code, the raw numbers, and the audit items (if you use Unsafe to
> poke at headers, verify).
>
> Link in the comments.
>
> #Java #Performance #JDK27 #JVM

**First comment:** `Part 2 — JDK 27's free performance wins, measured: https://medium.com/@baneeishaque/jdk-27-performance-defaults?utm_source=linkedin&utm_medium=social&utm_campaign=jdk27-series`

## Post 3 — security (week 3)

> Three JDK 27 security changes, in order of how much code they delete:
>
> 1. Post-quantum TLS is on by default. `X25519MLKEM768` is the most-preferred group —
>    zero configuration. Clients send two key shares so old servers skip the retry
>    round-trip.
> 2. JFR redacts secrets before recordings reach disk. The showcase proves it by scanning
>    the raw recording for the secret and finding nothing.
> 3. The PEM API turns key serialization into one-liners, including encryption — and
>    deletes every hand-rolled BEGIN/END base64 wrapper in your codebase.
>
> The article covers the migration items: strict server group allow-lists, JFR parsing
> pipelines, and the two PEM sharp edges (textual constructor, no container encryption).
>
> That closes the three-part series — all links in the comments.
>
> #Java #Security #JDK27 #TLS

**First comment:** `Part 3 — JDK 27 security: PQ TLS, JFR redaction, PEM: https://medium.com/@baneeishaque/jdk-27-security-crypto?utm_source=linkedin&utm_medium=social&utm_campaign=jdk27-series`

## Posting notes

- One post per week, Tuesday–Thursday morning, matching the article release.
- Attach `shot-jmh.png` to Post 2; the others need no image.
- Reply to comments within two hours; the article link lives in the first comment only.
