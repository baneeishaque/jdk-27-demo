# Explainer: JEP 533 — structured concurrency

## What changed

`StructuredTaskScope` treats a group of concurrent subtasks as one unit of work: forks
happen inside a scope, `join()` waits for the outcome, and closing the scope guarantees no
subtask is still running. JDK 27 (seventh preview) settles the API around `open()`, a
`Joiner` that decides what an outcome means, and a `Configuration` for scope-wide policy
such as timeouts.

The JDK 27 signatures:

```java
static <T> StructuredTaskScope<T, Void, ExecutionException> open()
static <T, R, R_X extends Throwable> StructuredTaskScope<T, R, R_X> open(Joiner<? super T, ? extends R, R_X>)
static <T, R, R_X extends Throwable> StructuredTaskScope<T, R, R_X> open(Joiner<...>, UnaryOperator<Configuration>)
```

## The three canonical shapes

[`StructuredConcurrency`](../../src/main/java/io/github/baneeishaque/jdk27/concurrency/StructuredConcurrency.java)
demonstrates each, plus failure propagation.

### Fan-out with the default joiner

```java
try (var scope = StructuredTaskScope.open()) {            // :43
    Subtask<String> profile = scope.fork(() -> fetch("profile", 80));
    Subtask<String> orders = scope.fork(() -> fetch("orders", 120));
    scope.join();
    return profile.get() + ", " + orders.get();
}
```

`open()` defaults to `awaitAllSuccessfulOrThrow` — wait for everything, throw if anything
failed. Result type is `Void`, so you read each subtask with `get()`.

### Racing with `anySuccessfulOrThrow`

```java
try (var scope = StructuredTaskScope.open(Joiner.<String>anySuccessfulOrThrow())) {   // :59
    scope.fork(() -> fetch("slow", 300));
    scope.fork(() -> { Thread.sleep(50); throw new IllegalStateException("fast failure is ignored"); });
    scope.fork(() -> fetch("fast", 150));
    return scope.join();
}
```

`join()` returns the first *successful* result — `"fast (150ms)"`. The failed subtask does
not abort the race; when every subtask fails, the joiner throws.

### Timeout via configuration

```java
try (var scope = StructuredTaskScope.open(Joiner.<String>anySuccessfulOrThrow(),      // :76
        configuration -> configuration.withTimeout(Duration.ofMillis(100)))) {
    scope.fork(() -> fetch("too-slow", 2_000));
    return "unexpected success: " + scope.join();
} catch (ExecutionException failure) {
    if (failure.getCause() instanceof StructuredTaskScope.CancelledByTimeoutException) {
        return "scope cancelled after 100ms";
    }
    ...
}
```

Two details from real runs: `joinUntil(Instant)` is gone — timeouts are configuration, not
join arguments; and with a joiner whose failure type is `ExecutionException`, the timeout
arrives **wrapped**: `ExecutionException` whose cause is `CancelledByTimeoutException`.
Catching `CancelledByTimeoutException` directly does not work.

### Failure propagation

```java
try (var scope = StructuredTaskScope.open()) {            // :97
    scope.fork(() -> fetch("healthy", 50));
    scope.fork(() -> { throw new IllegalStateException("payment declined"); });
    scope.join();
    ...
} catch (ExecutionException failure) {
    return "siblings cancelled; cause: " + failure.getCause().getMessage();
}
```

When one subtask fails, the scope cancels the siblings and `join()` throws the joiner's
failure with the original exception as cause.

## What we measured

```text
fan-out: profile (80ms), orders (120ms) in 129ms (not 200ms)
first success: fast (150ms)
timeout: scope cancelled after 100ms
failure: siblings cancelled; cause: payment declined
```

## Notes for practitioners

- Default threads are virtual; `Configuration#withThreadFactory` swaps them if you must.
- The scope is the cancellation boundary: closing it interrupts stragglers. This is the
  structural guarantee `ExecutorService` never gave you.
- Keep the scope's lifetime inside one method — that is what makes the concurrency
  *structured*.
