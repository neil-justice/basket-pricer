package com.github.neiljustice.basketpricer;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents the source of truth for the prices of items and the prices per kilo of
 * items sold by weight.  Would probably be replaced by a database in a real setup.
 */
public class PricingInfo {
    private final Map<String, BigDecimal> items = new HashMap<>();

    private final Map<String, BigDecimal> weightedItems = new HashMap<>();

    public void registerItem(String name, BigDecimal price) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(price);

        items.put(name, price);
    }

    public void registerWeightedItem(String name, BigDecimal pricePerKilo) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(pricePerKilo);

        weightedItems.put(name, pricePerKilo);
    }

    public Optional<BigDecimal> getPrice(String name) {
        return Optional.ofNullable(items.get(name));
    }

    public Optional<BigDecimal> getPricePerKilo(String name) {
        return Optional.ofNullable(weightedItems.get(name));
    }

}
