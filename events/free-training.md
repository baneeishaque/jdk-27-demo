# Free training playbook — Modern Java with JDK 27

A half-day (4-hour) free training for the community: from JDK 17-era habits to JDK 27
idioms, built entirely on the showcase repository. Run monthly or quarterly; recording
published to the community channel.

## Goal

- Lift the community's baseline: participants leave knowing what changed between JDK 17
  and JDK 27 and what to adopt first.
- Feed the paid workshop funnel: the last 15 minutes pitch the deep-dive workshop.

## Audience

- Java developers with ~2 years of experience; teams considering a JDK upgrade.
- No JDK 27 knowledge assumed.

## Curriculum (4 hours, with breaks)

| Time | Module | Content |
| --- | --- | --- |
| 0:00–0:30 | Why upgrade | The four final JEPs; what a real migration costs (nothing for two of them) |
| 0:30–1:15 | Structured concurrency | Mental model, API, live order pipeline; the wrapped-timeout gotcha |
| 1:15–1:30 | Break | — |
| 1:30–2:15 | Lazy constants + primitive patterns | Holder-class replacement; failure permanence; exactness-driven matching |
| 2:15–2:45 | Performance defaults | G1, compact headers (live allocation measurement), reading JMH reports |
| 2:45–3:00 | Break | — |
| 3:00–3:30 | Security | PQ TLS, JFR redaction, PEM one-liners |
| 3:30–3:45 | Vector API | What SIMD buys, the incubator rules, when to walk away |
| 3:45–4:00 | Migration clinic + Q&A | The upgrade checklist; open questions; workshop pitch |

## Teaching flow

Simple → complex, real-life examples at every step:

1. Start from a familiar pain (leaked tasks in `ExecutorService`, base64 wrappers, cast
   ladders).
2. Show the JDK 27 API that removes it.
3. Run the repo's demo and show the measured number.
4. Show the gotcha test — the thing that breaks in review.
5. Repeat.

## Materials

- Repository, explainers (`docs/explainers/`), upgrade guide (`docs/upgrade-guide.md`).
- The video kit's English cut as a pre-class warm-up (play in the waiting room).
- Exercise handouts: three simplified tasks from `events/hands-on-session.md`.

## Promotion

- Free registration form: name, email, experience level, "current JDK version".
- Announce via the content kit: LinkedIn post 1, X thread, community WhatsApp broadcast.
- Recording published within a week with the YouTube kit metadata.

## Follow-up (within 48 hours)

- Email: recording, repository, upgrade guide, and a one-question survey ("what should the
  next free session cover?").
- Invite attendees to the paid workshop with a community discount code.

## Success metrics

- ≥ 60% attendance of registrations.
- ≥ 30% of attendees open the repository link.
- ≥ 10% take the next step (workshop registration or newsletter).

## Risks and mitigations

- **Scope creep:** the 4-hour format forces choices — security gets 30 minutes, not 60;
  park deep questions for the workshop.
- **Mixed skill levels:** pre-class survey sets the baseline; exercises have a beginner
  path (run, observe) and an advanced path (extend, break, fix).
