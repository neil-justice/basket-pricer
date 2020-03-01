package com.github.neiljustice.basketpricer.offers.types;

import com.github.neiljustice.basketpricer.ItemInfo;
import com.github.neiljustice.basketpricer.basket.Item;
import com.github.neiljustice.basketpricer.offers.OfferException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Objects;

/**
 * BuyXForYCostOffer can represent offers like '2 for £1'
 */
public class BuyXForYCostOffer extends AnyInSetForXOffer {

    public BuyXForYCostOffer(String itemName, int quantity, BigDecimal price) {
        super(Collections.singletonList(itemName), quantity, price);
        Objects.requireNonNull(itemName);
    }

    @Override
    public void validate(ItemInfo itemInfo) {
        super.validate(itemInfo);
        final String itemName = itemNames.get(0);
        Item item = itemInfo.getItem(itemName);
        final BigDecimal priceWithoutDeal = item.getPricePer().multiply(new BigDecimal(getQuantity()));
        if (getPrice().compareTo(priceWithoutDeal) >= 0) {
            throw new OfferException(String.format(
                    "Discounted price (%.2f) was equal to or larger than cost without deal %.2f: ",
                    getPrice(), priceWithoutDeal));
        }
    }
}
