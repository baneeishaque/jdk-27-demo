# Facebook posts

Three posts, one per article, one week apart. Link in the first comment of each.

## Post 1 — structured concurrency (week 1)

> JDK 27's structured concurrency is on its seventh preview, and I think it is finally
> ready for production spikes.
>
> The idea: one scope per request, one fork per remote call, and when anything fails the
> scope cancels the siblings. That last part is the guarantee ExecutorService never gave
> us — no more leaked tasks when a request dies.
>
> I measured it on a realistic pipeline: three service calls per quote, 570 ms sequential
> vs 258 ms structured. About 2.2x — and the test proves both versions return identical
> quotes.
>
> Full deep dive (including a timeout gotcha that cost me an hour) in the comments.
>
> #Java #Concurrency #JDK27

**First comment:** `Part 1 of the JDK 27 series: https://medium.com/@baneeishaque/jdk-27-structured-concurrency?utm_source=facebook&utm_medium=social&utm_campaign=jdk27-series`

## Post 2 — performance (week 2)

> Two of JDK 27's performance upgrades are free — no code changes at all.
>
> - G1 is now the default garbage collector in every environment (no more surprise
>   collector switches in small containers).
> - Object headers shrank 25%: a two-int record now allocates 16 bytes instead of 24.
>
> The third one, the Vector API, is still incubating but still runs ~4x faster than scalar
> loops on a dot product (measured with JMH).
>
> The deep dive with raw numbers and an upgrade checklist is in the comments.
>
> #Java #Performance #JDK27

**First comment:** `Part 2 of the JDK 27 series: https://medium.com/@baneeishaque/jdk-27-performance-defaults?utm_source=facebook&utm_medium=social&utm_campaign=jdk27-series`

## Post 3 — security (week 3)

> Three JDK 27 security changes worth knowing about:
>
> 1. Post-quantum TLS is on by default — quantum-resistant handshakes with zero
>    configuration.
> 2. JFR recordings now redact secrets before they reach disk (I verified by scanning a
>    raw recording for a planted password — it was absent).
> 3. The new PEM API turns key handling into one-liners, including encryption.
>
> That closes my three-part JDK 27 series — all three deep dives linked in the comments.
>
> #Java #Security #JDK27

**First comment:** `Part 3 (and links to parts 1 and 2): https://medium.com/@baneeishaque/jdk-27-security-crypto?utm_source=facebook&utm_medium=social&utm_campaign=jdk27-series`

## Posting notes

- Attach `shot-demo.png` to Post 1, `shot-jmh.png` to Post 2, `shot-tls.png` to Post 3.
- Space group posts an hour apart; answer every reply.
- Keep links in the comments — Facebook demotes link posts.
