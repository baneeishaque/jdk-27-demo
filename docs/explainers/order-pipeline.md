# Explainer: the order pipeline (integration demo)

[`OrderPipeline`](../../src/main/java/io/github/baneeishaque/jdk27/orders/OrderPipeline.java)
is the "features together" demo: quoting an order requires three backend calls (price,
stock, shipping), each simulated with a fixed latency. The pipeline combines structured
concurrency (JEP 533) with a lazy service registry (JEP 531) and shows the wall-clock
difference against a sequential baseline.

## The shape

```java
static final class Services {                              // :32
    Services() { sleep(80); }                              // connection warm-up
    long priceCents(String sku)    { sleep(40); ... }
    int  stock(String sku)         { sleep(60); ... }
    long shippingCents(int qty)    { sleep(30); ... }
}

private static final LazyConstant<Services> SERVICES = LazyConstant.of(Services::new);   // :63

public static Quote quote(Order order) throws ExecutionException, InterruptedException { // :73
    try (var scope = StructuredTaskScope.open()) {
        var price    = scope.fork(() -> SERVICES.get().priceCents(order.sku()));
        var stock    = scope.fork(() -> SERVICES.get().stock(order.sku()));
        var shipping = scope.fork(() -> SERVICES.get().shippingCents(order.quantity()));
        scope.join();
        long total = price.get() * order.quantity() + shipping.get();
        return new Quote(order.id(), price.get(), stock.get(), shipping.get(), total);
    }
}
```

Three things are deliberate:

1. **One scope per quote.** The scope is the unit of work; if any of the three calls fails,
   the other two are cancelled — the caller never observes a half-quoted order.
2. **Lazy registry.** `SERVICES` pays its 80 ms construction cost exactly once, on first
   use; `LazyConstant` guarantees at-most-once even under concurrent first access.
3. **Identical outcomes.** `quoteSequentially` (`:90`) and `quoteConcurrently` (`:111`)
   build the same `Quote` records; the test asserts list equality, which makes the
   concurrency provably behaviour-preserving rather than eyeballed.

## What we measured

```text
orders: 4
sequential: 570ms
structured: 258ms
same quotes: true
first quote: Quote[orderId=A-1001, unitPriceCents=548, availableUnits=49, shippingCents=350, totalCents=1446]
```

The arithmetic: one quote costs `40 + 60 + 30 = 130 ms` sequentially, but
`max(40, 60, 30) = 60 ms` structured — a ~2.2x per-order speedup. Four orders:
`4 × 130 = 520 ms` plus overhead versus `4 × 60 = 240 ms` plus overhead. The measured
~570/~258 ms includes JIT warm-up runs before the timed rounds.

## Why this is the article's centerpiece

Each individual JEP demo proves one mechanism. The pipeline shows the *composition*:
structured concurrency for the fan-out, lazy constants for the shared registry, and an
equality test as the safety net. That composition — not any single feature — is what a
production service looks like after a JDK 27 migration.
