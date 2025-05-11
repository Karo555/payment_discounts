package org.example;

import org.example.order.Order;
import org.example.order.PaymentScenario;
import org.example.order.PaymentScenarioEvaluator;
import org.example.order.Wallet;
import org.example.payment.CardMethod;
import org.example.payment.PaymentMethod;
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
 * Tests for the PaymentScenarioEvaluator class.
 */
class PaymentScenarioEvaluatorTest {

    private PaymentScenarioEvaluator evaluator;
    private Order order;
    private Wallet wallet;
    private List<PromotionRule> rules;
    private CardMethod card1;
    private CardMethod card2;
    private PointsMethod pointsMethod;

    @BeforeEach
    void setUp() {
        evaluator = new PaymentScenarioEvaluator();

        // Create cards with different discount percentages
        card1 = new CardMethod("card1", new BigDecimal("0.10"), new BigDecimal("1000.00"));
        card2 = new CardMethod("card2", new BigDecimal("0.15"), new BigDecimal("500.00"));

        // Create points method
        pointsMethod = new PointsMethod(new BigDecimal("0.20"), new BigDecimal("200.00"));

        // Create wallet with cards and points
        List<CardMethod> cardMethods = Arrays.asList(card1, card2);
        wallet = Wallet.createWithCards(cardMethods, pointsMethod);

        // Create order with eligible promotions
        Set<String> eligiblePromos = new HashSet<>(Arrays.asList("card1", "card2"));
        order = new Order("order123", new BigDecimal("300.00"), eligiblePromos);

        // Create promotion rules
        rules = new ArrayList<>();
        rules.add(new FullCardRule("card1"));
        rules.add(new FullCardRule("card2"));
        rules.add(new FullPointsRule());
        rules.add(new PartialPointsRule());
    }

    @Test
    @DisplayName("Should throw exception when parameters are null")
    void shouldThrowExceptionWhenParametersAreNull() {
        assertThrows(IllegalArgumentException.class, () -> evaluator.evaluatePaymentScenarios(null, wallet, rules));
        assertThrows(IllegalArgumentException.class, () -> evaluator.evaluatePaymentScenarios(order, null, rules));
        assertThrows(IllegalArgumentException.class, () -> evaluator.evaluatePaymentScenarios(order, wallet, null));
    }

    @Test
    @DisplayName("Should prefer full points payment when it gives the highest discount")
    void shouldPreferFullPointsPaymentWhenItGivesHighestDiscount() {
        // Arrange: Order value is less than available points, and points discount is highest
        Order smallOrder = new Order("small123", new BigDecimal("150.00"), Collections.emptySet());

        // Act
        PaymentScenario result = evaluator.evaluatePaymentScenarios(smallOrder, wallet, rules);

        // Assert
        assertFalse(result.usesCard());
        assertTrue(result.usesPoints());
        // Use compareTo for BigDecimal comparison to avoid precision issues
        assertEquals(0, new BigDecimal("30.00").compareTo(result.getDiscountValue())); // 20% of 150 = 30
    }

    @Test
    @DisplayName("Should prefer card with highest discount when points are insufficient")
    void shouldPreferCardWithHighestDiscountWhenPointsAreInsufficient() {
        // Arrange: Order value is more than available points, and card2 has higher discount
        Order largeOrder = new Order("large123", new BigDecimal("400.00"), 
                                    new HashSet<>(Arrays.asList("card1", "card2")));

        // Debug: Print card discount percentages
        System.out.println("[DEBUG_LOG] Card1 discount: " + card1.getDiscountPercent());
        System.out.println("[DEBUG_LOG] Card2 discount: " + card2.getDiscountPercent());

        // Act
        PaymentScenario result = evaluator.evaluatePaymentScenarios(largeOrder, wallet, rules);

        // Debug: Print result details
        System.out.println("[DEBUG_LOG] Result uses card: " + result.usesCard());
        System.out.println("[DEBUG_LOG] Result uses points: " + result.usesPoints());
        System.out.println("[DEBUG_LOG] Result discount value: " + result.getDiscountValue());

        // Assert
        assertTrue(result.usesCard());
        assertFalse(result.usesPoints());
        // Use compareTo for BigDecimal comparison to avoid precision issues
        assertEquals(0, new BigDecimal("60.00").compareTo(result.getDiscountValue())); // 15% of 400 = 60
    }

    @Test
    @DisplayName("Should prefer points over card when discount is equal")
    void shouldPreferPointsOverCardWhenDiscountIsEqual() {
        // Arrange: Create a scenario where points and card give the same discount
        CardMethod equalCard = new CardMethod("equalCard", new BigDecimal("0.20"), new BigDecimal("1000.00"));
        Wallet equalWallet = Wallet.createWithCards(Collections.singletonList(equalCard), pointsMethod);
        Order smallOrder = new Order("small123", new BigDecimal("150.00"), 
                                    new HashSet<>(Collections.singletonList("equalCard")));
        List<PromotionRule> equalRules = Arrays.asList(new FullCardRule("equalCard"), new FullPointsRule());

        // Act
        PaymentScenario result = evaluator.evaluatePaymentScenarios(smallOrder, equalWallet, equalRules);

        // Assert
        assertFalse(result.usesCard());
        assertTrue(result.usesPoints());
        // Use compareTo for BigDecimal comparison to avoid precision issues
        assertEquals(0, new BigDecimal("30.00").compareTo(result.getDiscountValue())); // 20% of 150 = 30
    }

    @Test
    @DisplayName("Should create mixed scenario when points are partially sufficient")
    void shouldCreateMixedScenarioWhenPointsArePartiallySufficient() {
        // Arrange: Order value is more than available points but less than points + card
        Order mediumOrder = new Order("medium123", new BigDecimal("300.00"), 
                                     new HashSet<>(Arrays.asList("card1", "card2")));

        // Act
        PaymentScenario result = evaluator.evaluatePaymentScenarios(mediumOrder, wallet, rules);

        // Assert
        assertTrue(result.usesCard());
        assertTrue(result.usesPoints());

        // Expected discount: 10% of 300 (entire order) = 30
        // Use compareTo for BigDecimal comparison to avoid precision issues
        assertEquals(0, new BigDecimal("30.00").compareTo(result.getDiscountValue()));
    }

    @Test
    @DisplayName("Should throw exception when no valid payment scenarios are found")
    void shouldThrowExceptionWhenNoValidPaymentScenariosAreFound() {
        // Arrange: Order value is more than available payment methods
        Order hugeOrder = new Order("huge123", new BigDecimal("2000.00"), Collections.emptySet());
        Wallet emptyWallet = Wallet.createWithCards(Collections.emptyList(), null);

        // Act & Assert
        assertThrows(IllegalStateException.class, 
                    () -> evaluator.evaluatePaymentScenarios(hugeOrder, emptyWallet, rules));
    }
}
