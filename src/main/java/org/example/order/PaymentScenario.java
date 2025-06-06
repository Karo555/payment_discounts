package org.example.order;

import org.example.payment.CardMethod;
import org.example.payment.PaymentMethod;
import java.math.BigDecimal;

/**
 * Captures "one way to pay one order," including how many points or which card, and the resulting discount.
 */
public class PaymentScenario {
    private final Order order;
    private final PaymentMethod cardMethod; // null if no card used
    private final PaymentMethod pointsMethod; // null if no points used
    private final BigDecimal pointsUsed; // zero if none
    private final BigDecimal cardCharge; // zero if none
    private final BigDecimal discountValue; // monetary savings

    /**
     * Constructor for PaymentScenario.
     *
     * @param order The order being paid
     * @param cardMethod The card method used (null if no card used)
     * @param pointsMethod The points method used (null if no points used)
     * @param pointsUsed The amount of points used (zero if none)
     * @param cardCharge The amount charged to the card (zero if none)
     * @param discountValue The monetary savings from discounts
     */
    public PaymentScenario(Order order, PaymentMethod cardMethod, PaymentMethod pointsMethod, BigDecimal pointsUsed, 
                          BigDecimal cardCharge, BigDecimal discountValue) {
        this.order = order;
        this.cardMethod = cardMethod;
        this.pointsMethod = pointsMethod;
        this.pointsUsed = pointsUsed != null ? pointsUsed : BigDecimal.ZERO;
        this.cardCharge = cardCharge != null ? cardCharge : BigDecimal.ZERO;
        this.discountValue = discountValue != null ? discountValue : BigDecimal.ZERO;
    }

    /**
     * Constructor for backward compatibility.
     *
     * @param order The order being paid
     * @param cardMethod The card method used (null if no card used)
     * @param pointsUsed The amount of points used (zero if none)
     * @param cardCharge The amount charged to the card (zero if none)
     * @param discountValue The monetary savings from discounts
     */
    public PaymentScenario(Order order, CardMethod cardMethod, BigDecimal pointsUsed, 
                          BigDecimal cardCharge, BigDecimal discountValue) {
        this(order, cardMethod, null, pointsUsed, cardCharge, discountValue);
    }

    /**
     * @return The order being paid
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Checks if this payment scenario uses a card.
     *
     * @return true if a card is used, false otherwise
     */
    public boolean usesCard() {
        return cardMethod != null;
    }

    /**
     * Checks if this payment scenario uses points.
     *
     * @return true if points are used, false otherwise
     */
    public boolean usesPoints() {
        return pointsUsed.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * @return The monetary savings from discounts
     */
    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    /**
     * @return The card method used (null if no card used)
     */
    public PaymentMethod getCardMethod() {
        return cardMethod;
    }

    /**
     * @return The points method used (null if no points used)
     */
    public PaymentMethod getPointsMethod() {
        return pointsMethod;
    }

    /**
     * @return The amount of points used (zero if none)
     */
    public BigDecimal getPointsUsed() {
        return pointsUsed;
    }

    /**
     * @return The amount charged to the card (zero if none)
     */
    public BigDecimal getCardCharge() {
        return cardCharge;
    }
}
