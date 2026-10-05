# Explainer: JEP 531 — lazy constants

## What changed

`java.lang.LazyConstant` is a value computed **at most once**, on first access, and
eligible for the same JVM constant-folding treatment as a `static final` field. The API
surface is deliberately tiny: `LazyConstant.of(Supplier)` and `get()`. Lazy collections
(`List.ofLazy`, `Set.ofLazy`, `Map.ofLazy`) build on the same idea: a view whose elements
are computed on demand and cached.

The signatures in JDK 27 are worth pinning down, because they are not the shapes people
guess:

```java
static <E> List<E> ofLazy(int size, IntFunction<? extends E> computingFunction)
static <E> Set<E> ofLazy(Set<? extends E> elementCandidates, Predicate<? super E> filter)
static <K, V> Map<K, V> ofLazy(Set<? extends K> keys, Function<? super K, ? extends V> mapping)
```

A lazy list is indexed by position; a lazy set is a *filtered view* over candidate
elements; a lazy map is a computed view over a fixed key set.

## The demo

[`LazyConstants`](../../src/main/java/io/github/baneeishaque/jdk27/lazy/LazyConstants.java)
wraps each API with a call counter so the report shows laziness rather than describing it:

```java
public static LazyConstant<String> counting(AtomicInteger calls) {   // :28
    return LazyConstant.of(() -> { calls.incrementAndGet(); return "computed"; });
}
```

The tests assert the exact semantics: the supplier runs once for two `get()` calls; list
elements are computed per index and cached (`get(1)` twice plus `get(2)` is exactly two
computations); map values are computed per key.

## Failure permanence — the sharp edge

When the initial computation throws, the failure is **remembered**: later accesses do not
retry the supplier. The failure is surfaced as a `NoSuchElementException` that names the
original exception in its message:

```text
first access:  NoSuchElementException: Unable to access the constant because java.lang.IllegalStateException was thrown at initial computation
second access: NoSuchElementException: Unable to access the constant because java.lang.IllegalStateException was thrown at initial computation
```

Two consequences for real code: (1) a transient failure in an initializer becomes permanent
— wrap retryable work *inside* the supplier; (2) catch `NoSuchElementException`, not the
original exception type, and parse the message only for logs. The same wrapping happens for
`null` results (`NullPointerException` in the message), because lazy constants forbid null.

## What we measured

```text
supplier calls before first get(): 0
supplier calls after two get() calls: 1
list elements computed before any access: 0
list elements computed after get(1) x2 and get(2): 2
lazy set view: [banana]
map values computed after get(alpha) x2: 1
```

## Notes for practitioners

- Replace the holder-class idiom (`class Holder { static final X INSTANCE = ...; }`) with
  `LazyConstant.of(...)`; intent becomes explicit and the JIT gets a constant it can fold.
- Lazy collections are *views*: creation is free, but a full traversal computes everything.
  `size()` on a lazy list is free; `toString()` computes all elements — be careful in logs.
- `isInitialized` does not exist; if you need that signal, track it in the supplier.
