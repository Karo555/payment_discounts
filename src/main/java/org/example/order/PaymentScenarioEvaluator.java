package org.example.order;

import org.example.payment.CardMethod;
import org.example.payment.PointsMethod;
import org.example.promotion.PromotionRule;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Evaluates payment scenarios for an order based on available payment methods and promotion rules.
 * This is the core decision engine that encapsulates constraint logic and preference handling.
 */
public class PaymentScenarioEvaluator {

    /**
     * Evaluates all possible payment scenarios for an order and selects the optimal one.
     * The optimal scenario maximizes discount and minimizes card usage as a tie-breaker.
     *
     * @param order The order to evaluate payment scenarios for
     * @param wallet The wallet containing available payment methods
     * @param rules The list of applicable promotion rules
     * @return The optimal payment scenario
     */
    public PaymentScenario evaluatePaymentScenarios(Order order, Wallet wallet, List<PromotionRule> rules) {
        if (order == null || wallet == null || rules == null) {
            throw new IllegalArgumentException("Order, wallet, and rules cannot be null");
        }

        List<PaymentScenario> validScenarios = generateValidPaymentScenarios(order, wallet, rules);

        if (validScenarios.isEmpty()) {
            throw new IllegalStateException("No valid payment scenarios found for order: " + order.getId());
        }

        return selectOptimalScenario(validScenarios);
    }

    /**
     * Generates all valid payment scenarios for an order based on available payment methods and promotion rules.
     *
     * @param order The order to generate payment scenarios for
     * @param wallet The wallet containing available payment methods
     * @param rules The list of applicable promotion rules
     * @return A list of valid payment scenarios
     */
    private List<PaymentScenario> generateValidPaymentScenarios(Order order, Wallet wallet, List<PromotionRule> rules) {
        List<PaymentScenario> scenarios = new ArrayList<>();
        BigDecimal orderValue = order.getValue();

        // Scenario 1: Full payment with points
        if (wallet.getPointsMethod() != null && wallet.totalRemainingPoints().compareTo(orderValue) >= 0) {
            PaymentScenario pointsScenario = createFullPointsScenario(order, wallet, rules);
            scenarios.add(pointsScenario);
        }

        // Scenario 2: Full payment with each available card
        for (CardMethod cardMethod : wallet.getCardMethods()) {
            if (cardMethod.getRemainingLimit().compareTo(orderValue) >= 0) {
                PaymentScenario cardScenario = createFullCardScenario(order, wallet, cardMethod, rules);
                scenarios.add(cardScenario);
            }
        }

        // Scenario 3: Partial payment with points, remainder with card
        if (wallet.getPointsMethod() != null && wallet.totalRemainingPoints().compareTo(BigDecimal.ZERO) > 0) {
            for (CardMethod cardMethod : wallet.getCardMethods()) {
                BigDecimal pointsAvailable = wallet.totalRemainingPoints();
                BigDecimal remainingAmount = orderValue.subtract(pointsAvailable);

                if (remainingAmount.compareTo(BigDecimal.ZERO) > 0 && 
                    cardMethod.getRemainingLimit().compareTo(remainingAmount) >= 0) {
                    PaymentScenario mixedScenario = createMixedScenario(order, wallet, cardMethod, pointsAvailable, remainingAmount, rules);
                    scenarios.add(mixedScenario);
                }
            }
        }

        return scenarios;
    }

    /**
     * Creates a payment scenario where the order is paid entirely with points.
     *
     * @param order The order being paid
     * @param wallet The wallet containing payment methods
     * @param rules The list of applicable promotion rules
     * @return A payment scenario for full points payment
     */
    private PaymentScenario createFullPointsScenario(Order order, Wallet wallet, List<PromotionRule> rules) {
        BigDecimal orderValue = order.getValue();
        PointsMethod pointsMethod = wallet.getPointsMethod();

        // Calculate discount directly using the points method's discount percentage
        // This is what FullPointsRule would do if it were applicable
        BigDecimal discount = orderValue.multiply(pointsMethod.getDiscountPercent());

        // Debug logging
        System.out.println("[DEBUG_LOG] Creating points scenario");
        System.out.println("[DEBUG_LOG] Points discount percent: " + pointsMethod.getDiscountPercent());
        System.out.println("[DEBUG_LOG] Order value: " + orderValue);
        System.out.println("[DEBUG_LOG] Direct discount calculation: " + discount);

        return new PaymentScenario(order, null, orderValue, BigDecimal.ZERO, discount);
    }

    /**
     * Creates a payment scenario where the order is paid entirely with a card.
     *
     * @param order The order being paid
     * @param wallet The wallet containing payment methods
     * @param cardMethod The card method to use
     * @param rules The list of applicable promotion rules
     * @return A payment scenario for full card payment
     */
    private PaymentScenario createFullCardScenario(Order order, Wallet wallet, CardMethod cardMethod, List<PromotionRule> rules) {
        BigDecimal orderValue = order.getValue();

        // Create an empty base scenario (no card, no points) to allow FullCardRule to be applicable
        PaymentScenario baseScenario = new PaymentScenario(order, null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

        // Calculate discount directly using the card's discount percentage
        // This is what FullCardRule would do if it were applicable
        BigDecimal discount = orderValue.multiply(cardMethod.getDiscountPercent());

        // Debug logging
        System.out.println("[DEBUG_LOG] Creating card scenario with card ID: " + cardMethod.getId());
        System.out.println("[DEBUG_LOG] Card discount percent: " + cardMethod.getDiscountPercent());
        System.out.println("[DEBUG_LOG] Order value: " + orderValue);
        System.out.println("[DEBUG_LOG] Direct discount calculation: " + discount);

        return new PaymentScenario(order, cardMethod, BigDecimal.ZERO, orderValue, discount);
    }

    /**
     * Creates a payment scenario where the order is paid partially with points and partially with a card.
     *
     * @param order The order being paid
     * @param wallet The wallet containing payment methods
     * @param cardMethod The card method to use
     * @param pointsAmount The amount to pay with points
     * @param cardAmount The amount to pay with the card
     * @param rules The list of applicable promotion rules
     * @return A payment scenario for mixed payment
     */
    private PaymentScenario createMixedScenario(Order order, Wallet wallet, CardMethod cardMethod, 
                                              BigDecimal pointsAmount, BigDecimal cardAmount, List<PromotionRule> rules) {
        // Calculate discount directly
        // Points portion gets points discount, card portion gets card discount
        BigDecimal pointsDiscount = pointsAmount.multiply(wallet.getPointsMethod().getDiscountPercent());
        BigDecimal cardDiscount = cardAmount.multiply(cardMethod.getDiscountPercent());
        BigDecimal totalDiscount = pointsDiscount.add(cardDiscount);

        // Debug logging
        System.out.println("[DEBUG_LOG] Creating mixed scenario with card ID: " + cardMethod.getId());
        System.out.println("[DEBUG_LOG] Points amount: " + pointsAmount + ", Card amount: " + cardAmount);
        System.out.println("[DEBUG_LOG] Points discount: " + pointsDiscount + ", Card discount: " + cardDiscount);
        System.out.println("[DEBUG_LOG] Total discount: " + totalDiscount);

        return new PaymentScenario(order, cardMethod, pointsAmount, cardAmount, totalDiscount);
    }

    /**
     * Calculates the total discount for a payment scenario based on applicable promotion rules.
     *
     * @param order The order being paid
     * @param wallet The wallet containing payment methods
     * @param baseScenario The base payment scenario
     * @param rules The list of applicable promotion rules
     * @return The total discount amount
     */
    private BigDecimal calculateTotalDiscount(Order order, Wallet wallet, PaymentScenario baseScenario, List<PromotionRule> rules) {
        BigDecimal totalDiscount = BigDecimal.ZERO;

        System.out.println("[DEBUG_LOG] Calculating discount for scenario - Uses Card: " + baseScenario.usesCard() + ", Uses Points: " + baseScenario.usesPoints());

        for (PromotionRule rule : rules) {
            if (rule.isApplicable(order, wallet, baseScenario)) {
                BigDecimal ruleDiscount = rule.computeDiscount(order, wallet, baseScenario);
                System.out.println("[DEBUG_LOG] Rule " + rule.getClass().getSimpleName() + " is applicable, discount: " + ruleDiscount);
                totalDiscount = totalDiscount.add(ruleDiscount);
            } else {
                System.out.println("[DEBUG_LOG] Rule " + rule.getClass().getSimpleName() + " is not applicable");
            }
        }

        System.out.println("[DEBUG_LOG] Total discount: " + totalDiscount);
        return totalDiscount;
    }

    /**
     * Selects the optimal payment scenario from a list of valid scenarios.
     * The optimal scenario maximizes discount and minimizes card usage as a tie-breaker.
     *
     * @param scenarios The list of valid payment scenarios
     * @return The optimal payment scenario
     */
    private PaymentScenario selectOptimalScenario(List<PaymentScenario> scenarios) {
        return scenarios.stream()
            .max(Comparator
                .comparing(PaymentScenario::getDiscountValue)
                .thenComparing(scenario -> scenario.usesCard() ? 0 : 1)) // Prefer points (1) over cards (0) when discount is equal
            .orElseThrow(() -> new IllegalStateException("No valid payment scenarios found"));
    }
}
