package com.github.neiljustice.basketpricer;

import com.github.neiljustice.basketpricer.basket.Item;
import com.github.neiljustice.basketpricer.basket.PricingUnit;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents the source of truth for the prices of items and the prices per kilo of
 * items sold by weight.  Would probably be replaced by a database in a real setup.
 */
public class PricingInfo {
    private final Map<String, Item> items = new HashMap<>();

    public void registerItem(String name, BigDecimal pricePer, PricingUnit pricingUnit) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(pricePer);
        Objects.requireNonNull(pricingUnit);

        items.put(name, new Item(name, pricePer, pricingUnit));
    }

    public Item getItem(String name) {
        return items.get(name);
    }

}
