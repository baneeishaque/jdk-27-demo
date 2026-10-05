# Structured concurrency in JDK 27: the API that finally matches the mental model

Structured concurrency is on its seventh preview in JDK 27 — and this round is the one
where the API stopped arguing with the mental model. If you have ever leaked a thread,
lost a cancellation, or written a timeout handler that never fired, this is the feature to
spike first.

Everything here was verified on OpenJDK 27 (`27+35`) with runnable code, a test suite, and
a measured integration demo. Code links at the end.

## The problem structured concurrency solves

`ExecutorService` gives you tasks; it does not give you a *task hierarchy*. When a request
handler submits three calls and then fails, nothing cancels the other two. When the caller
gives up, the work keeps running. Cancellation is a convention you implement per call site,
and every mistake is invisible until production.

Structured concurrency inverts the relationship: the scope owns the tasks, the scope's
block is the lifetime, and the scope is the cancellation boundary. If the block exits —
normally, exceptionally, or by timeout — no task outlives it.

## The JDK 27 API

```java
try (var scope = StructuredTaskScope.open()) {
    Subtask<String> price = scope.fork(() -> fetch("price", 40));
    Subtask<String> stock = scope.fork(() -> fetch("stock", 60));
    Subtask<String> ship  = scope.fork(() -> fetch("shipping", 30));
    scope.join();
    return price.get() + stock.get() + ship.get();
}
```

The type is `StructuredTaskScope<T, R, R_X extends Throwable>` — three type parameters.
Factory shapes: `open()`, `open(Joiner)`, `open(Joiner, UnaryOperator<Configuration>)`, and
`open(UnaryOperator<Configuration>)`.

`Subtask` extends `Supplier`, so `get()` never throws checked exceptions for a successful
fork — failures arrive at `join()`.

## Joiners: all-or-nothing, and first-success-wins

- `Joiner.allSuccessfulOrThrow()` — the default; any failure fails the scope.
- `Joiner.anySuccessfulOrThrow()` — first success wins; the losers are cancelled.

The generic race needs an explicit type witness — a small wart worth knowing before code
review:

```java
try (var scope = StructuredTaskScope.open(Joiner.<String>anySuccessfulOrThrow())) {
    scope.fork(() -> fetch("primary"));
    scope.fork(() -> fetch("replica"));
    return scope.join(); // winner returned; loser cancelled
}
```

## Timeouts moved into configuration

`joinUntil(Instant)` is gone. Timeouts are scope configuration now, which composes better
with everything else:

```java
try (var scope = StructuredTaskScope.open(
        Joiner.<String>anySuccessfulOrThrow(),
        cfg -> cfg.withTimeout(Duration.ofMillis(300)))) {
    scope.fork(() -> fetch("primary"));
    scope.fork(() -> fetch("replica"));
    return scope.join();
}
```

`Configuration` also carries `withName` and `withThreadFactory`.

## Failure semantics

When a subtask fails under `allSuccessfulOrThrow()`, `join()` throws with the original
cause, and the scope cancels every sibling. The cancellation is cooperative: your task code
must respond to interruption the way good blocking code already does.

## The gotcha that costs an hour

With `anySuccessfulOrThrow()`, a scope **timeout does not throw
`CancelledByTimeoutException`**. It arrives as an `ExecutionException` whose *cause* is
`CancelledByTimeoutException`:

```java
try {
    return scope.join();
} catch (ExecutionException e) {
    if (e.getCause() instanceof StructuredTaskScope.CancelledByTimeoutException) {
        return fallback(); // this is the timeout path
    }
    throw e;
}
```

Catching `CancelledByTimeoutException` directly never fires. The showcase ships a test
that asserts the wrap, so the behaviour is documented in code, not tribal knowledge.

## The measured payoff

The integration demo quotes four orders through three simulated service calls:

| Mode | Wall clock, 4 orders |
| --- | --- |
| sequential | ~570 ms |
| structured fan-out | ~258 ms |

One quote costs `40 + 60 + 30 = 130 ms` sequentially but `max(40, 60, 30) = 60 ms` fanned
out — about 2.2x end to end. The test asserts both modes produce **identical quotes**: the
fast path is provably behaviour-preserving, not just faster.

```java
record Quote(String id, String price, String stock, String shipping) {}

static Quote quoteSequential(Services services, String id) {
    String price = services.price(id);
    String stock = services.stock(id);
    String shipping = services.shipping(id);
    return new Quote(id, price, stock, shipping);
}

static Quote quoteStructured(Services services, String id) throws Exception {
    try (var scope = StructuredTaskScope.open()) {
        var price = scope.fork(() -> services.price(id));
        var stock = scope.fork(() -> services.stock(id));
        var ship  = scope.fork(() -> services.shipping(id));
        scope.join();
        return new Quote(id, price.get(), stock.get(), ship.get());
    }
}
```

## Migration recipe

1. Pick one endpoint with two or more independent remote calls.
2. Wrap the calls in one scope; `fork` per call; `join()` before assembly.
3. Add the timeout to the scope configuration, and write the timeout path with the
   unwrap-first pattern.
4. Keep the sequential implementation behind a flag; add the equivalence test.
5. Only then delete the old path.

## The gotcha list for this feature

- Timeout arrives wrapped in `ExecutionException` (unwrapping required).
- `joinUntil(Instant)` no longer exists — use `Configuration#withTimeout`.
- Generic `anySuccessfulOrThrow()` needs `Joiner.<T>anySuccessfulOrThrow()`.
- Preview class files need `--enable-preview` in every process that loads them, including
  child JVMs your tooling spawns.

## Run it

```bash
git clone https://github.com/Baneeishaque/jdk-27-demo
cd jdk-27-demo
./mvnw verify
./scripts/run-demo.sh concurrency
./scripts/run-demo.sh orders
```

Full code, tests, and the line-by-line explainer:
`https://github.com/Baneeishaque/jdk-27-demo` — this article is part of a
three-part JDK 27 series; the other two cover the free performance wins and the security
features.
