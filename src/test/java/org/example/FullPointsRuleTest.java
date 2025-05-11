package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the FullPointsRule class.
 */
class FullPointsRuleTest {

    @Test
    @DisplayName("Constructor should initialize correctly")
    void constructorShouldInitializeCorrectly() {
        // Act
        FullPointsRule rule = new FullPointsRule();

        // Assert - We can only test this indirectly through behavior
        Order order = createOrder();
        Wallet wallet = createWalletWithSufficientPoints();
        PaymentScenario baseScenario = createBaseScenario(order);

        assertTrue(rule.isApplicable(order, wallet, baseScenario));
    }

    @Test
    @DisplayName("isApplicable should return true when all conditions are met")
    void isApplicableShouldReturnTrueWhenAllConditionsAreMet() {
        // Arrange
        FullPointsRule rule = new FullPointsRule();

        Order order = createOrder();
        Wallet wallet = createWalletWithSufficientPoints();
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
        FullPointsRule rule = new FullPointsRule();

        Order order = createOrder();
        Wallet wallet = createWalletWithoutPoints();
        PaymentScenario baseScenario = createBaseScenario(order);

        // Act
        boolean result = rule.isApplicable(order, wallet, baseScenario);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("isApplicable should return false when points are insufficient")
    void isApplicableShouldReturnFalseWhenPointsAreInsufficient() {
        // Arrange
        FullPointsRule rule = new FullPointsRule();

        Order order = createOrder(); // Order value is 100.00
        Wallet wallet = createWalletWithInsufficientPoints(); // Points limit is 50.00
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
        FullPointsRule rule = new FullPointsRule();

        Order order = createOrder();
        Wallet wallet = createWalletWithSufficientPoints();

        // Create a base scenario that already uses points
        PaymentScenario baseScenario = new PaymentScenario(order, null, new BigDecimal("50.00"), 
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
        FullPointsRule rule = new FullPointsRule();

        Order order = createOrder(); // Order value is 100.00
        Wallet wallet = createWalletWithSufficientPoints(); // Points discount is 0.15
        PaymentScenario baseScenario = createBaseScenario(order);

        // Expected discount: 100.00 * 0.15 = 15.00
        BigDecimal expectedDiscount = new BigDecimal("15.00");

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
        FullPointsRule rule = new FullPointsRule();

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

    private Wallet createWalletWithSufficientPoints() {
        List<CardMethod> cardMethods = new ArrayList<>();
        PointsMethod pointsMethod = new PointsMethod(new BigDecimal("0.15"), new BigDecimal("200.00"));
        return new Wallet(cardMethods, pointsMethod);
    }

    private Wallet createWalletWithInsufficientPoints() {
        List<CardMethod> cardMethods = new ArrayList<>();
        PointsMethod pointsMethod = new PointsMethod(new BigDecimal("0.15"), new BigDecimal("50.00"));
        return new Wallet(cardMethods, pointsMethod);
    }

    private Wallet createWalletWithoutPoints() {
        List<CardMethod> cardMethods = new ArrayList<>();
        cardMethods.add(new CardMethod("card1", new BigDecimal("0.10"), new BigDecimal("1000.00")));
        return new Wallet(cardMethods, null);
    }

    private PaymentScenario createBaseScenario(Order order) {
        return new PaymentScenario(order, null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
