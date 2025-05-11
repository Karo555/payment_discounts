package org.example.order;

import org.example.payment.CardMethod;
import org.example.payment.PaymentMethod;
import org.example.payment.PointsMethod;
import org.example.promotion.PromotionRule;
import org.example.report.SummaryReporter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Processes orders by assigning optimal payment methods and updating the wallet.
 * This class orchestrates the end-to-end order-to-payment resolution process.
 * 
 * This class is thread-safe for concurrent calls to processOrders, but not for
 * concurrent modifications to the same Wallet instance.
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
     * @param summaryReporter The summary reporter to use
     */
    public OrderProcessor(PaymentScenarioEvaluator evaluator, List<PromotionRule> rules, SummaryReporter summaryReporter) {
        if (evaluator == null || rules == null || summaryReporter == null) {
            throw new IllegalArgumentException("Evaluator, rules, and summaryReporter cannot be null");
        }
        this.evaluator = evaluator;
        this.rules = Collections.unmodifiableList(new ArrayList<>(rules));
        this.finalAllocations = new CopyOnWriteArrayList<>();
        this.paymentTotals = new HashMap<>();
        this.summaryReporter = summaryReporter;
    }

    /**
     * Constructor for backward compatibility.
     *
     * @param evaluator The payment scenario evaluator to use
     * @param rules The list of promotion rules to apply
     */
    public OrderProcessor(PaymentScenarioEvaluator evaluator, List<PromotionRule> rules) {
        this(evaluator, rules, new SummaryReporter());
    }

    /**
     * Processes a list of orders using a wallet, assigning optimal payment methods and updating the wallet.
     *
     * @param orders The list of orders to process
     * @param wallet The wallet containing payment methods
     * @return The list of final payment allocations
     * @throws IllegalArgumentException if orders or wallet is null
     */
    public List<PaymentScenario> processOrders(List<Order> orders, Wallet wallet) {
        if (orders == null || wallet == null) {
            throw new IllegalArgumentException("Orders and wallet cannot be null");
        }

        // Create a new list for this processing run
        List<PaymentScenario> currentAllocations = new ArrayList<>();
        List<String> failedOrders = new ArrayList<>();

        // Process each order
        for (Order order : orders) {
            if (order == null) {
                continue;
            }

            try {
                // Find the optimal payment scenario for this order
                PaymentScenario optimalScenario = evaluator.evaluatePaymentScenarios(order, wallet, rules);

                // Update the wallet based on the payment scenario
                updateWallet(wallet, optimalScenario);

                // Add the scenario to the current allocations
                currentAllocations.add(optimalScenario);
            } catch (Exception e) {
                // Log the error and continue with the next order
                String errorMessage = "Error processing order " + order.getId() + ": " + e.getMessage();
                System.err.println(errorMessage);
                failedOrders.add(order.getId() + " - " + e.getMessage());
            }
        }

        // Update the final allocations in a thread-safe way
        synchronized (this) {
            finalAllocations.clear();
            finalAllocations.addAll(currentAllocations);
        }

        // Print the payment summary
        if (!currentAllocations.isEmpty()) {
            summaryReporter.printPaymentSummary(currentAllocations);
        }

        // Log failed orders
        if (!failedOrders.isEmpty()) {
            System.err.println("Failed to process " + failedOrders.size() + " orders:");
            for (String failedOrder : failedOrders) {
                System.err.println("  - " + failedOrder);
            }
        }

        return new ArrayList<>(currentAllocations);
    }

    /**
     * Updates the wallet based on a payment scenario.
     *
     * @param wallet The wallet to update
     * @param scenario The payment scenario to apply
     * @throws IllegalArgumentException if the wallet or scenario is null, or if there's an issue with deducting amounts
     */
    private void updateWallet(Wallet wallet, PaymentScenario scenario) {
        if (wallet == null || scenario == null) {
            throw new IllegalArgumentException("Wallet and scenario cannot be null");
        }

        try {
            // Deduct points if used
            if (scenario.usesPoints()) {
                PaymentMethod pointsMethod = scenario.getPointsMethod();
                if (pointsMethod == null) {
                    pointsMethod = wallet.getPointsMethod();
                }

                if (pointsMethod != null) {
                    BigDecimal pointsUsed = scenario.getPointsUsed();
                    if (pointsUsed.compareTo(BigDecimal.ZERO) > 0) {
                        pointsMethod.deductAmount(pointsUsed);
                    }
                }
            }

            // Deduct card amount if used
            if (scenario.usesCard()) {
                PaymentMethod cardMethod = scenario.getCardMethod();
                if (cardMethod != null) {
                    BigDecimal cardCharge = scenario.getCardCharge();
                    if (cardCharge.compareTo(BigDecimal.ZERO) > 0) {
                        cardMethod.deductAmount(cardCharge);
                    }
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error updating wallet: " + e.getMessage(), e);
        }
    }

    /**
     * @return The list of final payment allocations
     */
    public List<PaymentScenario> getFinalAllocations() {
        return new ArrayList<>(finalAllocations);
    }
}
