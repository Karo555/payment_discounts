package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the CardMethod class.
 */
class CardMethodTest {

    @Test
    @DisplayName("Constructor should initialize fields correctly")
    void constructorShouldInitializeFieldsCorrectly() {
        // Arrange
        String id = "card123";
        BigDecimal discountPercent = new BigDecimal("0.10");
        BigDecimal remainingLimit = new BigDecimal("1000.00");

        // Act
        CardMethod cardMethod = new CardMethod(id, discountPercent, remainingLimit);

        // Assert
        assertEquals(id, cardMethod.getId());
        assertEquals(discountPercent, cardMethod.getDiscountPercent());
        assertEquals(remainingLimit, cardMethod.getRemainingLimit());
        assertTrue(cardMethod.isCard());
    }

    @Test
    @DisplayName("deductAmount should reduce remaining limit correctly")
    void deductAmountShouldReduceRemainingLimitCorrectly() {
        // Arrange
        String id = "card123";
        BigDecimal discountPercent = new BigDecimal("0.10");
        BigDecimal remainingLimit = new BigDecimal("1000.00");
        CardMethod cardMethod = new CardMethod(id, discountPercent, remainingLimit);
        BigDecimal amountToDeduct = new BigDecimal("200.00");
        BigDecimal expectedRemainingLimit = new BigDecimal("800.00");

        // Act
        cardMethod.deductAmount(amountToDeduct);

        // Assert
        assertEquals(expectedRemainingLimit, cardMethod.getRemainingLimit());
    }

    @Test
    @DisplayName("deductAmount should throw exception for null amount")
    void deductAmountShouldThrowExceptionForNullAmount() {
        // Arrange
        String id = "card123";
        BigDecimal discountPercent = new BigDecimal("0.10");
        BigDecimal remainingLimit = new BigDecimal("1000.00");
        CardMethod cardMethod = new CardMethod(id, discountPercent, remainingLimit);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            cardMethod.deductAmount(null);
        });
    }

    @Test
    @DisplayName("deductAmount should throw exception for negative amount")
    void deductAmountShouldThrowExceptionForNegativeAmount() {
        // Arrange
        String id = "card123";
        BigDecimal discountPercent = new BigDecimal("0.10");
        BigDecimal remainingLimit = new BigDecimal("1000.00");
        CardMethod cardMethod = new CardMethod(id, discountPercent, remainingLimit);
        BigDecimal negativeAmount = new BigDecimal("-100.00");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            cardMethod.deductAmount(negativeAmount);
        });
    }

    @Test
    @DisplayName("deductAmount should throw exception for insufficient limit")
    void deductAmountShouldThrowExceptionForInsufficientLimit() {
        // Arrange
        String id = "card123";
        BigDecimal discountPercent = new BigDecimal("0.10");
        BigDecimal remainingLimit = new BigDecimal("1000.00");
        CardMethod cardMethod = new CardMethod(id, discountPercent, remainingLimit);
        BigDecimal excessiveAmount = new BigDecimal("1100.00");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            cardMethod.deductAmount(excessiveAmount);
        });
    }

    @Test
    @DisplayName("isCard should return true")
    void isCardShouldReturnTrue() {
        // Arrange
        String id = "card123";
        BigDecimal discountPercent = new BigDecimal("0.10");
        BigDecimal remainingLimit = new BigDecimal("1000.00");
        CardMethod cardMethod = new CardMethod(id, discountPercent, remainingLimit);

        // Act & Assert
        assertTrue(cardMethod.isCard());
    }
}