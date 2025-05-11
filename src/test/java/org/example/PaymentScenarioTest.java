package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the PaymentScenario class.
 */
class PaymentScenarioTest {

    @Test
    @DisplayName("Constructor should initialize fields correctly")
    void constructorShouldInitializeFieldsCorrectly() {
        // Arrange
        Order order = new Order("order123", new BigDecimal("100.00"), new HashSet<>());
        CardMethod cardMethod = new CardMethod("card1", new BigDecimal("0.10"), new BigDecimal("1000.00"));
        BigDecimal pointsUsed = new BigDecimal("50.00");
        BigDecimal cardCharge = new BigDecimal("50.00");
        BigDecimal discountValue = new BigDecimal("10.00");

        // Act
        PaymentScenario scenario = new PaymentScenario(order, cardMethod, pointsUsed, cardCharge, discountValue);

        // Assert
        assertEquals(order, scenario.getOrder());
        assertTrue(scenario.usesCard());
        assertTrue(scenario.usesPoints());
        assertEquals(discountValue, scenario.getDiscountValue());
    }

    @Test
    @DisplayName("Constructor should handle null pointsUsed, cardCharge, and discountValue")
    void constructorShouldHandleNullValues() {
        // Arrange
        Order order = new Order("order123", new BigDecimal("100.00"), new HashSet<>());
        CardMethod cardMethod = new CardMethod("card1", new BigDecimal("0.10"), new BigDecimal("1000.00"));

        // Act
        PaymentScenario scenario = new PaymentScenario(order, cardMethod, null, null, null);

        // Assert
        assertEquals(order, scenario.getOrder());
        assertTrue(scenario.usesCard());
        assertFalse(scenario.usesPoints());
        assertEquals(BigDecimal.ZERO, scenario.getDiscountValue());
    }

    @Test
    @DisplayName("usesCard should return true when cardMethod is not null")
    void usesCardShouldReturnTrueWhenCardMethodIsNotNull() {
        // Arrange
        Order order = new Order("order123", new BigDecimal("100.00"), new HashSet<>());
        CardMethod cardMethod = new CardMethod("card1", new BigDecimal("0.10"), new BigDecimal("1000.00"));
        BigDecimal pointsUsed = BigDecimal.ZERO;
        BigDecimal cardCharge = new BigDecimal("100.00");
        BigDecimal discountValue = new BigDecimal("10.00");

        // Act
        PaymentScenario scenario = new PaymentScenario(order, cardMethod, pointsUsed, cardCharge, discountValue);

        // Assert
        assertTrue(scenario.usesCard());
    }

    @Test
    @DisplayName("usesCard should return false when cardMethod is null")
    void usesCardShouldReturnFalseWhenCardMethodIsNull() {
        // Arrange
        Order order = new Order("order123", new BigDecimal("100.00"), new HashSet<>());
        BigDecimal pointsUsed = new BigDecimal("100.00");
        BigDecimal cardCharge = BigDecimal.ZERO;
        BigDecimal discountValue = new BigDecimal("15.00");

        // Act
        PaymentScenario scenario = new PaymentScenario(order, null, pointsUsed, cardCharge, discountValue);

        // Assert
        assertFalse(scenario.usesCard());
    }

    @Test
    @DisplayName("usesPoints should return true when pointsUsed is greater than zero")
    void usesPointsShouldReturnTrueWhenPointsUsedIsGreaterThanZero() {
        // Arrange
        Order order = new Order("order123", new BigDecimal("100.00"), new HashSet<>());
        BigDecimal pointsUsed = new BigDecimal("50.00");
        BigDecimal cardCharge = BigDecimal.ZERO;
        BigDecimal discountValue = new BigDecimal("7.50");

        // Act
        PaymentScenario scenario = new PaymentScenario(order, null, pointsUsed, cardCharge, discountValue);

        // Assert
        assertTrue(scenario.usesPoints());
    }

    @Test
    @DisplayName("usesPoints should return false when pointsUsed is zero")
    void usesPointsShouldReturnFalseWhenPointsUsedIsZero() {
        // Arrange
        Order order = new Order("order123", new BigDecimal("100.00"), new HashSet<>());
        CardMethod cardMethod = new CardMethod("card1", new BigDecimal("0.10"), new BigDecimal("1000.00"));
        BigDecimal pointsUsed = BigDecimal.ZERO;
        BigDecimal cardCharge = new BigDecimal("100.00");
        BigDecimal discountValue = new BigDecimal("10.00");

        // Act
        PaymentScenario scenario = new PaymentScenario(order, cardMethod, pointsUsed, cardCharge, discountValue);

        // Assert
        assertFalse(scenario.usesPoints());
    }
}