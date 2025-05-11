package org.example.promotion;

import org.example.order.Order;
import org.example.order.PaymentScenario;
import org.example.order.Wallet;
import org.example.payment.CardMethod;

import java.math.BigDecimal;

/**
 * A promotion rule that applies a full discount when a specific card method is used.
 */
public class FullCardRule implements PromotionRule {
    private final String cardMethodId;

    /**
     * Constructor for FullCardRule.
     *
     * @param cardMethodId The ID of the card method this rule applies to
     */
    public FullCardRule(String cardMethodId) {
        this.cardMethodId = cardMethodId;
    }

    @Override
    public boolean isApplicable(Order o, Wallet w, PaymentScenario base) {
        // Rule applies if:
        // 1. The order has a card promotion for this card method
        // 2. The wallet contains a card method with this ID
        // 3. The base scenario doesn't already use a card
        
        if (!o.hasCardPromotion(cardMethodId) || base.usesCard()) {
            return false;
        }
        
        // Check if the wallet contains a card with this ID
        for (CardMethod card : w.getCardMethods()) {
            if (card.getId().equals(cardMethodId)) {
                // Check if the card has sufficient limit
                return card.getRemainingLimit().compareTo(o.getValue()) >= 0;
            }
        }
        
        return false;
    }

    @Override
    public BigDecimal computeDiscount(Order o, Wallet w, PaymentScenario base) {
        if (!isApplicable(o, w, base)) {
            return BigDecimal.ZERO;
        }
        
        // Find the card method with the matching ID
        for (CardMethod card : w.getCardMethods()) {
            if (card.getId().equals(cardMethodId)) {
                // Apply the discount percentage from the card
                return o.getValue().multiply(card.getDiscountPercent());
            }
        }
        
        return BigDecimal.ZERO;
    }
}