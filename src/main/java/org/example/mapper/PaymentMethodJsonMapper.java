package org.example.mapper;

import org.example.payment.CardMethod;
import org.example.payment.PaymentMethod;
import org.example.payment.PointsMethod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Maps JSON data from paymentmethods.json to PaymentMethod objects.
 */
public class PaymentMethodJsonMapper {

    // Patterns for parsing JSON
    private static final Pattern ID_PATTERN = Pattern.compile("\"id\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern DISCOUNT_PATTERN = Pattern.compile("\"discount\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern LIMIT_PATTERN = Pattern.compile("\"limit\"\\s*:\\s*\"([^\"]+)\"");
    
    // Constants
    private static final String POINTS_ID = "PUNKTY";

    /**
     * Reads payment methods from a JSON file and converts them to PaymentMethod objects.
     *
     * @param jsonFile The JSON file containing payment method data
     * @return A list of PaymentMethod objects
     * @throws IOException If there's an error reading the file
     */
    public static List<PaymentMethod> readPaymentMethods(File jsonFile) throws IOException {
        List<PaymentMethod> paymentMethods = new ArrayList<>();
        
        // Read the entire file content
        StringBuilder jsonContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(jsonFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
        }
        
        // Split the content into individual payment method objects
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
        String[] methodJsons = content.split("\\},\\s*\\{");
        
        for (int i = 0; i < methodJsons.length; i++) {
            String methodJson = methodJsons[i];
            
            // Add back the braces that were removed during splitting
            if (!methodJson.startsWith("{")) {
                methodJson = "{" + methodJson;
            }
            if (!methodJson.endsWith("}")) {
                methodJson = methodJson + "}";
            }
            
            // Extract data using regex
            String id = extractValue(ID_PATTERN, methodJson);
            String discountStr = extractValue(DISCOUNT_PATTERN, methodJson);
            String limitStr = extractValue(LIMIT_PATTERN, methodJson);
            
            BigDecimal discount = new BigDecimal(discountStr);
            BigDecimal limit = new BigDecimal(limitStr);
            
            // Create the appropriate payment method based on ID
            PaymentMethod paymentMethod;
            if (POINTS_ID.equals(id)) {
                paymentMethod = new PointsMethod(discount, limit);
            } else {
                paymentMethod = new CardMethod(id, discount, limit);
            }
            
            paymentMethods.add(paymentMethod);
        }
        
        return paymentMethods;
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