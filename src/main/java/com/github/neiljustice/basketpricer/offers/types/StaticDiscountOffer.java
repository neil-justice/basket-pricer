package com.github.neiljustice.basketpricer.offers.types;

import com.github.neiljustice.basketpricer.CurrencyConfiguration;
import com.github.neiljustice.basketpricer.basket.Basket;
import com.github.neiljustice.basketpricer.PricingInfo;
import com.github.neiljustice.basketpricer.offers.AppliedOffer;
import com.github.neiljustice.basketpricer.offers.Offer;
import com.github.neiljustice.basketpricer.offers.OfferException;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * StaticDiscountOffer can represent offers such as '£3 off'.
 */
public class StaticDiscountOffer implements Offer {

    private final BigDecimal discount;

    private final String itemName;

    public StaticDiscountOffer(BigDecimal discount, String itemName) {
        this.discount = CurrencyConfiguration.scale(Objects.requireNonNull(discount));
        this.itemName = Objects.requireNonNull(itemName);

        if (discount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new OfferException("Cannot create offer: discount was not positive : " + discount);
        }
    }

    @Override
    public AppliedOffer apply(Basket basket) {
        final long count = basket.getItems().stream()
                .filter(i -> Objects.equals(i.getName(), itemName))
                .count();

        if (count > 0) {
            return new AppliedOffer(discount.multiply(new BigDecimal(count)), true);
        } else {
            return AppliedOffer.NotApplicable();
        }
    }

    @Override
    public void validate(PricingInfo pricingInfo) {
        // Assumes these sort of deals do not make sense for items priced by weight
        if (!pricingInfo.getPrice(itemName).isPresent()) {
            throw new OfferException("Could not find item " + itemName);
        }
        final BigDecimal price = pricingInfo.getPrice(itemName).get();
        if (discount.compareTo(price) >= 0) {
            throw new OfferException(String.format("Discount (%.2f) was greater than or equal to price (%.2f)", discount, price));
        }
    }
}
