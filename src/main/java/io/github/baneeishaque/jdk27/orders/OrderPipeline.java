package io.github.baneeishaque.jdk27.orders;

import java.lang.LazyConstant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

/**
 * Integration demo: a quote pipeline that combines several JDK 27 features.
 *
 * <p>Structured concurrency fans the three service calls of one quote out
 * concurrently (JEP 533); the service registry itself is a lazy constant that
 * is built on first use (JEP 531). The report contrasts a sequential run with
 * the structured run over the same order book.
 */
public final class OrderPipeline {

    /** An order line to quote. */
    public record Order(String id, String sku, int quantity) {
    }

    /** The assembled quote for one order. */
    public record Quote(String orderId, long unitPriceCents, int availableUnits,
                        long shippingCents, long totalCents) {
    }

    private OrderPipeline() {
    }

    /** Simulated backend services with fixed latencies. */
    static final class Services {

        Services() {
            sleep(80);
        }

        long priceCents(String sku) {
            sleep(40);
            return Math.abs(sku.hashCode() % 500) + 100L;
        }

        int stock(String sku) {
            sleep(60);
            return Math.abs(sku.hashCode() % 50) + 1;
        }

        long shippingCents(int quantity) {
            sleep(30);
            return 250L + 50L * quantity;
        }

        private static void sleep(long millis) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(interrupted);
            }
        }
    }

    private static final LazyConstant<Services> SERVICES = LazyConstant.of(Services::new);

    /**
     * Quotes one order with the three service calls fanned out concurrently.
     *
     * @param order order to quote
     * @return the assembled quote
     * @throws ExecutionException   when a service call fails
     * @throws InterruptedException when interrupted while joining
     */
    public static Quote quote(Order order) throws ExecutionException, InterruptedException {
        try (var scope = StructuredTaskScope.open()) {
            var price = scope.fork(() -> SERVICES.get().priceCents(order.sku()));
            var stock = scope.fork(() -> SERVICES.get().stock(order.sku()));
            var shipping = scope.fork(() -> SERVICES.get().shippingCents(order.quantity()));
            scope.join();
            long total = price.get() * order.quantity() + shipping.get();
            return new Quote(order.id(), price.get(), stock.get(), shipping.get(), total);
        }
    }

    /**
     * Quotes an order book sequentially, one service call after another.
     *
     * @param orders orders to quote
     * @return the quotes in order
     */
    public static List<Quote> quoteSequentially(List<Order> orders) {
        Services services = SERVICES.get();
        List<Quote> quotes = new ArrayList<>(orders.size());
        for (Order order : orders) {
            long price = services.priceCents(order.sku());
            int stock = services.stock(order.sku());
            long shipping = services.shippingCents(order.quantity());
            quotes.add(new Quote(order.id(), price, stock, shipping,
                    price * order.quantity() + shipping));
        }
        return quotes;
    }

    /**
     * Quotes an order book with the structured pipeline.
     *
     * @param orders orders to quote
     * @return the quotes in order
     * @throws ExecutionException   when a service call fails
     * @throws InterruptedException when interrupted while joining
     */
    public static List<Quote> quoteConcurrently(List<Order> orders)
            throws ExecutionException, InterruptedException {
        List<Quote> quotes = new ArrayList<>(orders.size());
        for (Order order : orders) {
            quotes.add(quote(order));
        }
        return quotes;
    }

    /**
     * Produces the demo report comparing sequential and structured runs.
     *
     * @return human-readable summary with measured wall-clock times
     */
    public static String report() {
        List<Order> orders = List.of(
                new Order("A-1001", "SKU-77", 2),
                new Order("A-1002", "SKU-12", 1),
                new Order("A-1003", "SKU-90", 4),
                new Order("A-1004", "SKU-33", 1));
        try {
            quoteSequentially(orders);
            quoteConcurrently(orders);
            long sequentialStart = System.nanoTime();
            List<Quote> sequential = quoteSequentially(orders);
            long sequentialMillis = (System.nanoTime() - sequentialStart) / 1_000_000;
            long concurrentStart = System.nanoTime();
            List<Quote> concurrent = quoteConcurrently(orders);
            long concurrentMillis = (System.nanoTime() - concurrentStart) / 1_000_000;
            return "orders: " + orders.size() + System.lineSeparator()
                    + "sequential: " + sequentialMillis + "ms" + System.lineSeparator()
                    + "structured: " + concurrentMillis + "ms" + System.lineSeparator()
                    + "same quotes: " + sequential.equals(concurrent) + System.lineSeparator()
                    + "first quote: " + concurrent.getFirst();
        } catch (ExecutionException | InterruptedException failure) {
            Thread.currentThread().interrupt();
            return "pipeline failed: " + failure;
        }
    }
}
