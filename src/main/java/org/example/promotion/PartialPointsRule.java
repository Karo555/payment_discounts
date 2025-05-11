package org.example.promotion;

import org.example.order.Order;
import org.example.order.PaymentScenario;
import org.example.order.Wallet;

import java.math.BigDecimal;

/**
 * A promotion rule that applies a partial discount when points are used to pay for part of the order.
 */
public class PartialPointsRule implements PromotionRule {

    /**
     * Constructor for PartialPointsRule.
     */
    public PartialPointsRule() {
        // No parameters needed for this rule
    }

    @Override
    public boolean isApplicable(Order o, Wallet w, PaymentScenario base) {
        // Rule applies if:
        // 1. The wallet has a points method
        // 2. The points method has some limit (at least 10% of order value but not enough to cover the entire order)
        // 3. The base scenario doesn't already use points

        if (w.getPointsMethod() == null || base.usesPoints()) {
            return false;
        }

        BigDecimal pointsLimit = w.getPointsMethod().getRemainingLimit();
        BigDecimal tenPercentOfOrder = o.getValue().multiply(new BigDecimal("0.1"));

        // Check if there are enough points to cover at least 10% of the order
        // but not enough to cover the entire order
        return pointsLimit.compareTo(tenPercentOfOrder) >= 0 && 
               pointsLimit.compareTo(o.getValue()) < 0;
    }

    @Override
    public BigDecimal computeDiscount(Order o, Wallet w, PaymentScenario base) {
        if (!isApplicable(o, w, base)) {
            return BigDecimal.ZERO;
        }

        // If at least 10% of the order is paid with points, apply a 10% discount to the entire order
        return o.getValue().multiply(new BigDecimal("0.1"));
    }
}
