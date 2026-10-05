# Hands-on session playbook — JDK 27 migration lab

A 2.5-hour guided lab where participants run the showcase, break it, fix it, and leave with
one migrated piece of code of their own. Small groups (15–25), screens on, instructor
roaming.

## Goal

- Every participant runs the full test suite on JDK 27 and completes three guided
  exercises plus one migration of their own code.
- Participants experience the preview gotchas first-hand (wrapped timeout, permanent lazy
  failure) instead of reading about them.

## Audience

- Java developers with ~2 years of experience; comfortable with Maven and JUnit.
- Bring a laptop with JDK 27 via mise (or any JDK 27 build) pre-installed; a pre-flight
  email with `mise install && ./mvnw verify` avoids losing the first 30 minutes.

## Agenda (2.5 hours)

| Time | Segment | Content |
| --- | --- | --- |
| 0:00–0:15 | Setup check | Everyone runs `./mvnw verify`; fix stragglers |
| 0:15–0:35 | Guided demo | Instructor runs `run-demo.sh` end to end; participants follow |
| 0:35–1:05 | Exercise 1 — structured concurrency | Add a fourth service call to `OrderPipeline`; keep both modes identical; make the test pass |
| 1:05–1:20 | Break | — |
| 1:20–1:50 | Exercise 2 — break the timeout | Configure a 50 ms scope timeout; observe `ExecutionException` wrapping `CancelledByTimeoutException`; write the unwrap-first handler |
| 1:50–2:15 | Exercise 3 — vectorize a loop | Vectorize a scalar loop of their choice; keep the scalar fallback; tolerance-test it |
| 2:15–2:30 | Own-code clinic | Each participant migrates one small piece of their own code (a lazy singleton, a PEM wrapper, a parsing cast ladder) and shares the result |

## Exercises (solutions in the repo)

1. **Order pipeline extension** — add `warranty` (50 ms) to `Services`, `OrderPipeline`,
   and both quote paths. Expected: sequential grows by 50 ms per order, structured does
   not; the equality test still passes.
2. **Timeout unwrap** — in `StructuredConcurrency.timeoutDemo`, catch `ExecutionException`
   and inspect `getCause()`; add a test asserting the wrap.
3. **Vectorize** — reuse `VectorApi.dotVector`'s structure; assert with `1e-2` relative
   tolerance, never bit-equality.

## Materials

- Repository + `docs/explainers/` (each exercise maps to one explainer).
- `benchmarks/results/` for expected outputs.
- A shared chat channel for blockers; the instructor posts the canonical commands there.

## Promotion

- Registration form: name, email, laptop OS, "JDK 27 installed? yes/no".
- Announce 10 days out with the repo link; cap at 25 and run a waitlist.
- Pre-work email (T-2 days): `mise install`, `./mvnw verify`, report failures.

## Follow-up (within 48 hours)

- Email: exercise solutions branch, recording of the guided demo, upgrade guide, and the
  video kit link.
- Ask each participant for one sentence on what they migrated — quote them (with
  permission) in the community blog.

## Success metrics

- ≥ 80% complete all three exercises.
- ≥ 50% migrate a piece of their own code in the clinic.
- Post-session survey: "confident to run a JDK 27 spike" ≥ 4/5.

## Risks and mitigations

- **Environment drift:** version-check script at the door (`java -version`, `./mvnw -v`);
  keep two spare machines with the repo pre-built.
- **Exercise overruns:** exercise 3 is the cut line — move it to take-home if behind.
- **JDK download bandwidth:** mirror the mise cache on a USB drive for the room.
