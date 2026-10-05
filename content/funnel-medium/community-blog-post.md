# Community blog post

Companion to the Medium deep dive, for the community site. Warmer voice, fewer code
blocks — it tells the story of the measurement weekend and links to the article as "the
full write-up".

## Post

## JDK 27 shipped nine JEPs. I spent the weekend measuring them — here is what I found

Every six months a new JDK lands, and every six months the same thing happens: the release
notes get skimmed, the team nods, and nothing changes. This time I decided to do the
opposite. I took JDK 27's nine JEPs, built a small demo for each one, wrote a test that
proves the behaviour, and ran benchmarks until the claims had numbers attached.

Here is the honest summary.

**The four free wins.** Four of the nine JEPs are final, and they need zero code changes:
G1 is now the default collector in every environment (no more surprise Serial GC on small
containers), object headers shrank from 96 to 64 bits, TLS 1.3 offers post-quantum key
exchange by default, and JFR redacts secrets before a recording reaches disk. My favourite
measurement: a two-int record now allocates 16 bytes instead of 24. Eight bytes, every
object, for free.

**The one I have waited years for.** Structured concurrency is still a preview (seventh
round), but the API finally reads like the mental model. One scope per request, one fork
per remote call, and when anything fails the scope cancels the siblings — the guarantee
`ExecutorService` never gave us. I measured it on a realistic shape: an order pipeline
fanning out three service calls per quote. Sequential: about 570 ms for four orders.
Structured: about 258 ms. The test asserts both versions return identical quotes, so the
fast path is provably correct, not just faster.

There is one gotcha that cost me an hour and that the release notes do not mention: with
the "first success wins" joiner, a timeout does not throw `CancelledByTimeoutException` —
it arrives wrapped in an `ExecutionException`. Your catch block silently never fires.
That, and nine more gotchas, are written up with tests in the repository.

**The previews worth spiking.** Lazy constants make the holder-class singleton a
first-class API — with a subtlety: failure is permanent, so retries must live inside the
supplier. Primitive patterns convert only when the conversion is exact, which deletes a
whole genre of range-check code. And the PEM API turns key handling into one-liners,
including encryption.

**The incubator that keeps paying.** The Vector API is on its twelfth round, and the
numbers are boring in the best way: a dot product runs ~4x faster than the scalar loop,
tracking the four SIMD lanes almost exactly. Boring is what you want from SIMD.

Everything — nine demos, 33 tests, the JMH reports, and line-by-line explainers — is in
one repository, linked below. If your team is planning the JDK 27 upgrade, start with the
migration checklist in the README; four of the features cost nothing at all.

The full write-up, with the API shapes, the exception chains, and the complete gotcha
catalogue, is on Medium: **JDK 27, JEP by JEP: the maximum-detail guide** (link below).
Numbers were measured on Apple Silicon with OpenJDK 27 build `27+35`; run them on your
hardware before quoting them in a planning meeting.

***

*Repository: `https://github.com/Baneeishaque/jdk-27-demo`*
*Full write-up: `https://medium.com/@baneeishaque/jdk-27-jep-by-jep`*

## Posting notes

- Publish the same day as the Medium article, but a few hours later — the blog post is the
  warm-up, the article is the destination.
- The two bold links at the end are the only links; keep them on their own lines.
- Append `?utm_source=community-blog&utm_medium=referral&utm_campaign=jdk27-medium` to the
  Medium link when posting.
