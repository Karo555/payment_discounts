package org.example;

import org.example.order.PaymentScenario;
import org.example.payment.CardMethod;
import org.example.payment.PointsMethod;
import org.example.promotion.PartialPointsRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.example.order.Order;
import org.example.order.Wallet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the PartialPointsRule class.
 */
class PartialPointsRuleTest {

    @Test
    @DisplayName("Constructor should initialize correctly")
    void constructorShouldInitializeCorrectly() {
        // Act
        PartialPointsRule rule = new PartialPointsRule();

        // Assert - We can only test this indirectly through behavior
        Order order = createOrder();
        Wallet wallet = createWalletWithPartialPoints();
        PaymentScenario baseScenario = createBaseScenario(order);

        assertTrue(rule.isApplicable(order, wallet, baseScenario));
    }

    @Test
    @DisplayName("isApplicable should return true when all conditions are met")
    void isApplicableShouldReturnTrueWhenAllConditionsAreMet() {
        // Arrange
        PartialPointsRule rule = new PartialPointsRule();

        Order order = createOrder(); // Order value is 100.00
        Wallet wallet = createWalletWithPartialPoints(); // Points limit is 50.00
        PaymentScenario baseScenario = createBaseScenario(order);

        // Act
        boolean result = rule.isApplicable(order, wallet, baseScenario);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isApplicable should return false when wallet has no points method")
    void isApplicableShouldReturnFalseWhenWalletHasNoPointsMethod() {
        // Arrange
        PartialPointsRule rule = new PartialPointsRule();

        Order order = createOrder();
        Wallet wallet = createWalletWithoutPoints();
        PaymentScenario baseScenario = createBaseScenario(order);

        // Act
        boolean result = rule.isApplicable(order, wallet, baseScenario);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("isApplicable should return false when points are zero")
    void isApplicableShouldReturnFalseWhenPointsAreZero() {
        // Arrange
        PartialPointsRule rule = new PartialPointsRule();

        Order order = createOrder();
        Wallet wallet = createWalletWithZeroPoints();
        PaymentScenario baseScenario = createBaseScenario(order);

        // Act
        boolean result = rule.isApplicable(order, wallet, baseScenario);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("isApplicable should return false when points are sufficient for full payment")
    void isApplicableShouldReturnFalseWhenPointsAreSufficientForFullPayment() {
        // Arrange
        PartialPointsRule rule = new PartialPointsRule();

        Order order = createOrder(); // Order value is 100.00
        Wallet wallet = createWalletWithSufficientPoints(); // Points limit is 200.00
        PaymentScenario baseScenario = createBaseScenario(order);

        // Act
        boolean result = rule.isApplicable(order, wallet, baseScenario);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("isApplicable should return false when base scenario already uses points")
    void isApplicableShouldReturnFalseWhenBaseScenarioAlreadyUsesPoints() {
        // Arrange
        PartialPointsRule rule = new PartialPointsRule();

        Order order = createOrder();
        Wallet wallet = createWalletWithPartialPoints();

        // Create a base scenario that already uses points
        PaymentScenario baseScenario = new PaymentScenario(order, null, new BigDecimal("25.00"), 
                                                          BigDecimal.ZERO, BigDecimal.ZERO);

        // Act
        boolean result = rule.isApplicable(order, wallet, baseScenario);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("computeDiscount should calculate discount correctly when applicable")
    void computeDiscountShouldCalculateDiscountCorrectlyWhenApplicable() {
        // Arrange
        PartialPointsRule rule = new PartialPointsRule();

        Order order = createOrder(); // Order value is 100.00
        Wallet wallet = createWalletWithPartialPoints(); // Points limit is 50.00, discount is 0.15
        PaymentScenario baseScenario = createBaseScenario(order);

        // Expected discount: 10% of order value = 10.00
        BigDecimal expectedDiscount = new BigDecimal("10.00");

        // Act
        BigDecimal actualDiscount = rule.computeDiscount(order, wallet, baseScenario);

        // Assert
        // Use compareTo for BigDecimal comparison to ignore scale differences
        assertEquals(0, expectedDiscount.compareTo(actualDiscount), 
                    "Expected discount " + expectedDiscount + " but got " + actualDiscount);
    }

    @Test
    @DisplayName("computeDiscount should return zero when not applicable")
    void computeDiscountShouldReturnZeroWhenNotApplicable() {
        // Arrange
        PartialPointsRule rule = new PartialPointsRule();

        Order order = createOrder();
        Wallet wallet = createWalletWithoutPoints();
        PaymentScenario baseScenario = createBaseScenario(order);

        // Act
        BigDecimal actualDiscount = rule.computeDiscount(order, wallet, baseScenario);

        // Assert
        assertEquals(BigDecimal.ZERO, actualDiscount);
    }

    // Helper methods to create test objects

    private Order createOrder() {
        return new Order("order123", new BigDecimal("100.00"), new HashSet<>());
    }

    private Wallet createWalletWithPartialPoints() {
        List<CardMethod> cardMethods = new ArrayList<>();
        PointsMethod pointsMethod = new PointsMethod(new BigDecimal("0.15"), new BigDecimal("50.00"));
        return Wallet.createWithCards(cardMethods, pointsMethod);
    }

    private Wallet createWalletWithSufficientPoints() {
        List<CardMethod> cardMethods = new ArrayList<>();
        PointsMethod pointsMethod = new PointsMethod(new BigDecimal("0.15"), new BigDecimal("200.00"));
        return Wallet.createWithCards(cardMethods, pointsMethod);
    }

    private Wallet createWalletWithZeroPoints() {
        List<CardMethod> cardMethods = new ArrayList<>();
        PointsMethod pointsMethod = new PointsMethod(new BigDecimal("0.15"), BigDecimal.ZERO);
        return Wallet.createWithCards(cardMethods, pointsMethod);
    }

    private Wallet createWalletWithoutPoints() {
        List<CardMethod> cardMethods = new ArrayList<>();
        cardMethods.add(new CardMethod("card1", new BigDecimal("0.10"), new BigDecimal("1000.00")));
        return Wallet.createWithCards(cardMethods, null);
    }

    private PaymentScenario createBaseScenario(Order order) {
        return new PaymentScenario(order, null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
