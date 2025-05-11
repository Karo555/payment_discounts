package org.example.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.payment.CardMethod;
import org.example.payment.PaymentMethod;
import org.example.payment.PointsMethod;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps JSON data from paymentmethods.json to PaymentMethod objects using Jackson.
 */
public class PaymentMethodJsonMapper {

    // Constants
    private static final String POINTS_ID = "PUNKTY";

    /**
     * DTO class for deserializing JSON data.
     * This class maps the JSON fields to Java fields, handling any name differences.
     */
    private static class PaymentMethodDto {
        private String id;

        @JsonProperty("discount")
        private String discountPercent;

        @JsonProperty("limit")
        private String remainingLimit;

        // Default constructor for Jackson
        public PaymentMethodDto() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDiscountPercent() {
            return discountPercent;
        }

        public void setDiscountPercent(String discountPercent) {
            this.discountPercent = discountPercent;
        }

        public String getRemainingLimit() {
            return remainingLimit;
        }

        public void setRemainingLimit(String remainingLimit) {
            this.remainingLimit = remainingLimit;
        }

        /**
         * Converts this DTO to a domain PaymentMethod object.
         *
         * @return A new PaymentMethod object (either CardMethod or PointsMethod)
         */
        public PaymentMethod toDomainObject() {
            BigDecimal discount = new BigDecimal(discountPercent);
            BigDecimal limit = new BigDecimal(remainingLimit);

            if (POINTS_ID.equals(id)) {
                return new PointsMethod(discount, limit);
            } else {
                return new CardMethod(id, discount, limit);
            }
        }
    }

    /**
     * Reads payment methods from a JSON file and converts them to PaymentMethod objects.
     *
     * @param jsonFile The JSON file containing payment method data
     * @return A list of PaymentMethod objects
     * @throws IOException If there's an error reading the file
     */
    public static List<PaymentMethod> readPaymentMethods(File jsonFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        // Read the JSON file into a list of PaymentMethodDto objects
        List<PaymentMethodDto> methodDtos = mapper.readValue(jsonFile, new TypeReference<List<PaymentMethodDto>>() {});

        // Convert the DTOs to domain objects
        return methodDtos.stream()
                .map(PaymentMethodDto::toDomainObject)
                .collect(Collectors.toList());
    }
}
