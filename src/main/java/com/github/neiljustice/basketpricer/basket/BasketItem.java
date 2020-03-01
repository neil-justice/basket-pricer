package com.github.neiljustice.basketpricer.basket;

import com.github.neiljustice.basketpricer.CurrencyConfiguration;

import java.math.BigDecimal;

public class BasketItem extends Item {

    private final BigDecimal quantity;

    public BasketItem(String name, BigDecimal pricePer, PricingUnit pricingUnit, BigDecimal quantity) {
        super(name, pricePer, pricingUnit);
        this.quantity = quantity;
    }

    public BasketItem(Item item, BigDecimal quantity) {
        this(item.getName(), item.getPricePer(), item.getPricingUnit(), quantity);
    }

    public BigDecimal getPrice() {
        return CurrencyConfiguration.scale(quantity.multiply(getPricePer()));
    }

    public BigDecimal getQuantity() {
        return quantity;
    }
}
