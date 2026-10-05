# LinkedIn posts

Two posts driving traffic to the Medium deep dive. Post 1 hooks; Post 2 tells the
engineering story behind one section of the article. Link in the first comment.

## Post 1 — the hook (publish day 1)

> I measured every JEP in JDK 27 so your team does not have to.
>
> Nine JEPs. Four are final and free: G1 is now the default collector everywhere, object
> headers shrank 25%, TLS is post-quantum by default, and JFR redacts secrets before they
> hit disk.
>
> The other five are previews and incubators — structured concurrency, lazy constants,
> primitive patterns, PEM encodings, and the Vector API — and each one now has a number
> attached:
>
> - Vector API dot product: ~4.0x faster than scalar (JMH, 2 forks)
> - Order pipeline with structured fan-out: 570 ms to 258 ms
> - Object header: 24 to 16 bytes per two-int record
> - 33 tests, all green, on JDK 27+35
>
> I wrote the maximum-detail version — API shapes, exception chains, measurements, and a
> ten-item gotcha catalogue — as a JEP-by-JEP guide.
>
> Link to the full article in the comments.
>
> #Java #JDK27 #Performance #Concurrency #SoftwareEngineering

**First comment:** `JDK 27, JEP by JEP — the maximum-detail guide: https://medium.com/@baneeishaque/jdk-27-jep-by-jep?utm_source=linkedin&utm_medium=social&utm_campaign=jdk27-medium`

## Post 2 — the engineering story (day 3–4)

> The best bug I hit while measuring JDK 27 was in the error path.
>
> I was testing structured concurrency's timeout behaviour. The scope was configured with
> a 300 ms timeout. The task slept for 2 seconds. I caught CancelledByTimeoutException.
>
> Nothing happened. No catch, no log, no test failure — the exception just sailed past.
>
> The reason: with the "first success wins" joiner, a scope timeout arrives wrapped. You
> get an ExecutionException whose cause is CancelledByTimeoutException. Catching the cause
> directly never fires.
>
> It is exactly the kind of detail the release notes skip and code review misses — the
> happy path works, the tests are green, and the timeout handler is dead code.
>
> I catalogued ten gotchas like this one while building the showcase, each with a test
> that proves it: lazy-constant failure permanence, the JFR RecordingFile API change, the
> new JSON-style JSSE debug trace, PEM's textual-content constructor, and more.
>
> The full guide is on Medium — link in the comments.
>
> #Java #Concurrency #JDK27 #BackendEngineering

**First comment:** `Full article (ten gotchas included): https://medium.com/@baneeishaque/jdk-27-jep-by-jep?utm_source=linkedin&utm_medium=social&utm_campaign=jdk27-medium`

## Posting notes

- Post 1 Tuesday–Thursday morning; Post 2 the same slot 2–3 days later.
- Attach `shot-jmh.png` to Post 1 (numbers are the hook); Post 2 needs no image.
- Reply to every comment in the first two hours; the Medium link lives there, not in the
  post body.
