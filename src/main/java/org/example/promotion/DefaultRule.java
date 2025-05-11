package org.example.promotion;

import org.example.order.Order;
import org.example.order.PaymentScenario;
import org.example.order.Wallet;

import java.math.BigDecimal;

/**
 * A default promotion rule that applies to all orders, regardless of promotions.
 * This rule is used as a fallback when no other rules apply.
 */
public class DefaultRule implements PromotionRule {

    /**
     * Constructor for DefaultRule.
     */
    public DefaultRule() {
        // No parameters needed for this rule
    }

    @Override
    public boolean isApplicable(Order o, Wallet w, PaymentScenario base) {
        // This rule applies to all orders
        return true;
    }

    @Override
    public BigDecimal computeDiscount(Order o, Wallet w, PaymentScenario base) {
        // No discount for this rule
        return BigDecimal.ZERO;
    }
}