package org.example.promotion;

import java.math.BigDecimal;
import org.example.order.Order;
import org.example.order.Wallet;
import org.example.order.PaymentScenario;

/**
 * Interface for promotion rules that determine if a promotion is applicable
 * and compute the discount amount.
 */
public interface PromotionRule {
    /**
     * Determines if this promotion rule is applicable to the given order, wallet,
     * and base payment scenario.
     *
     * @param o The order
     * @param w The wallet containing payment methods
     * @param base The base payment scenario
     * @return true if the rule is applicable, false otherwise
     */
    boolean isApplicable(Order o, Wallet w, PaymentScenario base);

    /**
     * Computes the discount amount for the given order, wallet, and base payment scenario.
     *
     * @param o The order
     * @param w The wallet containing payment methods
     * @param base The base payment scenario
     * @return The computed discount amount
     */
    BigDecimal computeDiscount(Order o, Wallet w, PaymentScenario base);
}
