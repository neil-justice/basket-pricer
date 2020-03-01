package com.github.neiljustice.basketpricer.offers.types;

import com.github.neiljustice.basketpricer.CurrencyConfiguration;
import com.github.neiljustice.basketpricer.PricingInfo;
import com.github.neiljustice.basketpricer.basket.Basket;
import com.github.neiljustice.basketpricer.basket.BasketItem;
import com.github.neiljustice.basketpricer.basket.Item;
import com.github.neiljustice.basketpricer.basket.PricingUnit;
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
        BasketItem item = basket.getItem(itemName);
        if (item == null) {
            return AppliedOffer.NotApplicable();
        }

        final int quantity = item.getQuantity().intValueExact();
        if (quantity > 0) {
            return new AppliedOffer(discount.multiply(new BigDecimal(quantity)), true);
        } else {
            return AppliedOffer.NotApplicable();
        }
    }

    @Override
    public void validate(PricingInfo pricingInfo) {
        Item item = pricingInfo.getItem(itemName);
        if (item == null) {
            throw new OfferException("Could not find item " + itemName);
        }
        if (discount.compareTo(item.getPricePer()) >= 0) {
            throw new OfferException(String.format("Discount (%.2f) was greater than or equal to price (%.2f)", discount, item.getPricePer()));
        }
        if (item.getPricingUnit() != PricingUnit.PER_ITEM) {
            throw new OfferException("Offer cannot be applied to items priced by weight");
        }
    }
}
