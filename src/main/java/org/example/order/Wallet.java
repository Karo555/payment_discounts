package org.example.order;

import org.example.payment.CardMethod;
import org.example.payment.PaymentMethod;
import org.example.payment.PointsMethod;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Aggregates all of a customer's payment methods and exposes global limits.
 */
public class Wallet {
    private final List<PaymentMethod> paymentMethods;
    private final PaymentMethod pointsMethod;

    /**
     * Constructor for Wallet.
     *
     * @param paymentMethods The list of payment methods
     * @param pointsMethod The points payment method
     */
    public Wallet(List<PaymentMethod> paymentMethods, PaymentMethod pointsMethod) {
        this.paymentMethods = paymentMethods != null ? 
            Collections.unmodifiableList(new ArrayList<>(paymentMethods)) : 
            Collections.emptyList();
        this.pointsMethod = pointsMethod;
    }

    /**
     * Static factory method for creating a wallet with card methods.
     *
     * @param cardMethods The list of card payment methods
     * @param pointsMethod The points payment method
     * @return A new Wallet instance
     */
    public static Wallet createWithCards(List<CardMethod> cardMethods, PointsMethod pointsMethod) {
        return new Wallet(cardMethods != null ? 
            new ArrayList<>(cardMethods.stream().map(card -> (PaymentMethod) card).collect(Collectors.toList())) : 
            null, 
            pointsMethod);
    }

    /**
     * @return The list of card payment methods
     */
    public List<CardMethod> getCardMethods() {
        return Collections.unmodifiableList(
            paymentMethods.stream()
            .filter(PaymentMethod::isCard)
            .map(method -> (CardMethod) method)
            .collect(Collectors.toList())
        );
    }

    /**
     * @return The points payment method
     */
    public PointsMethod getPointsMethod() {
        return (PointsMethod) pointsMethod;
    }

    /**
     * @return The list of all payment methods
     */
    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    /**
     * Calculates the total remaining limit across all card methods.
     *
     * @return The total remaining card limit
     */
    public BigDecimal totalRemainingCardLimit() {
        BigDecimal total = BigDecimal.ZERO;
        for (PaymentMethod method : paymentMethods) {
            if (method.isCard()) {
                total = total.add(method.getRemainingLimit());
            }
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
