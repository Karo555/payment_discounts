package org.example;

import org.example.order.Order;
import org.example.order.PaymentScenario;
import org.example.payment.CardMethod;
import org.example.payment.PointsMethod;
import org.example.report.SummaryReporter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the SummaryReporter class.
 */
class SummaryReporterTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    void printPaymentSummary_shouldAggregateAndFormatCorrectly() {
        // Arrange
        SummaryReporter reporter = new SummaryReporter();
        List<PaymentScenario> scenarios = new ArrayList<>();

        // Create test data
        Order order1 = new Order("order1", new BigDecimal("100.00"), new HashSet<>());
        Order order2 = new Order("order2", new BigDecimal("65.00"), new HashSet<>());
        Order order3 = new Order("order3", new BigDecimal("100.00"), new HashSet<>());

        CardMethod cardMethod = new CardMethod("mZysk", new BigDecimal("0.10"), new BigDecimal("1000.00"));

        // Scenario 1: Card payment only
        PaymentScenario scenario1 = new PaymentScenario(
                order1, 
                cardMethod, 
                BigDecimal.ZERO, 
                new BigDecimal("100.00"), 
                BigDecimal.ZERO
        );

        // Scenario 2: Card payment only
        PaymentScenario scenario2 = new PaymentScenario(
                order2, 
                cardMethod, 
                BigDecimal.ZERO, 
                new BigDecimal("65.00"), 
                BigDecimal.ZERO
        );

        // Scenario 3: Points payment only
        PaymentScenario scenario3 = new PaymentScenario(
                order3, 
                null, 
                new BigDecimal("100.00"), 
                BigDecimal.ZERO, 
                BigDecimal.ZERO
        );

        scenarios.add(scenario1);
        scenarios.add(scenario2);
        scenarios.add(scenario3);

        // Act
        reporter.printPaymentSummary(scenarios);

        // Expected output
        String expectedOutput = "mZysk 165.00\nPOINTS 100.00\n";

        // Debug
        System.setOut(originalOut);
        System.out.println("Expected output: " + expectedOutput);
        System.out.println("Actual output: " + outContent.toString());
        System.setOut(new PrintStream(outContent));

        // Assert
        assertEquals(expectedOutput, outContent.toString());
    }

    @Test
    void printPaymentSummary_shouldHandleEmptyScenarios() {
        // Arrange
        SummaryReporter reporter = new SummaryReporter();
        List<PaymentScenario> scenarios = new ArrayList<>();

        // Act
        reporter.printPaymentSummary(scenarios);

        // Assert
        String expectedOutput = "No payment scenarios to report.\n";
        assertEquals(expectedOutput, outContent.toString());
    }
}
