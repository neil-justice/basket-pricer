package com.github.neiljustice.basketpricer.offers;

import com.github.neiljustice.basketpricer.CurrencyConfiguration;
import com.github.neiljustice.basketpricer.basket.Item;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;

/**
 * StaticDiscountOffer can represent offers such as '£3 off'.
 */
public class StaticDiscountOffer implements Offer {

    private final BigDecimal discount;

    private final String itemName;

    public StaticDiscountOffer(BigDecimal discount, String itemName) {
        this.discount = CurrencyConfiguration.scale(discount);
        this.itemName = itemName;
    }

    @Override
    public AppliedOffer applyOffer(Collection<Item> items) {
        final long count = items.stream().filter(i -> Objects.equals(i.getName(), itemName)).count();

        if (count > 0) {
            return new AppliedOffer(discount.multiply(new BigDecimal(count)), true);
        } else {
            return new AppliedOffer(BigDecimal.ZERO, false);
        }
    }
}
