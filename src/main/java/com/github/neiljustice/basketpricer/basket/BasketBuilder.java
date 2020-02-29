package com.github.neiljustice.basketpricer.basket;

import com.github.neiljustice.basketpricer.PricingInfo;

import java.math.BigDecimal;
import java.util.ArrayList;

public class BasketBuilder {
    private final PricingInfo pricingInfo;

    private final Basket basket = new Basket(new ArrayList<>());

    public BasketBuilder(PricingInfo pricingInfo) {
        this.pricingInfo = pricingInfo;
    }

    public BasketBuilder withItem(String name) {
        pricingInfo.getPrice(name).ifPresent(price -> {
            basket.getItems().add(new StandardItem(name, price));
        });
        return this;
    }

    public BasketBuilder withItem(String name, int quantity) {
        for (int i = 0; i < quantity; i++) {
            withItem(name);
        }
        return this;
    }

    public BasketBuilder withItem(String name, BigDecimal weight) {
        pricingInfo.getPricePerKilo(name).ifPresent(pricePerKilo -> {
            basket.getItems().add(new WeightedItem(name, pricePerKilo, weight));
        });
        return this;
    }

    public BasketBuilder withItems(String... names) {
        for (String name : names) {
            withItem(name);
        }

        return this;
    }

    public Basket build() {
        return basket;
    }
}
