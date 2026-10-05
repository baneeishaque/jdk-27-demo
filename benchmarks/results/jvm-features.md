# JVM feature measurements

- JDK: openjdk version "27" 2026-09-15
- Machine: arm64 on Darwin 27.0.0
- Flags: --enable-preview --add-modules jdk.incubator.vector

## Compact object headers (JEP 534)

| Scenario | bytes per Point(1,2) |
| --- | --- |
| compact object headers (default) | 16 |
| -XX:-UseCompactObjectHeaders | 24 |

## G1 under constrained CPU count (JEP 523)

```text
-XX:ActiveProcessorCount=1: bool UseG1GC = true {product} {ergonomic}
```

## Structured concurrency order pipeline (JEP 533)

| Mode | 4 orders wall clock |
| --- | --- |
| sequential service calls | 572 ms |
| structured fan-out | 257 ms |

## Vector API dot product, in-process timing (JEP 537)

```text
preferred species: Species[float, 4, S_128_BIT] (4 lanes)
max species: 4 lanes
scalar: 3584 us/op
vector: 979 us/op
speedup: 3.66x
results agree (relative error 1.85e-03): true
```

> For JMH-grade vector numbers see `vector-jmh.txt`.
