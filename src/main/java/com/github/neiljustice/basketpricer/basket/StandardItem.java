package com.github.neiljustice.basketpricer.basket;

import com.github.neiljustice.basketpricer.CurrencyConfiguration;

import java.math.BigDecimal;

/**
 * Represents an item in a basket that is priced per item, such as tins of beans.
 */
public class StandardItem implements Item {
    private final String name;

    private final BigDecimal price;

    public StandardItem(String name, BigDecimal price) {
        this.name = name;
        this.price = CurrencyConfiguration.scale(price);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }
}
