# Explainer: JEP 523 — G1 is the default everywhere

Read [`../architecture.md`](../architecture.md) for the child-VM probe pattern this demo
leans on.

## What changed

Before JDK 27, the ergonomic collector selection was size-sensitive: machines with fewer
than two processors or less than roughly 1.8 GB of memory were given Serial GC, everything
else got G1. In containers this meant the *same* service could silently change collector
when a CPU limit or memory limit changed. JDK 27 removes the small-machine branch: G1 is
the default in all environments, and only an explicit `-XX` flag selects something else.

## The demo

[`G1Default`](../../src/main/java/io/github/baneeishaque/jdk27/gc/G1Default.java) reads the
decision back from the platform MXBeans instead of trusting the documentation:

```java
public static List<String> collectorNames() {          // :25
    return ManagementFactory.getGarbageCollectorMXBeans().stream()
            .map(GarbageCollectorMXBean::getName)
            .toList();
}

public static boolean isG1() {                          // :36
    return collectorNames().stream().anyMatch(name -> name.startsWith("G1"));
}
```

Why MXBeans and not a flag lookup: the MXBean view is the *result* of the whole selection
process — ergonomics, flags, container detection — whereas `-XX:+PrintFlagsFinal` shows the
final flag values but requires a second process. The demo uses both, deliberately:

- In-process: `isG1()` asserts the effective collector of the running VM.
- Child process: `G1DefaultTest` launches `-XX:ActiveProcessorCount=1 -XX:+PrintFlagsFinal
  -version` and asserts the `UseG1GC = true` line, because that is the JEP's actual claim.

The test also asserts the escape hatch: with `-XX:+UseSerialGC` the same probe reports
`UseSerialGC = true`, so an explicit choice still wins over the new default.

## What we measured

```text
active collectors: [G1 Young Generation, G1 Concurrent GC, G1 Old Generation]
G1 selected by default: true

-XX:ActiveProcessorCount=1: bool UseG1GC = true {product} {ergonomic}
```

The `{ergonomic}` tag matters: nothing forced G1 — the VM chose it. On JDK 26 the same
probe selected Serial.

## Notes for practitioners

- If you carried `-XX:+UseG1GC` purely to defend against the small-machine fallback, you
  can drop it; the default now covers you.
- If you relied on Serial GC in tiny containers for startup time, keep the explicit flag —
  JEP 523 changes the default, not the available collectors.
- `G1 Concurrent GC` in the bean list is the concurrent marking cycle; asserting
  `startsWith("G1")` covers both young and old collectors.
