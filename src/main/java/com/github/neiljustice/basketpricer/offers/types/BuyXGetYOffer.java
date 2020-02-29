package com.github.neiljustice.basketpricer.offers.types;

import com.github.neiljustice.basketpricer.basket.Basket;
import com.github.neiljustice.basketpricer.basket.Item;
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

    public BuyXGetYOffer(int amountToBuy, int amountToPayFor, String itemName) {
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
    public AppliedOffer applyOffer(Basket basket) {
        int amountBought = 0;
        BigDecimal pricePer = BigDecimal.ZERO;

        for (Item item : basket.getItems()) {
            if (Objects.equals(item.getName(), itemName)) {
                amountBought++;
                pricePer = item.getPrice();
            }
        }

        if (amountBought < amountToBuy) {
            return AppliedOffer.NotApplicable();
        } else {
            final int timesToApply = amountBought / amountToBuy;
            final BigDecimal savings = pricePer.multiply(new BigDecimal(timesToApply))
                    .multiply(new BigDecimal(amountToBuy - amountToPayFor));
            return new AppliedOffer(savings, true);
        }
    }
}
