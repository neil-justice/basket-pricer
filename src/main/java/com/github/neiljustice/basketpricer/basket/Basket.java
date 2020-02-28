package com.github.neiljustice.basketpricer.basket;

import java.util.Collection;

public class Basket {
    private final Collection<Item> items;

    public Basket(Collection<Item> items) {
        this.items = items;
    }

    public Collection<Item> getItems() {
        return items;
    }
}
