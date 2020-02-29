package com.github.neiljustice.basketpricer.basket;

import com.github.neiljustice.basketpricer.CurrencyConfiguration;

import java.math.BigDecimal;

/**
 * Represents an item in a basket that is priced by weight, such as onions.
 */
public class WeightedItem implements Item {

    private final String name;

    private final BigDecimal pricePerKilo;

    private final BigDecimal weightInKilos;

    public WeightedItem(String name, BigDecimal pricePerKilo, BigDecimal weightInKilos) {
        this.name = name;
        this.pricePerKilo = CurrencyConfiguration.scale(pricePerKilo);
        this.weightInKilos = weightInKilos;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public BigDecimal getPrice() {
        return CurrencyConfiguration.scale(pricePerKilo.multiply(weightInKilos));
    }
}
