package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Order class.
 */
class OrderTest {

    @Test
    @DisplayName("Constructor should initialize fields correctly")
    void constructorShouldInitializeFieldsCorrectly() {
        // Arrange
        String id = "order123";
        BigDecimal value = new BigDecimal("100.00");
        Set<String> eligiblePromoIds = new HashSet<>();
        eligiblePromoIds.add("promo1");
        eligiblePromoIds.add("promo2");

        // Act
        Order order = new Order(id, value, eligiblePromoIds);

        // Assert
        assertEquals(id, order.getId());
        assertEquals(value, order.getValue());
        assertEquals(eligiblePromoIds, order.getEligiblePromoIds());
    }

    @Test
    @DisplayName("Constructor should handle null eligiblePromoIds")
    void constructorShouldHandleNullEligiblePromoIds() {
        // Arrange
        String id = "order123";
        BigDecimal value = new BigDecimal("100.00");

        // Act
        Order order = new Order(id, value, null);

        // Assert
        assertEquals(id, order.getId());
        assertEquals(value, order.getValue());
        assertNotNull(order.getEligiblePromoIds());
        assertTrue(order.getEligiblePromoIds().isEmpty());
    }

    @Test
    @DisplayName("getEligiblePromoIds should return an unmodifiable set")
    void getEligiblePromoIdsShouldReturnUnmodifiableSet() {
        // Arrange
        String id = "order123";
        BigDecimal value = new BigDecimal("100.00");
        Set<String> eligiblePromoIds = new HashSet<>();
        eligiblePromoIds.add("promo1");
        Order order = new Order(id, value, eligiblePromoIds);

        // Act & Assert
        assertThrows(UnsupportedOperationException.class, () -> {
            order.getEligiblePromoIds().add("promo3");
        });
    }

    @Test
    @DisplayName("hasCardPromotion should return true when promo exists")
    void hasCardPromotionShouldReturnTrueWhenPromoExists() {
        // Arrange
        String id = "order123";
        BigDecimal value = new BigDecimal("100.00");
        Set<String> eligiblePromoIds = new HashSet<>();
        String promoId = "promo1";
        eligiblePromoIds.add(promoId);
        Order order = new Order(id, value, eligiblePromoIds);

        // Act & Assert
        assertTrue(order.hasCardPromotion(promoId));
    }

    @Test
    @DisplayName("hasCardPromotion should return false when promo doesn't exist")
    void hasCardPromotionShouldReturnFalseWhenPromoDoesNotExist() {
        // Arrange
        String id = "order123";
        BigDecimal value = new BigDecimal("100.00");
        Set<String> eligiblePromoIds = new HashSet<>();
        eligiblePromoIds.add("promo1");
        Order order = new Order(id, value, eligiblePromoIds);

        // Act & Assert
        assertFalse(order.hasCardPromotion("nonExistentPromo"));
    }
}