package com.github.neiljustice.basketpricer.basket;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Basket {
    private final Map<String, BasketItem> items = new HashMap<>();

    /**
     * Use {@link BasketBuilder} to construct instances.
     */
    protected Basket() {
    }

    /**
     * Use {@link BasketBuilder} to add items.
     */
    protected void addItem(BasketItem item) {
        items.put(item.getName(), item);
    }

    public BasketItem getItem(String name) {
        return items.get(name);
    }

    public Collection<BasketItem> getItems() {
        return Collections.unmodifiableCollection(items.values());
    }

}
