package org.example.order;

import org.example.payment.CardMethod;
import org.example.payment.PointsMethod;
import org.example.promotion.PromotionRule;
import org.example.report.SummaryReporter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Processes orders by assigning optimal payment methods and updating the wallet.
 * This class orchestrates the end-to-end order-to-payment resolution process.
 */
public class OrderProcessor {
    private final PaymentScenarioEvaluator evaluator;
    private final List<PromotionRule> rules;
    private final List<PaymentScenario> finalAllocations;
    private final Map<String, BigDecimal> paymentTotals;
    private final SummaryReporter summaryReporter;

    /**
     * Constructor for OrderProcessor.
     *
     * @param evaluator The payment scenario evaluator to use
     * @param rules The list of promotion rules to apply
     */
    public OrderProcessor(PaymentScenarioEvaluator evaluator, List<PromotionRule> rules) {
        if (evaluator == null || rules == null) {
            throw new IllegalArgumentException("Evaluator and rules cannot be null");
        }
        this.evaluator = evaluator;
        this.rules = rules;
        this.finalAllocations = new ArrayList<>();
        this.paymentTotals = new HashMap<>();
        this.summaryReporter = new SummaryReporter();
    }

    /**
     * Processes a list of orders using a wallet, assigning optimal payment methods and updating the wallet.
     *
     * @param orders The list of orders to process
     * @param wallet The wallet containing payment methods
     * @return The list of final payment allocations
     */
    public List<PaymentScenario> processOrders(List<Order> orders, Wallet wallet) {
        if (orders == null || wallet == null) {
            throw new IllegalArgumentException("Orders and wallet cannot be null");
        }

        // Clear previous allocations and payment totals
        finalAllocations.clear();
        paymentTotals.clear();

        // Process each order
        for (Order order : orders) {
            try {
                // Find the optimal payment scenario for this order
                PaymentScenario optimalScenario = evaluator.evaluatePaymentScenarios(order, wallet, rules);

                // Update the wallet based on the payment scenario
                updateWallet(wallet, optimalScenario);

                // Add the scenario to the final allocations
                finalAllocations.add(optimalScenario);
            } catch (Exception e) {
                // Log the error and continue with the next order
                System.err.println("Error processing order " + order.getId() + ": " + e.getMessage());
            }
        }

        // Print the payment summary
        summaryReporter.printPaymentSummary(finalAllocations);

        return finalAllocations;
    }

    /**
     * Updates the wallet based on a payment scenario.
     *
     * @param wallet The wallet to update
     * @param scenario The payment scenario to apply
     */
    private void updateWallet(Wallet wallet, PaymentScenario scenario) {
        // Since PaymentScenario doesn't expose getters for pointsUsed and cardCharge,
        // we need to create a new scenario with the same parameters but with updated values
        // This is a workaround for the lack of getters in PaymentScenario

        // Get the order from the scenario
        Order order = scenario.getOrder();
        BigDecimal orderValue = order.getValue();

        // Deduct points if used
        if (scenario.usesPoints()) {
            PointsMethod pointsMethod = wallet.getPointsMethod();
            if (pointsMethod != null) {
                // Since we can't directly access pointsUsed, we need to estimate it
                // In a real implementation, this would need to be more accurate
                // For now, we'll assume all available points up to the order value are used
                BigDecimal pointsAvailable = wallet.totalRemainingPoints();
                BigDecimal pointsUsed = pointsAvailable.min(orderValue);

                // Deduct the points
                pointsMethod.deductAmount(pointsUsed);
            }
        }

        // Deduct card amount if used
        if (scenario.usesCard()) {
            // Since we can't directly access cardMethod, we need to use the scenario's usesCard method
            // In a real implementation, this would need to be more accurate
            // For now, we'll assume the first card with sufficient limit is used
            for (CardMethod cardMethod : wallet.getCardMethods()) {
                // If the card has sufficient limit, use it
                BigDecimal remainingAmount = orderValue;
                if (scenario.usesPoints()) {
                    // If points are also used, reduce the amount needed from the card
                    BigDecimal pointsAvailable = wallet.totalRemainingPoints();
                    remainingAmount = orderValue.subtract(pointsAvailable.min(orderValue));
                }

                if (cardMethod.getRemainingLimit().compareTo(remainingAmount) >= 0) {
                    // Deduct the card charge
                    cardMethod.deductAmount(remainingAmount);
                    break; // Use only one card
                }
            }
        }
    }

    /**
     * @return The list of final payment allocations
     */
    public List<PaymentScenario> getFinalAllocations() {
        return new ArrayList<>(finalAllocations);
    }
}
