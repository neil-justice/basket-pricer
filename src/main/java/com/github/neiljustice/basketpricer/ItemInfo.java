package com.github.neiljustice.basketpricer;

import com.github.neiljustice.basketpricer.basket.Item;
import com.github.neiljustice.basketpricer.basket.PricingUnit;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents the source of truth for the properties of items (price per unit, unit of pricing, name).
 * Would probably be replaced by a database in a real setup.
 */
public class ItemInfo {
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
