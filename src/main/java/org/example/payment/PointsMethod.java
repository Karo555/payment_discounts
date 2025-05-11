package org.example.payment;

import java.math.BigDecimal;

/**
 * Represents a points payment method.
 */
public class PointsMethod implements PaymentMethod {
    private static final String POINTS_ID = "POINTS";
    private final BigDecimal discountPercent;
    private BigDecimal remainingLimit;

    /**
     * Constructor for PointsMethod.
     *
     * @param discountPercent The full-points discount percentage
     * @param remainingLimit The available points-converted value
     */
    public PointsMethod(BigDecimal discountPercent, BigDecimal remainingLimit) {
        this.discountPercent = discountPercent;
        this.remainingLimit = remainingLimit;
    }

    @Override
    public String getId() {
        return POINTS_ID;
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
            throw new IllegalArgumentException("Insufficient points available");
        }

        this.remainingLimit = newLimit;
    }

    @Override
    public boolean isCard() {
        return false;
    }
}
