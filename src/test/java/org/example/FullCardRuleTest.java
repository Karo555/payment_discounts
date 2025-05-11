package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the FullCardRule class.
 */
class FullCardRuleTest {

    @Test
    @DisplayName("Constructor should initialize cardMethodId correctly")
    void constructorShouldInitializeCardMethodIdCorrectly() {
        // Arrange
        String cardMethodId = "card123";

        // Act
        FullCardRule rule = new FullCardRule(cardMethodId);

        // Assert - We can only test this indirectly through behavior
        Order order = createOrderWithPromo(cardMethodId);
        Wallet wallet = createWalletWithCard(cardMethodId);
        PaymentScenario baseScenario = createBaseScenario(order);
        
        assertTrue(rule.isApplicable(order, wallet, baseScenario));
    }

    @Test
    @DisplayName("isApplicable should return true when all conditions are met")
    void isApplicableShouldReturnTrueWhenAllConditionsAreMet() {
        // Arrange
        String cardMethodId = "card123";
        FullCardRule rule = new FullCardRule(cardMethodId);
        
        Order order = createOrderWithPromo(cardMethodId);
        Wallet wallet = createWalletWithCard(cardMethodId);
        PaymentScenario baseScenario = createBaseScenario(order);

        // Act
        boolean result = rule.isApplicable(order, wallet, baseScenario);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isApplicable should return false when order doesn't have card promotion")
    void isApplicableShouldReturnFalseWhenOrderDoesntHaveCardPromotion() {
        // Arrange
        String cardMethodId = "card123";
        FullCardRule rule = new FullCardRule(cardMethodId);
        
        Order order = createOrderWithoutPromos();
        Wallet wallet = createWalletWithCard(cardMethodId);
        PaymentScenario baseScenario = createBaseScenario(order);

        // Act
        boolean result = rule.isApplicable(order, wallet, baseScenario);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("isApplicable should return false when wallet doesn't have matching card")
    void isApplicableShouldReturnFalseWhenWalletDoesntHaveMatchingCard() {
        // Arrange
        String cardMethodId = "card123";
        String differentCardId = "card456";
        FullCardRule rule = new FullCardRule(cardMethodId);
        
        Order order = createOrderWithPromo(cardMethodId);
        Wallet wallet = createWalletWithCard(differentCardId);
        PaymentScenario baseScenario = createBaseScenario(order);

        // Act
        boolean result = rule.isApplicable(order, wallet, baseScenario);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("isApplicable should return false when base scenario already uses card")
    void isApplicableShouldReturnFalseWhenBaseScenarioAlreadyUsesCard() {
        // Arrange
        String cardMethodId = "card123";
        FullCardRule rule = new FullCardRule(cardMethodId);
        
        Order order = createOrderWithPromo(cardMethodId);
        Wallet wallet = createWalletWithCard(cardMethodId);
        
        // Create a base scenario that already uses a card
        CardMethod cardMethod = new CardMethod("otherCard", new BigDecimal("0.05"), new BigDecimal("500.00"));
        PaymentScenario baseScenario = new PaymentScenario(order, cardMethod, BigDecimal.ZERO, 
                                                          new BigDecimal("100.00"), BigDecimal.ZERO);

        // Act
        boolean result = rule.isApplicable(order, wallet, baseScenario);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("isApplicable should return false when card has insufficient limit")
    void isApplicableShouldReturnFalseWhenCardHasInsufficientLimit() {
        // Arrange
        String cardMethodId = "card123";
        FullCardRule rule = new FullCardRule(cardMethodId);
        
        // Order value is 100.00
        Order order = createOrderWithPromo(cardMethodId);
        
        // Create a wallet with a card that has insufficient limit (50.00 < 100.00)
        List<CardMethod> cardMethods = new ArrayList<>();
        cardMethods.add(new CardMethod(cardMethodId, new BigDecimal("0.10"), new BigDecimal("50.00")));
        Wallet wallet = new Wallet(cardMethods, null);
        
        PaymentScenario baseScenario = createBaseScenario(order);

        // Act
        boolean result = rule.isApplicable(order, wallet, baseScenario);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("computeDiscount should calculate discount correctly when applicable")
    void computeDiscountShouldCalculateDiscountCorrectlyWhenApplicable() {
        // Arrange
        String cardMethodId = "card123";
        FullCardRule rule = new FullCardRule(cardMethodId);
        
        Order order = createOrderWithPromo(cardMethodId);
        Wallet wallet = createWalletWithCard(cardMethodId);
        PaymentScenario baseScenario = createBaseScenario(order);
        
        // Expected discount: 100.00 * 0.10 = 10.00
        BigDecimal expectedDiscount = new BigDecimal("10.00");

        // Act
        BigDecimal actualDiscount = rule.computeDiscount(order, wallet, baseScenario);

        // Assert
        assertEquals(expectedDiscount, actualDiscount);
    }

    @Test
    @DisplayName("computeDiscount should return zero when not applicable")
    void computeDiscountShouldReturnZeroWhenNotApplicable() {
        // Arrange
        String cardMethodId = "card123";
        FullCardRule rule = new FullCardRule(cardMethodId);
        
        Order order = createOrderWithoutPromos();
        Wallet wallet = createWalletWithCard(cardMethodId);
        PaymentScenario baseScenario = createBaseScenario(order);

        // Act
        BigDecimal actualDiscount = rule.computeDiscount(order, wallet, baseScenario);

        // Assert
        assertEquals(BigDecimal.ZERO, actualDiscount);
    }

    // Helper methods to create test objects
    
    private Order createOrderWithPromo(String promoId) {
        Set<String> promos = new HashSet<>();
        promos.add(promoId);
        return new Order("order123", new BigDecimal("100.00"), promos);
    }
    
    private Order createOrderWithoutPromos() {
        return new Order("order123", new BigDecimal("100.00"), new HashSet<>());
    }
    
    private Wallet createWalletWithCard(String cardId) {
        List<CardMethod> cardMethods = new ArrayList<>();
        cardMethods.add(new CardMethod(cardId, new BigDecimal("0.10"), new BigDecimal("1000.00")));
        return new Wallet(cardMethods, null);
    }
    
    private PaymentScenario createBaseScenario(Order order) {
        return new PaymentScenario(order, null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }
}