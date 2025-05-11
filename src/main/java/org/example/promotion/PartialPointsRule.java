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
        // 2. The points method has some limit (but not enough to cover the entire order)
        // 3. The base scenario doesn't already use points
        
        if (w.getPointsMethod() == null || base.usesPoints()) {
            return false;
        }
        
        BigDecimal pointsLimit = w.getPointsMethod().getRemainingLimit();
        
        // Check if there are some points available but not enough to cover the entire order
        return pointsLimit.compareTo(BigDecimal.ZERO) > 0 && 
               pointsLimit.compareTo(o.getValue()) < 0;
    }

    @Override
    public BigDecimal computeDiscount(Order o, Wallet w, PaymentScenario base) {
        if (!isApplicable(o, w, base)) {
            return BigDecimal.ZERO;
        }
        
        // The discount is based on the available points and their discount percentage
        BigDecimal pointsLimit = w.getPointsMethod().getRemainingLimit();
        
        // Apply the discount percentage only to the portion covered by points
        return pointsLimit.multiply(w.getPointsMethod().getDiscountPercent());
    }
}