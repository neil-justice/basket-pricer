package com.github.neiljustice.basketpricer.offers.types;

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
 * BuyXForYCostOffer can represent offers like '2 for £1'
 */
public class BuyXForYCostOffer implements Offer {

    private final String itemName;

    private final int quantity;

    private final BigDecimal price;

    public BuyXForYCostOffer(String itemName, int quantity, BigDecimal price) {
        this.itemName = Objects.requireNonNull(itemName);
        this.quantity = quantity;
        this.price = Objects.requireNonNull(price);

        if (quantity < 2) {
            throw new OfferException("Cannot create offer: quantity less than 2: " + quantity);
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new OfferException("Cannot create offer: price was not positive : " + price);
        }
    }

    @Override
    public AppliedOffer apply(Basket basket) {
        final BasketItem item = basket.getItem(itemName);
        if (item == null) {
            return AppliedOffer.NotApplicable();
        }

        final int amountBought = item.getQuantity().intValueExact();
        if (amountBought < quantity) {
            return AppliedOffer.NotApplicable();
        }

        final int timesToApply = amountBought / quantity;
        final int leftover = amountBought % quantity;
        final BigDecimal totalCost = price
                .multiply(new BigDecimal(timesToApply))
                .add(new BigDecimal(leftover).multiply(item.getPricePer()));
        return new AppliedOffer(item.getPrice().subtract(totalCost), true);
    }

    @Override
    public void validate(PricingInfo pricingInfo) {
        Item item = pricingInfo.getItem(itemName);
        if (item == null) {
            throw new OfferException("Could not find item " + itemName);
        }
        if (item.getPricingUnit() != PricingUnit.PER_ITEM) {
            throw new OfferException("Offer cannot be applied to items priced by weight");
        }
        final BigDecimal priceWithoutDeal = item.getPricePer().multiply(new BigDecimal(quantity));
        if (price.compareTo(priceWithoutDeal) >= 0) {
            throw new OfferException(String.format(
                    "Discounted price (%.2f) was equal to or larger than cost without deal %.2f: ",
                    price, priceWithoutDeal));
        }
    }
}
