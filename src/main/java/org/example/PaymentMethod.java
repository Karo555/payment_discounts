package org.example;

import java.math.BigDecimal;

/**
 * Represents any way to pay—card or points—abstracting common data.
 */
public interface PaymentMethod {
    /**
     * @return The payment method ID
     */
    String getId();
    
    /**
     * @return The discount percentage for this payment method
     */
    BigDecimal getDiscountPercent();
    
    /**
     * @return The remaining limit available for this payment method
     */
    BigDecimal getRemainingLimit();
    
    /**
     * Subtracts the specified amount from the remaining limit.
     * 
     * @param amount The amount to deduct
     */
    void deductAmount(BigDecimal amount);
    
    /**
     * @return true if this is a card payment method, false otherwise
     */
    boolean isCard();
}