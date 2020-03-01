package com.github.neiljustice.basketpricer.basket;

import com.github.neiljustice.basketpricer.ItemInfo;

import java.math.BigDecimal;

public class BasketBuilder {
    private final ItemInfo itemInfo;

    private final Basket basket = new Basket();

    public BasketBuilder(ItemInfo itemInfo) {
        this.itemInfo = itemInfo;
    }

    public BasketBuilder withItem(String name) {
        return withItem(name, 1);
    }

    // TODO it would be good to validate quantities, e.g. prevent non-positive quantities
    public BasketBuilder withItem(String name, int quantity) {
        final Item item = itemInfo.getItem(name);
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

    // TODO it would be good to validate quantities, e.g. prevent non-positive quantities
    public BasketBuilder withItemByWeightKilos(String name, BigDecimal quantity) {
        final Item item = itemInfo.getItem(name);
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
