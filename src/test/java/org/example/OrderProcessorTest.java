package org.example;

import org.example.order.Order;
import org.example.order.OrderProcessor;
import org.example.order.PaymentScenario;
import org.example.order.PaymentScenarioEvaluator;
import org.example.order.Wallet;
import org.example.payment.CardMethod;
import org.example.payment.PointsMethod;
import org.example.promotion.FullCardRule;
import org.example.promotion.FullPointsRule;
import org.example.promotion.PartialPointsRule;
import org.example.promotion.PromotionRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the OrderProcessor class.
 */
class OrderProcessorTest {

    private OrderProcessor orderProcessor;
    private PaymentScenarioEvaluator evaluator;
    private List<PromotionRule> rules;
    private Wallet wallet;
    private List<Order> orders;
    private CardMethod card1;
    private CardMethod card2;
    private PointsMethod pointsMethod;

    @BeforeEach
    void setUp() {
        // Create evaluator
        evaluator = new PaymentScenarioEvaluator();

        // Create cards with different discount percentages
        card1 = new CardMethod("card1", new BigDecimal("0.10"), new BigDecimal("1000.00"));
        card2 = new CardMethod("card2", new BigDecimal("0.15"), new BigDecimal("500.00"));

        // Create points method
        pointsMethod = new PointsMethod(new BigDecimal("0.20"), new BigDecimal("200.00"));

        // Create wallet with cards and points
        wallet = Wallet.createWithCards(Arrays.asList(card1, card2), pointsMethod);

        // Create promotion rules
        rules = new ArrayList<>();
        rules.add(new FullCardRule("card1"));
        rules.add(new FullCardRule("card2"));
        rules.add(new FullPointsRule());
        rules.add(new PartialPointsRule());

        // Create orders
        orders = new ArrayList<>();
        Set<String> eligiblePromos = new HashSet<>(Arrays.asList("card1", "card2"));
        orders.add(new Order("order1", new BigDecimal("100.00"), eligiblePromos));
        orders.add(new Order("order2", new BigDecimal("150.00"), eligiblePromos));
        orders.add(new Order("order3", new BigDecimal("300.00"), eligiblePromos));

        // Create order processor
        orderProcessor = new OrderProcessor(evaluator, rules);
    }

    @Test
    @DisplayName("Should throw exception when parameters are null")
    void shouldThrowExceptionWhenParametersAreNull() {
        assertThrows(IllegalArgumentException.class, () -> orderProcessor.processOrders(null, wallet));
        assertThrows(IllegalArgumentException.class, () -> orderProcessor.processOrders(orders, null));
    }

    @Test
    @DisplayName("Should process orders and return final allocations")
    void shouldProcessOrdersAndReturnFinalAllocations() {
        // Act
        List<PaymentScenario> result = orderProcessor.processOrders(orders, wallet);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());

        // Debug logging
        System.out.println("[DEBUG_LOG] Final allocations size: " + result.size());
        for (int i = 0; i < result.size(); i++) {
            PaymentScenario scenario = result.get(i);
            System.out.println("[DEBUG_LOG] Scenario " + i + " - Order ID: " + scenario.getOrder().getId());
            System.out.println("[DEBUG_LOG] Scenario " + i + " - Uses Card: " + scenario.usesCard());
            System.out.println("[DEBUG_LOG] Scenario " + i + " - Uses Points: " + scenario.usesPoints());
            System.out.println("[DEBUG_LOG] Scenario " + i + " - Discount Value: " + scenario.getDiscountValue());
        }
    }

    @Test
    @DisplayName("Should update wallet by deducting used funds")
    void shouldUpdateWalletByDeductingUsedFunds() {
        // Arrange
        BigDecimal initialPointsLimit = wallet.totalRemainingPoints();
        BigDecimal initialCard1Limit = card1.getRemainingLimit();
        BigDecimal initialCard2Limit = card2.getRemainingLimit();

        System.out.println("[DEBUG_LOG] Initial points limit: " + initialPointsLimit);
        System.out.println("[DEBUG_LOG] Initial card1 limit: " + initialCard1Limit);
        System.out.println("[DEBUG_LOG] Initial card2 limit: " + initialCard2Limit);

        // Act
        orderProcessor.processOrders(orders, wallet);

        // Assert
        BigDecimal finalPointsLimit = wallet.totalRemainingPoints();
        BigDecimal finalCard1Limit = card1.getRemainingLimit();
        BigDecimal finalCard2Limit = card2.getRemainingLimit();

        System.out.println("[DEBUG_LOG] Final points limit: " + finalPointsLimit);
        System.out.println("[DEBUG_LOG] Final card1 limit: " + finalCard1Limit);
        System.out.println("[DEBUG_LOG] Final card2 limit: " + finalCard2Limit);

        // Verify that funds were deducted
        assertTrue(finalPointsLimit.compareTo(initialPointsLimit) < 0 || 
                  finalCard1Limit.compareTo(initialCard1Limit) < 0 || 
                  finalCard2Limit.compareTo(initialCard2Limit) < 0);
    }

    @Test
    @DisplayName("Should handle empty orders list")
    void shouldHandleEmptyOrdersList() {
        // Act
        List<PaymentScenario> result = orderProcessor.processOrders(Collections.emptyList(), wallet);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle errors during processing and continue with next order")
    void shouldHandleErrorsDuringProcessingAndContinueWithNextOrder() {
        // Arrange
        List<Order> ordersWithInvalid = new ArrayList<>(orders);
        // Add an order with a very large value that can't be paid with available methods
        ordersWithInvalid.add(new Order("invalidOrder", new BigDecimal("10000.00"), Collections.emptySet()));

        // Act
        List<PaymentScenario> result = orderProcessor.processOrders(ordersWithInvalid, wallet);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size()); // Only the valid orders should be processed
    }

    @Test
    @DisplayName("Should keep track of final allocations")
    void shouldKeepTrackOfFinalAllocations() {
        // Act
        orderProcessor.processOrders(orders, wallet);
        List<PaymentScenario> finalAllocations = orderProcessor.getFinalAllocations();

        // Assert
        assertNotNull(finalAllocations);
        assertEquals(3, finalAllocations.size());

        // Verify that the final allocations match the orders
        for (int i = 0; i < orders.size(); i++) {
            assertEquals(orders.get(i).getId(), finalAllocations.get(i).getOrder().getId());
        }
    }
}
