# Explainer: JEP 534 — compact object headers

## What changed

Every Java object starts with a header: a mark word plus a class pointer. On 64-bit
platforms the legacy layout spends 96 bits (12 bytes, padded to 16) on that header. JDK 27
makes the **compact** layout the default on x64 and AArch64: the mark word and a 22-bit
compressed class pointer share one 64-bit word, so the header drops to 8 bytes. Object-
dense heaps typically shrink 10–20%.

## The demo

Two pieces of evidence, because the flag state and the actual footprint are different
claims.

### Flag state

```java
public static boolean isEnabled() {                       // :32
    HotSpotDiagnosticMXBean bean = ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean.class);
    return Boolean.parseBoolean(bean.getVMOption("UseCompactObjectHeaders").getValue());
}
```

### Footprint, measured

```java
public static long bytesPerAllocation(Supplier<Object> factory, int count) {   // :49
    com.sun.management.ThreadMXBean threadBean = ...;
    Object[] keepAlive = new Object[count];
    factory.get();                                        // warm-up excluded
    long before = threadBean.getCurrentThreadAllocatedBytes();
    for (int index = 0; index < count; index++) {
        keepAlive[index] = factory.get();
    }
    long after = threadBean.getCurrentThreadAllocatedBytes();
    ...
    return (after - before) / count;
}
```

`getCurrentThreadAllocatedBytes` counts allocation, not retention, so the number is
independent of GC timing and heap size. The warm-up call keeps class loading and TLAB
set-up out of the window; the `keepAlive` array prevents dead-code elimination.

`Point(1,2)` — a two-int record — is the probe object: header + 8 bytes of payload.

| Layout | bytes per `Point(1,2)` |
| --- | --- |
| compact (default) | 16 |
| `-XX:-UseCompactObjectHeaders` | 24 |

The 8-byte difference *is* the header difference. The test asserts a delta (and that the
compact figure is ≤ 16) rather than hard-coding both numbers, so it stays meaningful on
platforms where the baseline differs.

## Why a child VM for the control

The layout is fixed at VM startup; a running JVM cannot switch. The test therefore launches
`Main headers` in a child with `-XX:-UseCompactObjectHeaders` and parses its allocation
line, comparing it with the in-process measurement. This is the child-VM probe pattern
described in [`../architecture.md`](../architecture.md).

## Notes for practitioners

- Expect live-set reductions to show up as lower heap pressure and fewer GCs; per-object
  arithmetic (8 bytes saved per header) is the honest way to estimate.
- Escape hatch: `-XX:-UseCompactObjectHeaders` restores the legacy layout for the rare
  tool that pokes at header bits.
- The compact layout is a JVM-wide choice; mixed-version JVMs (e.g. during rolling
  restarts) can each make their own decision safely.
