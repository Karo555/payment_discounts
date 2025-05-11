package org.example.order;

import org.example.payment.CardMethod;
import org.example.payment.PointsMethod;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aggregates all of a customer's payment methods and exposes global limits.
 */
public class Wallet {
    private final List<CardMethod> cardMethods;
    private final PointsMethod pointsMethod;

    /**
     * Constructor for Wallet.
     *
     * @param cardMethods The list of card payment methods
     * @param pointsMethod The points payment method
     */
    public Wallet(List<CardMethod> cardMethods, PointsMethod pointsMethod) {
        this.cardMethods = cardMethods != null ? 
            Collections.unmodifiableList(new ArrayList<>(cardMethods)) : 
            Collections.emptyList();
        this.pointsMethod = pointsMethod;
    }

    /**
     * @return The list of card payment methods
     */
    public List<CardMethod> getCardMethods() {
        return cardMethods;
    }

    /**
     * @return The points payment method
     */
    public PointsMethod getPointsMethod() {
        return pointsMethod;
    }

    /**
     * Calculates the total remaining limit across all card methods.
     *
     * @return The total remaining card limit
     */
    public BigDecimal totalRemainingCardLimit() {
        BigDecimal total = BigDecimal.ZERO;
        for (CardMethod cardMethod : cardMethods) {
            total = total.add(cardMethod.getRemainingLimit());
        }
        return total;
    }

    /**
     * @return The total remaining points (the remaining limit of the points method)
     */
    public BigDecimal totalRemainingPoints() {
        return pointsMethod != null ? pointsMethod.getRemainingLimit() : BigDecimal.ZERO;
    }
}