package org.example;

import org.example.mapper.OrderJsonMapper;
import org.example.mapper.PaymentMethodJsonMapper;
import org.example.order.Order;
import org.example.payment.PaymentMethod;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Demonstrates the use of JSON mappers to read orders.json and paymentmethods.json.
 */
public class JsonMapperDemo {

    public static void main(String[] args) {
        try {
            // Read orders from JSON
            File ordersFile = new File("src/main/resources/data/orders.json");
            List<Order> orders = OrderJsonMapper.readOrders(ordersFile);

            System.out.println("Orders:");
            for (Order order : orders) {
                System.out.println("  ID: " + order.getId());
                System.out.println("  Value: " + order.getValue());
                System.out.println("  Promotions: " + order.getEligiblePromoIds());
                System.out.println();
            }

            // Read payment methods from JSON
            File paymentMethodsFile = new File("src/main/resources/data/paymentmethods.json");
            List<PaymentMethod> paymentMethods = PaymentMethodJsonMapper.readPaymentMethods(paymentMethodsFile);

            System.out.println("Payment Methods:");
            for (PaymentMethod method : paymentMethods) {
                System.out.println("  ID: " + method.getId());
                System.out.println("  Discount: " + method.getDiscountPercent());
                System.out.println("  Limit: " + method.getRemainingLimit());
                System.out.println("  Type: " + (method.isCard() ? "Card" : "Points"));
                System.out.println();
            }
        } catch (IOException e) {
            System.err.println("Error reading JSON files: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
