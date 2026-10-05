# Paid training playbook — JDK 27 migration workshop

A two-day commercial workshop for engineering teams planning or executing a JDK upgrade.
Delivered on-site or remote, capped at 20 participants, with a team-code migration clinic
on day two.

## Goal

- A team leaves with its own migration plan, one endpoint migrated to structured
  concurrency, and a benchmark baseline on its own hardware.
- Position the trainer as the JDK upgrade partner for the follow-up engagement.

## Audience

- Teams of 8–20 Java engineers (~2+ years experience) with a live codebase on JDK 17–21.
- Prerequisite: a pre-workshop audit call to confirm the codebase builds on the target
  JDK in a sandbox.

## Agenda

### Day 1 — Foundations and the free wins (8 hours incl. breaks)

| Time | Module | Content |
| --- | --- | --- |
| 0:00–1:00 | Upgrade economics | What JDK 27 changes by default; building the team's upgrade case |
| 1:00–2:30 | Toolchain lab | mise/toolchain setup on the team's machines; build + test the team's repo on JDK 27 |
| 2:30–3:30 | The four free wins | G1, compact headers, PQ TLS, JFR redaction — measured on the team's hardware |
| 3:30–5:00 | Structured concurrency, part 1 | API, mental model, the order pipeline; design exercise on the team's endpoints |
| 5:00–6:30 | Structured concurrency, part 2 | Lab: migrate one real endpoint; equality tests; timeout handling |
| 6:30–8:00 | Preview adoption policy | Lazy constants, primitive patterns, PEM; writing the team's preview-flag policy |

### Day 2 — Performance, security, and the plan (8 hours incl. breaks)

| Time | Module | Content |
| --- | --- | --- |
| 0:00–1:30 | Performance defaults audit | Allocation profiling before/after; reading the compact-headers delta on the team's workload |
| 1:30–3:00 | Vector API lab | Identify one hot loop in the team's code; vectorize behind a flag; tolerance tests |
| 3:00–4:30 | Security review | PQ TLS rollout (client/server/proxy inventory); JFR pipeline re-check; PEM migration |
| 4:30–6:00 | Migration clinic | Each squad migrates one assigned item; instructor reviews diffs |
| 6:00–7:00 | Benchmarking discipline | JMH setup on the team's repo; baseline + comparison protocol |
| 7:00–8:00 | The plan | Assemble the team's migration plan: owners, order, rollback, success metrics; Q&A |

## Deliverables to the client

- The team's JDK 27 migration plan (squads, order, rollback, metrics).
- One migrated endpoint with equality tests, merged or PR-ready.
- A benchmark baseline report on the team's hardware.
- A preview-adoption policy document (which previews are allowed, how flags are managed).
- Access to the showcase repository and its explainers as reference material.

## Commercials (template)

- **Format:** 2 days × 8 hours; on-site or remote; 8–20 participants.
- **Pricing:** per-workshop flat fee (not per seat) — quote on the audit call; travel
  billed separately for on-site.
- **Includes:** pre-workshop audit call, all materials, the team's migration plan, and a
  30-day follow-up review call.
- **Follow-up engagement:** 2–4 weeks of migration support (optional), priced per sprint.

## Pre-workshop (T-14 to T-2 days)

1. Audit call: codebase size, JDK version, test coverage, build system, deployment shape.
2. Sandbox build: the team's repo built on JDK 27 by the trainer; issues list sent back.
3. Environment pre-flight: every participant runs the showcase's `./mvnw verify` at least
   once before day 1.
4. Collect the team's top three upgrade fears — the workshop answers them explicitly.

## Post-workshop (within 5 business days)

- Deliver the migration plan and benchmark baseline report.
- Schedule the 30-day follow-up review.
- Offer the follow-up engagement with a scoped proposal.

## Success metrics

- The team's repo builds and tests green on JDK 27 by end of day 1.
- ≥ 1 endpoint migrated (PR-ready) by end of day 2.
- Migration plan accepted by the engineering lead.
- ≥ 50% of clients book the follow-up engagement.

## Risks and mitigations

- **Team codebase too broken on JDK 27:** the sandbox build surfaces this at T-7; if the
  gap is large, the workshop shifts to a "fix list" format with the showcase as the lab.
- **Mixed time zones (remote):** record sessions; run labs in breakout rooms with a
  roaming instructor.
- **Scope pressure:** the plan module is non-negotiable — protect the last hour; a workshop
  without a written plan does not convert to follow-up work.
