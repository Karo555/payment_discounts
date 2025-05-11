package org.example;

import org.example.payment.PointsMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the PointsMethod class.
 */
class PointsMethodTest {

    @Test
    @DisplayName("Constructor should initialize fields correctly")
    void constructorShouldInitializeFieldsCorrectly() {
        // Arrange
        BigDecimal discountPercent = new BigDecimal("0.15");
        BigDecimal remainingLimit = new BigDecimal("500.00");

        // Act
        PointsMethod pointsMethod = new PointsMethod(discountPercent, remainingLimit);

        // Assert
        assertEquals("PUNKTY", pointsMethod.getId());
        assertEquals(discountPercent, pointsMethod.getDiscountPercent());
        assertEquals(remainingLimit, pointsMethod.getRemainingLimit());
        assertFalse(pointsMethod.isCard());
    }

    @Test
    @DisplayName("deductAmount should reduce remaining limit correctly")
    void deductAmountShouldReduceRemainingLimitCorrectly() {
        // Arrange
        BigDecimal discountPercent = new BigDecimal("0.15");
        BigDecimal remainingLimit = new BigDecimal("500.00");
        PointsMethod pointsMethod = new PointsMethod(discountPercent, remainingLimit);
        BigDecimal amountToDeduct = new BigDecimal("100.00");
        BigDecimal expectedRemainingLimit = new BigDecimal("400.00");

        // Act
        pointsMethod.deductAmount(amountToDeduct);

        // Assert
        assertEquals(expectedRemainingLimit, pointsMethod.getRemainingLimit());
    }

    @Test
    @DisplayName("deductAmount should throw exception for null amount")
    void deductAmountShouldThrowExceptionForNullAmount() {
        // Arrange
        BigDecimal discountPercent = new BigDecimal("0.15");
        BigDecimal remainingLimit = new BigDecimal("500.00");
        PointsMethod pointsMethod = new PointsMethod(discountPercent, remainingLimit);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            pointsMethod.deductAmount(null);
        });
    }

    @Test
    @DisplayName("deductAmount should throw exception for negative amount")
    void deductAmountShouldThrowExceptionForNegativeAmount() {
        // Arrange
        BigDecimal discountPercent = new BigDecimal("0.15");
        BigDecimal remainingLimit = new BigDecimal("500.00");
        PointsMethod pointsMethod = new PointsMethod(discountPercent, remainingLimit);
        BigDecimal negativeAmount = new BigDecimal("-50.00");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            pointsMethod.deductAmount(negativeAmount);
        });
    }

    @Test
    @DisplayName("deductAmount should throw exception for insufficient points")
    void deductAmountShouldThrowExceptionForInsufficientPoints() {
        // Arrange
        BigDecimal discountPercent = new BigDecimal("0.15");
        BigDecimal remainingLimit = new BigDecimal("500.00");
        PointsMethod pointsMethod = new PointsMethod(discountPercent, remainingLimit);
        BigDecimal excessiveAmount = new BigDecimal("600.00");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            pointsMethod.deductAmount(excessiveAmount);
        });
    }

    @Test
    @DisplayName("isCard should return false")
    void isCardShouldReturnFalse() {
        // Arrange
        BigDecimal discountPercent = new BigDecimal("0.15");
        BigDecimal remainingLimit = new BigDecimal("500.00");
        PointsMethod pointsMethod = new PointsMethod(discountPercent, remainingLimit);

        // Act & Assert
        assertFalse(pointsMethod.isCard());
    }
}
