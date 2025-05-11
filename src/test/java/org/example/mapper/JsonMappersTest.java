package org.example.mapper;

import org.example.order.Order;
import org.example.payment.CardMethod;
import org.example.payment.PaymentMethod;
import org.example.payment.PointsMethod;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the JSON mappers.
 */
public class JsonMappersTest {

    @Test
    public void testOrderJsonMapper() throws IOException {
        // Get the file from resources
        File ordersFile = new File("src/main/resources/data/orders.json");

        // Read the orders
        List<Order> orders = OrderJsonMapper.readOrders(ordersFile);

        // Verify the number of orders
        assertEquals(4, orders.size(), "Should have 4 orders");

        // Verify the first order
        Order order1 = orders.stream()
                .filter(o -> "ORDER1".equals(o.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("ORDER1 not found"));

        assertEquals(new BigDecimal("100.00"), order1.getValue(), "ORDER1 value should be 100.00");
        assertTrue(order1.getEligiblePromoIds().contains("mZysk"), "ORDER1 should have mZysk promotion");
        assertEquals(1, order1.getEligiblePromoIds().size(), "ORDER1 should have 1 promotion");

        // Verify the fourth order (no promotions)
        Order order4 = orders.stream()
                .filter(o -> "ORDER4".equals(o.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("ORDER4 not found"));

        assertEquals(new BigDecimal("50.00"), order4.getValue(), "ORDER4 value should be 50.00");
        assertTrue(order4.getEligiblePromoIds().isEmpty(), "ORDER4 should have no promotions");
    }

    @Test
    public void testPaymentMethodJsonMapper() throws IOException {
        // Get the file from resources
        File paymentMethodsFile = new File("src/main/resources/data/paymentmethods.json");

        // Read the payment methods
        List<PaymentMethod> paymentMethods = PaymentMethodJsonMapper.readPaymentMethods(paymentMethodsFile);

        // Verify the number of payment methods
        assertEquals(3, paymentMethods.size(), "Should have 3 payment methods");

        // Verify the points method
        PaymentMethod punkty = paymentMethods.stream()
                .filter(pm -> "PUNKTY".equals(pm.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("PUNKTY not found"));

        assertFalse(punkty.isCard(), "PUNKTY should not be a card");
        assertTrue(punkty instanceof PointsMethod, "PUNKTY should be a PointsMethod");
        assertEquals(new BigDecimal("15"), punkty.getDiscountPercent(), "PUNKTY discount should be 15");
        assertEquals(new BigDecimal("100.00"), punkty.getRemainingLimit(), "PUNKTY limit should be 100.00");

        // Verify a card method
        PaymentMethod mZysk = paymentMethods.stream()
                .filter(pm -> "mZysk".equals(pm.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("mZysk not found"));

        assertTrue(mZysk.isCard(), "mZysk should be a card");
        assertTrue(mZysk instanceof CardMethod, "mZysk should be a CardMethod");
        assertEquals(new BigDecimal("10"), mZysk.getDiscountPercent(), "mZysk discount should be 10");
        assertEquals(new BigDecimal("180.00"), mZysk.getRemainingLimit(), "mZysk limit should be 180.00");
    }
}
