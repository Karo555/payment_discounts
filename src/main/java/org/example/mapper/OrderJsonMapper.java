package org.example.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.order.Order;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Maps JSON data from orders.json to Order objects using Jackson.
 */
public class OrderJsonMapper {

    /**
     * DTO class for deserializing JSON data.
     * This class maps the JSON fields to Java fields, handling any name differences.
     */
    private static class OrderDto {
        private String id;
        private String value;

        @JsonProperty("promotions")
        private Set<String> eligiblePromoIds;

        // Default constructor for Jackson
        public OrderDto() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Set<String> getEligiblePromoIds() {
            return eligiblePromoIds;
        }

        public void setEligiblePromoIds(Set<String> eligiblePromoIds) {
            this.eligiblePromoIds = eligiblePromoIds;
        }

        /**
         * Converts this DTO to a domain Order object.
         *
         * @return A new Order object
         */
        public Order toDomainObject() {
            return new Order(id, new BigDecimal(value), eligiblePromoIds);
        }
    }

    /**
     * Reads orders from a JSON file and converts them to Order objects.
     *
     * @param jsonFile The JSON file containing order data
     * @return A list of Order objects
     * @throws IOException If there's an error reading the file
     */
    public static List<Order> readOrders(File jsonFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        // Read the JSON file into a list of OrderDto objects
        List<OrderDto> orderDtos = mapper.readValue(jsonFile, new TypeReference<List<OrderDto>>() {});

        // Convert the DTOs to domain objects
        return orderDtos.stream()
                .map(OrderDto::toDomainObject)
                .toList();
    }
}
