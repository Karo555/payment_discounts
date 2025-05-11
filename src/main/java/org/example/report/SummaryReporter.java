package org.example.report;

import org.example.order.PaymentScenario;
import org.example.payment.CardMethod;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Aggregates and formats payment summaries from a list of payment scenarios.
 * This component is responsible for calculating the total amount spent per payment method
 * and formatting the results as required.
 */
public class SummaryReporter {

    private static final String POINTS_ID = "PUNKTY";
    private static final String POINTS_DISPLAY_ID = "POINTS";

    /**
     * Aggregates payment information from a list of payment scenarios and prints a summary.
     *
     * @param scenarios The list of payment scenarios to aggregate
     */
    public void printPaymentSummary(List<PaymentScenario> scenarios) {
        if (scenarios == null || scenarios.isEmpty()) {
            System.out.println("No payment scenarios to report.");
            return;
        }

        Map<String, BigDecimal> paymentTotals = aggregatePaymentTotals(scenarios);
        formatAndPrintResults(paymentTotals);
    }

    /**
     * Aggregates the total amount spent per payment method.
     *
     * @param scenarios The list of payment scenarios to aggregate
     * @return A map of payment method IDs to total amounts
     */
    private Map<String, BigDecimal> aggregatePaymentTotals(List<PaymentScenario> scenarios) {
        Map<String, BigDecimal> paymentTotals = new HashMap<>();

        for (PaymentScenario scenario : scenarios) {
            // Handle card payments
            if (scenario.usesCard()) {
                CardMethod cardMethod = getCardMethod(scenario);
                if (cardMethod != null) {
                    String cardId = cardMethod.getId();
                    BigDecimal cardAmount = getCardAmount(scenario);

                    // Add to existing total or create new entry
                    paymentTotals.put(cardId, 
                        paymentTotals.getOrDefault(cardId, BigDecimal.ZERO).add(cardAmount));
                }
            }

            // Handle points payments
            if (scenario.usesPoints()) {
                BigDecimal pointsAmount = getPointsAmount(scenario);

                // Add to existing total or create new entry
                paymentTotals.put(POINTS_ID, 
                    paymentTotals.getOrDefault(POINTS_ID, BigDecimal.ZERO).add(pointsAmount));
            }
        }

        return paymentTotals;
    }

    /**
     * Formats and prints the payment totals.
     *
     * @param paymentTotals A map of payment method IDs to total amounts
     */
    private void formatAndPrintResults(Map<String, BigDecimal> paymentTotals) {
        // First print card payments (non-POINTS payments)
        for (Map.Entry<String, BigDecimal> entry : paymentTotals.entrySet()) {
            String paymentId = entry.getKey();
            if (!POINTS_ID.equals(paymentId)) {
                BigDecimal amount = entry.getValue().setScale(2, RoundingMode.HALF_UP);
                System.out.println(paymentId + " " + amount);
            }
        }

        // Then print points payment if it exists
        if (paymentTotals.containsKey(POINTS_ID)) {
            BigDecimal amount = paymentTotals.get(POINTS_ID).setScale(2, RoundingMode.HALF_UP);
            System.out.println(POINTS_DISPLAY_ID + " " + amount);
        }
    }

    /**
     * Gets the card method from a payment scenario.
     *
     * @param scenario The payment scenario
     * @return The card method, or null if not available
     */
    private CardMethod getCardMethod(PaymentScenario scenario) {
        return scenario.getCardMethod();
    }

    /**
     * Gets the amount charged to the card from a payment scenario.
     *
     * @param scenario The payment scenario
     * @return The card charge amount
     */
    private BigDecimal getCardAmount(PaymentScenario scenario) {
        return scenario.getCardCharge();
    }

    /**
     * Gets the amount of points used from a payment scenario.
     *
     * @param scenario The payment scenario
     * @return The points used amount
     */
    private BigDecimal getPointsAmount(PaymentScenario scenario) {
        return scenario.getPointsUsed();
    }
}
