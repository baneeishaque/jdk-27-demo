# Webinar playbook — JDK 27: all nine JEPs, measured

A 60-minute live webinar introducing JDK 27 through measured demos. Free, open
registration, recorded and published to the community channel afterwards.

## Goal

- Audience leaves able to name the four final JEPs, one preview to spike, and where to
  reproduce every number.
- Registrations convert to repository visits and newsletter sign-ups.

## Audience

- Java developers with ~2 years of experience.
- No JDK 27 knowledge assumed; basic familiarity with `ExecutorService`, TLS, and Maven
  helps.

## Agenda (60 minutes)

| Time | Segment | Content |
| --- | --- | --- |
| 0:00–0:05 | Welcome | Who I am, what the showcase is, where the repo is |
| 0:05–0:15 | The four free wins | G1 default, compact headers (24 → 16 bytes), PQ TLS, JFR redaction |
| 0:15–0:30 | Structured concurrency | Live demo: sequential vs structured order pipeline (570 → 258 ms) |
| 0:30–0:40 | The other previews | Lazy constants, primitive patterns, PEM — live demo of each gotcha |
| 0:40–0:50 | Vector API | JMH numbers, live benchmark run, when not to use it |
| 0:50–1:00 | Q&A | Collected from chat; end with the migration checklist and repo link |

## Demo checklist (rehearse before going live)

1. `./mvnw verify` — pre-warmed, 33 tests green.
2. `./scripts/run-demo.sh orders` — the 570 ms → 258 ms pipeline.
3. `./scripts/run-demo.sh patterns` — the exactness cases (42.0 / 5e9 / 0.1).
4. `./scripts/bench-vector.sh` — pre-run; show `benchmarks/results/vector-jmh.txt`
   rather than waiting on JMH live.
5. `./scripts/run-demo.sh tls` — the group list with `X25519MLKEM768` first.

## Materials

- Repository: `https://github.com/Baneeishaque/jdk-27-demo`
- Video kit: `content/youtube/` — play the English cut (or a locale cut) as the intro
  segment if the live setup fails.
- Explainers: `docs/explainers/` — link each JEP section to its explainer.
- Slides: the YouTube slide PNGs under `content/youtube/build/slides/en/` work as-is.

## Promotion

- Announce 7 days out, reminder 1 day out, "starting now" post at T-5 minutes.
- Use the LinkedIn announcement copy from `content/linkedin.md` (post 1) for the
  announcement; the X thread works as the reminder.
- The registration form carries: name, email, experience level, "what do you want to see"
  free-text.

## Follow-up (within 48 hours)

- Email all registrants: recording link, repository link, migration checklist
  (`docs/upgrade-guide.md`), and the benchmark reports.
- Publish the recording with the YouTube kit's metadata (`content/youtube/metadata.md`).
- Post the Q&A highlights as a LinkedIn post 2 (deep dive copy in `content/linkedin.md`).

## Success metrics

- Registration → attendance ≥ 40%.
- Repository visits from the webinar UTM link.
- At least 10 questions in chat; at least 5 answered live.

## Risks and mitigations

- **Live demo fails:** the pre-recorded video kit is the fallback; keep it open in a tab.
- **JMH runs long:** never run JMH live; show the committed report.
- **Preview flags:** the demo scripts already pass `--enable-preview`; do not improvise
  commands live.
