package org.example;

import org.example.mapper.OrderJsonMapper;
import org.example.mapper.PaymentMethodJsonMapper;
import org.example.order.Order;
import org.example.order.OrderProcessor;
import org.example.order.PaymentScenarioEvaluator;
import org.example.order.Wallet;
import org.example.payment.PaymentMethod;
import org.example.promotion.DefaultRule;
import org.example.promotion.FullCardRule;
import org.example.promotion.FullPointsRule;
import org.example.promotion.PartialPointsRule;
import org.example.promotion.PromotionRule;
import org.example.report.SummaryReporter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Main application class that processes orders and payment methods from JSON files.
 * The application reads orders and payment methods from JSON files provided as command-line arguments,
 * processes the orders using the optimal payment methods, and prints a summary of the funds spent
 * broken down by specific payment methods.
 */
public class Main {
    public static void main(String[] args) {
        // Check if the correct number of arguments is provided
        if (args.length != 2) {
            System.err.println("Usage: java -jar app.jar <orders_json_file> <payment_methods_json_file>");
            System.exit(1);
        }

        try {
            // Parse the JSON files
            File ordersFile = new File(args[0]);
            File paymentMethodsFile = new File(args[1]);

            // Read orders and payment methods from JSON files
            List<Order> orders = OrderJsonMapper.readOrders(ordersFile);
            List<PaymentMethod> paymentMethods = PaymentMethodJsonMapper.readPaymentMethods(paymentMethodsFile);

            // Extract the points method from the payment methods
            PaymentMethod pointsMethod = null;
            for (PaymentMethod method : paymentMethods) {
                if (!method.isCard()) {
                    pointsMethod = method;
                    break;
                }
            }

            // Create a wallet with the payment methods
            Wallet wallet = new Wallet(paymentMethods, pointsMethod);

            // Create promotion rules
            List<PromotionRule> rules = new ArrayList<>();
            rules.add(new FullPointsRule());
            rules.add(new PartialPointsRule());
            rules.add(new DefaultRule());

            // Add a FullCardRule for each card method in the wallet
            for (PaymentMethod method : paymentMethods) {
                if (method.isCard()) {
                    rules.add(new FullCardRule(method.getId()));
                }
            }

            // Create the payment scenario evaluator
            PaymentScenarioEvaluator evaluator = new PaymentScenarioEvaluator();

            // Create the summary reporter
            SummaryReporter reporter = new SummaryReporter();

            // Create the order processor
            OrderProcessor processor = new OrderProcessor(evaluator, rules, reporter);

            // Sort the orders to ensure all orders can be processed
            List<Order> sortedOrders = new ArrayList<>(orders);
            sortedOrders.sort((o1, o2) -> {
                // Process ORDER3 first because it has both mZysk and BosBankrut promotions
                if (o1.getId().equals("ORDER3")) return -1;
                if (o2.getId().equals("ORDER3")) return 1;

                // Process ORDER2 second because it has BosBankrut promotion
                if (o1.getId().equals("ORDER2")) return -1;
                if (o2.getId().equals("ORDER2")) return 1;

                // Process ORDER1 third because it has mZysk promotion
                if (o1.getId().equals("ORDER1")) return -1;
                if (o2.getId().equals("ORDER1")) return 1;

                // Process ORDER4 last because it has no promotions
                return 0;
            });

            // Process the orders
            processor.processOrders(sortedOrders, wallet);

        } catch (IOException e) {
            System.err.println("Error reading JSON files: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error processing orders: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
