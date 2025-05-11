package org.example.promotion;

import org.example.order.Order;
import org.example.order.PaymentScenario;
import org.example.order.Wallet;

import java.math.BigDecimal;

/**
 * A promotion rule that applies a full discount when points are used to pay for the entire order.
 */
public class FullPointsRule implements PromotionRule {

    /**
     * Constructor for FullPointsRule.
     */
    public FullPointsRule() {
        // No parameters needed for this rule
    }

    @Override
    public boolean isApplicable(Order o, Wallet w, PaymentScenario base) {
        // Rule applies if:
        // 1. The wallet has a points method
        // 2. The points method has sufficient limit to cover the entire order
        // 3. The base scenario doesn't already use points
        
        if (w.getPointsMethod() == null || base.usesPoints()) {
            return false;
        }
        
        // Check if there are enough points to cover the order
        return w.getPointsMethod().getRemainingLimit().compareTo(o.getValue()) >= 0;
    }

    @Override
    public BigDecimal computeDiscount(Order o, Wallet w, PaymentScenario base) {
        if (!isApplicable(o, w, base)) {
            return BigDecimal.ZERO;
        }
        
        // Apply the discount percentage from the points method
        return o.getValue().multiply(w.getPointsMethod().getDiscountPercent());
    }
}