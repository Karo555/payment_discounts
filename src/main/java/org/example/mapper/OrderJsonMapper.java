package org.example.mapper;

import org.example.order.Order;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Maps JSON data from orders.json to Order objects.
 */
public class OrderJsonMapper {

    // Patterns for parsing JSON
    private static final Pattern ID_PATTERN = Pattern.compile("\"id\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern VALUE_PATTERN = Pattern.compile("\"value\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern PROMOTIONS_PATTERN = Pattern.compile("\"promotions\"\\s*:\\s*\\[([^\\]]+)\\]");
    private static final Pattern PROMOTION_ITEM_PATTERN = Pattern.compile("\"([^\"]+)\"");

    /**
     * Reads orders from a JSON file and converts them to Order objects.
     *
     * @param jsonFile The JSON file containing order data
     * @return A list of Order objects
     * @throws IOException If there's an error reading the file
     */
    public static List<Order> readOrders(File jsonFile) throws IOException {
        List<Order> orders = new ArrayList<>();

        // Read the entire file content
        StringBuilder jsonContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(jsonFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
        }

        // Split the content into individual order objects
        String content = jsonContent.toString();
        // Remove the outer brackets and whitespace
        content = content.trim();
        if (content.startsWith("[")) {
            content = content.substring(1);
        }
        if (content.endsWith("]")) {
            content = content.substring(0, content.length() - 1);
        }

        // Split by closing and opening braces of objects
        String[] orderJsons = content.split("\\},\\s*\\{");

        for (int i = 0; i < orderJsons.length; i++) {
            String orderJson = orderJsons[i];

            // Add back the braces that were removed during splitting
            if (!orderJson.startsWith("{")) {
                orderJson = "{" + orderJson;
            }
            if (!orderJson.endsWith("}")) {
                orderJson = orderJson + "}";
            }

            // Extract data using regex
            String id = extractValue(ID_PATTERN, orderJson);
            String valueStr = extractValue(VALUE_PATTERN, orderJson);
            BigDecimal value = new BigDecimal(valueStr);

            // Extract promotions if present
            Set<String> promotions = null;
            String promotionsStr = extractValue(PROMOTIONS_PATTERN, orderJson);
            if (promotionsStr != null) {
                promotions = new HashSet<>();
                Matcher matcher = PROMOTION_ITEM_PATTERN.matcher(promotionsStr);
                while (matcher.find()) {
                    promotions.add(matcher.group(1));
                }
            }

            // Create and add the order
            orders.add(new Order(id, value, promotions));
        }

        return orders;
    }

    /**
     * Extracts a value from a JSON string using a regex pattern.
     *
     * @param pattern The regex pattern to use
     * @param json The JSON string to search
     * @return The extracted value, or null if not found
     */
    private static String extractValue(Pattern pattern, String json) {
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
