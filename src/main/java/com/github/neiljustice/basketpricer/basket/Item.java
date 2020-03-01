package com.github.neiljustice.basketpricer.basket;

import java.math.BigDecimal;

public class Item {

    private final String name;

    private final BigDecimal pricePer;

    private final PricingUnit pricingUnit;

    public Item(String name, BigDecimal pricePer, PricingUnit pricingUnit) {
        this.name = name;
        this.pricePer = pricePer;
        this.pricingUnit = pricingUnit;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPricePer() {
        return pricePer;
    }

    public PricingUnit getPricingUnit() {
        return pricingUnit;
    }
}
