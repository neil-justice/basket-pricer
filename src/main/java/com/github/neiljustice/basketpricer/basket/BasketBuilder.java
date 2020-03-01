package com.github.neiljustice.basketpricer.basket;

import com.github.neiljustice.basketpricer.PricingInfo;

import java.math.BigDecimal;

public class BasketBuilder {
    private final PricingInfo pricingInfo;

    private final Basket basket = new Basket();

    public BasketBuilder(PricingInfo pricingInfo) {
        this.pricingInfo = pricingInfo;
    }

    public BasketBuilder withItem(String name) {
        return withItem(name, 1);
    }

    public BasketBuilder withItem(String name, int quantity) {
        final Item item = pricingInfo.getItem(name);
        if (item == null) {
            throw new ItemException("Tried to add un-stocked item: " + name);
        }
        if (item.getPricingUnit() != PricingUnit.PER_ITEM) {
            throw new ItemException("Tried to add unit quantity of weight-priced item: " + name);
        }
        final BasketItem existing = basket.getItem(name);
        if (existing != null) {
            quantity += existing.getQuantity().intValueExact();
        }
        basket.addItem(new BasketItem(item, new BigDecimal(quantity)));
        return this;
    }

    public BasketBuilder withItemByWeight(String name, BigDecimal quantity) {
        final Item item = pricingInfo.getItem(name);
        if (item == null) {
            throw new ItemException("Tried to add un-stocked item: " + name);
        }
        if (item.getPricingUnit() != PricingUnit.PER_KILOGRAM_WEIGHT) {
            throw new ItemException("Tried to add decimal quantity of unit-priced item: " + name);
        }
        final BasketItem existing = basket.getItem(name);
        if (existing != null) {
            quantity = existing.getQuantity().add(quantity);
        }

        basket.addItem(new BasketItem(item, quantity));
        return this;
    }

    public Basket build() {
        return basket;
    }
}
