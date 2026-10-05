# Trade-offs

Decisions taken while building this showcase, and the alternatives that lost.

## Child VMs instead of reflection hacks

Four features are startup-configured (collector, object layout, recording redaction, TLS
debug). The alternative to child VMs is reflection into internal APIs — for example
`jdk.internal.misc` or `sun.security.ssl` — which breaks under `--illegal-access` policy,
across builds, and in any module-path deployment. Spawning a JVM costs ~300 ms per probe
and buys exact fidelity: the child runs the same code path a production launch would.

## Thread-allocated bytes instead of JOL

JOL (`org.openjdk.jol:jol-core`) gives exact object layouts. It also adds a dependency, and
its `GraphLayout` output is a wall of text that the test would have to parse. The
thread-allocated-bytes approach needs no dependency, measures what actually matters for
heap footprint (bytes allocated per instance), and pairs with a child-VM control for the
legacy layout.

## `keytool` instead of BouncyCastle for the TLS demo

A programmatic self-signed certificate needs either internal `sun.security.x509` APIs or
BouncyCastle. Neither is acceptable in a dependency-light showcase, so the child VM shells
out to the JDK's own `keytool` once per handshake (~200 ms). The keystore is a PKCS#12 file
in a temp directory, deleted with the VM.

## Regex over the TLS debug trace instead of a public API

JDK 27 has no public API that exposes the named group a handshake negotiated —
`SSLSession` does not carry it, and `SSLParameters#getNamedGroups` only shows what you
configured. Parsing `javax.net.debug` output is brittle in principle, so the demo pins the
format with tests: `supportedGroups` and `negotiatedGroups` are asserted against known
values, and the trace format is the same one JDK tooling documents for debugging.
The parse targets are the two JSON-style lines the JDK 27 trace emits
(`"named groups": [...]` and `"named group": ...`).

## Scalar reference inside the production class

`VectorApi.dotScalar` lives in `src/main` even though only tests and benchmarks use it as a
reference. The alternative — duplicating the scalar loop in the test — risks the reference
and the subject drifting. Keeping both in one class makes the comparison reviewable in one
screen and lets `report()` print the speedup without test-only helpers.

## JMH only for the Vector API

JMH is the right tool for CPU-bound microbenchmarks but the wrong tool for
feature-presence demos: collector selection and object layout are binary facts, and the
order pipeline is an end-to-end wall-clock story where JMH's harness would only add noise.
The repository therefore uses JMH exactly once (dot product, two sizes, two forks) and a
plain script for the rest, with `benchmarks/README.md` stating which is which.

## JUnit 5 instead of JUnit 6

JUnit 6 is current, but the 5.x line is what the surrounding tooling (Surefire 3.6,
IDE runners) has the most field mileage with, and this project's tests need nothing from
6.x. The dependency is one property change away if that calculus ever changes.

## Preview flags pinned in the build, not per command

Every compile and test invocation needs `--enable-preview --release 27`; hiding that in
`pom.xml` (and in the scripts) means a contributor cannot accidentally run with the wrong
flags, and the README does not need a flags footnote on every command.
