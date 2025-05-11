package org.example;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Holds data about a customer's order and which promotions it could use.
 */
public class Order {
    private final String id;
    private final BigDecimal value;
    private final Set<String> eligiblePromoIds;

    /**
     * Constructor for Order.
     *
     * @param id The order ID
     * @param value The pre-discount total value
     * @param eligiblePromoIds The set of eligible promotion IDs (may be null or empty)
     */
    public Order(String id, BigDecimal value, Set<String> eligiblePromoIds) {
        this.id = id;
        this.value = value;
        // If eligiblePromoIds is null, treat it as an empty set
        this.eligiblePromoIds = eligiblePromoIds != null ? 
            Collections.unmodifiableSet(new HashSet<>(eligiblePromoIds)) : 
            Collections.emptySet();
    }

    /**
     * @return The order ID
     */
    public String getId() {
        return id;
    }

    /**
     * @return The pre-discount total value
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * @return The set of eligible promotion IDs (may be empty)
     */
    public Set<String> getEligiblePromoIds() {
        return eligiblePromoIds;
    }

    /**
     * Convenience method for rule checks.
     *
     * @param methodId The payment method ID to check
     * @return true if the order has a card promotion for the given method ID
     */
    public boolean hasCardPromotion(String methodId) {
        return eligiblePromoIds.contains(methodId);
    }
}