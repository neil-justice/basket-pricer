package com.github.neiljustice.basketpricer.offers.types;

import com.github.neiljustice.basketpricer.ItemInfo;
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
 * BuyXGetYOffer can represent offers such as 'buy one get one free', or '3 for the price of 2'.
 */
public class BuyXGetYOffer implements Offer {
    private final int amountToBuy;

    private final int amountToPayFor;

    private final String itemName;

    public BuyXGetYOffer(String itemName, int amountToBuy, int amountToPayFor) {
        this.amountToBuy = amountToBuy;
        this.amountToPayFor = amountToPayFor;
        this.itemName = Objects.requireNonNull(itemName);

        if (amountToBuy <= amountToPayFor) {
            throw new OfferException(String.format(
                    "Cannot create offer: amount to buy (%d) less than or equal to amount to pay for (%d)",
                    amountToBuy, amountToPayFor));
        }
        if (amountToPayFor < 1) {
            // I've assumed that we don't want to allow offers which give things away for free...
            throw new OfferException(String.format("Cannot create offer: amount to pay for (%d) less than 1",
                    amountToPayFor));
        }
    }

    @Override
    public AppliedOffer apply(Basket basket) {
        final BasketItem item = basket.getItem(itemName);
        if (item == null) {
            return AppliedOffer.NotApplicable();
        }

        final int amountBought = item.getQuantity().intValueExact();
        if (amountBought < amountToBuy) {
            return AppliedOffer.NotApplicable();
        }

        final BigDecimal pricePer = item.getPricePer();
        final int timesToApply = amountBought / amountToBuy;
        final BigDecimal savings = pricePer
                .multiply(new BigDecimal(timesToApply))
                .multiply(new BigDecimal(amountToBuy - amountToPayFor));
        return new AppliedOffer(savings, true);
    }

    @Override
    public void validate(ItemInfo itemInfo) {
        Item item = itemInfo.getItem(itemName);
        if (item == null) {
            throw new OfferException("Could not find item " + itemName);
        }
        if (item.getPricingUnit() != PricingUnit.PER_ITEM) {
            throw new OfferException("Offer cannot be applied to items priced by weight");
        }
    }
}
