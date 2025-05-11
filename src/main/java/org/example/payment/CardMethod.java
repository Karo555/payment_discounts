package org.example.payment;

import java.math.BigDecimal;

/**
 * Represents a card payment method.
 */
public class CardMethod implements PaymentMethod {
    private final String id;
    private final BigDecimal discountPercent;
    private BigDecimal remainingLimit;

    /**
     * Constructor for CardMethod.
     *
     * @param id The card method ID (e.g. "mZysk")
     * @param discountPercent The discount percentage for this card
     * @param remainingLimit The remaining limit available for this card
     */
    public CardMethod(String id, BigDecimal discountPercent, BigDecimal remainingLimit) {
        this.id = id;
        this.discountPercent = discountPercent;
        this.remainingLimit = remainingLimit;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    @Override
    public BigDecimal getRemainingLimit() {
        return remainingLimit;
    }

    @Override
    public void deductAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be non-negative");
        }

        BigDecimal newLimit = remainingLimit.subtract(amount);
        if (newLimit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Insufficient limit available");
        }

        this.remainingLimit = newLimit;
    }

    @Override
    public boolean isCard() {
        return true;
    }
}
