package org.example;

import org.example.order.Wallet;
import org.example.payment.CardMethod;
import org.example.payment.PaymentMethod;
import org.example.payment.PointsMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Wallet class.
 */
class WalletTest {

    @Test
    @DisplayName("Constructor should initialize fields correctly")
    void constructorShouldInitializeFieldsCorrectly() {
        // Arrange
        List<CardMethod> cardMethods = new ArrayList<>();
        cardMethods.add(new CardMethod("card1", new BigDecimal("0.10"), new BigDecimal("1000.00")));
        cardMethods.add(new CardMethod("card2", new BigDecimal("0.05"), new BigDecimal("500.00")));
        PointsMethod pointsMethod = new PointsMethod(new BigDecimal("0.15"), new BigDecimal("300.00"));

        // Act
        Wallet wallet = Wallet.createWithCards(cardMethods, pointsMethod);

        // Assert
        assertEquals(cardMethods, wallet.getCardMethods());
        assertEquals(pointsMethod, wallet.getPointsMethod());
    }

    @Test
    @DisplayName("Constructor should handle null cardMethods")
    void constructorShouldHandleNullCardMethods() {
        // Arrange
        PointsMethod pointsMethod = new PointsMethod(new BigDecimal("0.15"), new BigDecimal("300.00"));
        List<CardMethod> nullCardMethods = null;

        // Act
        Wallet wallet = Wallet.createWithCards(nullCardMethods, pointsMethod);

        // Assert
        assertNotNull(wallet.getCardMethods());
        assertTrue(wallet.getCardMethods().isEmpty());
        assertEquals(pointsMethod, wallet.getPointsMethod());
    }

    @Test
    @DisplayName("getCardMethods should return an unmodifiable list")
    void getCardMethodsShouldReturnUnmodifiableList() {
        // Arrange
        List<CardMethod> cardMethods = new ArrayList<>();
        cardMethods.add(new CardMethod("card1", new BigDecimal("0.10"), new BigDecimal("1000.00")));
        PointsMethod pointsMethod = new PointsMethod(new BigDecimal("0.15"), new BigDecimal("300.00"));
        Wallet wallet = Wallet.createWithCards(cardMethods, pointsMethod);

        // Act & Assert
        assertThrows(UnsupportedOperationException.class, () -> {
            wallet.getCardMethods().add(new CardMethod("card3", new BigDecimal("0.07"), new BigDecimal("700.00")));
        });
    }

    @Test
    @DisplayName("totalRemainingCardLimit should calculate sum correctly")
    void totalRemainingCardLimitShouldCalculateSumCorrectly() {
        // Arrange
        List<CardMethod> cardMethods = new ArrayList<>();
        cardMethods.add(new CardMethod("card1", new BigDecimal("0.10"), new BigDecimal("1000.00")));
        cardMethods.add(new CardMethod("card2", new BigDecimal("0.05"), new BigDecimal("500.00")));
        PointsMethod pointsMethod = new PointsMethod(new BigDecimal("0.15"), new BigDecimal("300.00"));
        Wallet wallet = Wallet.createWithCards(cardMethods, pointsMethod);
        BigDecimal expectedTotal = new BigDecimal("1500.00");

        // Act
        BigDecimal actualTotal = wallet.totalRemainingCardLimit();

        // Assert
        assertEquals(expectedTotal, actualTotal);
    }

    @Test
    @DisplayName("totalRemainingCardLimit should return zero for empty card methods")
    void totalRemainingCardLimitShouldReturnZeroForEmptyCardMethods() {
        // Arrange
        PointsMethod pointsMethod = new PointsMethod(new BigDecimal("0.15"), new BigDecimal("300.00"));
        List<CardMethod> emptyCardMethods = Collections.emptyList();
        Wallet wallet = Wallet.createWithCards(emptyCardMethods, pointsMethod);

        // Act
        BigDecimal actualTotal = wallet.totalRemainingCardLimit();

        // Assert
        assertEquals(BigDecimal.ZERO, actualTotal);
    }

    @Test
    @DisplayName("totalRemainingPoints should return points method limit")
    void totalRemainingPointsShouldReturnPointsMethodLimit() {
        // Arrange
        List<CardMethod> cardMethods = new ArrayList<>();
        cardMethods.add(new CardMethod("card1", new BigDecimal("0.10"), new BigDecimal("1000.00")));
        BigDecimal pointsLimit = new BigDecimal("300.00");
        PointsMethod pointsMethod = new PointsMethod(new BigDecimal("0.15"), pointsLimit);
        Wallet wallet = Wallet.createWithCards(cardMethods, pointsMethod);

        // Act
        BigDecimal actualPoints = wallet.totalRemainingPoints();

        // Assert
        assertEquals(pointsLimit, actualPoints);
    }

    @Test
    @DisplayName("totalRemainingPoints should return zero for null points method")
    void totalRemainingPointsShouldReturnZeroForNullPointsMethod() {
        // Arrange
        List<CardMethod> cardMethods = new ArrayList<>();
        cardMethods.add(new CardMethod("card1", new BigDecimal("0.10"), new BigDecimal("1000.00")));
        Wallet wallet = Wallet.createWithCards(cardMethods, null);

        // Act
        BigDecimal actualPoints = wallet.totalRemainingPoints();

        // Assert
        assertEquals(BigDecimal.ZERO, actualPoints);
    }
}
