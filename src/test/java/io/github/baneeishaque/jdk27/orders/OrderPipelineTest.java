package io.github.baneeishaque.jdk27.orders;

import io.github.baneeishaque.jdk27.orders.OrderPipeline.Order;
import io.github.baneeishaque.jdk27.orders.OrderPipeline.Quote;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The integration pipeline must produce identical quotes in both modes.
 */
class OrderPipelineTest {

    private static final List<Order> ORDERS = List.of(
            new Order("A-1", "SKU-77", 2),
            new Order("A-2", "SKU-12", 1));

    @Test
    void quoteCombinesAllThreeServiceCalls() throws Exception {
        Quote quote = OrderPipeline.quote(ORDERS.getFirst());
        assertTrue(quote.availableUnits() > 0);
        assertEquals(quote.unitPriceCents() * 2 + quote.shippingCents(), quote.totalCents());
    }

    @Test
    void sequentialAndStructuredModesAgree() throws Exception {
        assertEquals(OrderPipeline.quoteSequentially(ORDERS), OrderPipeline.quoteConcurrently(ORDERS));
    }
}
