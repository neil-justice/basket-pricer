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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class AnyInSetForXOffer implements Offer {

    protected final List<String> itemNames;

    private final int quantity;

    private final BigDecimal price;

    public AnyInSetForXOffer(List<String> itemNames, int quantity, BigDecimal price) {
        this.itemNames = Objects.requireNonNull(itemNames);
        this.quantity = quantity;
        this.price = Objects.requireNonNull(price);

        if (itemNames.isEmpty()) {
            throw new OfferException("Cannot create offer: no items in offer");
        }
        if (quantity < 2) {
            throw new OfferException("Cannot create offer: quantity less than 2: " + quantity);
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new OfferException("Cannot create offer: price was not positive : " + price);
        }
    }

    @Override
    public AppliedOffer apply(Basket basket) {
        List<BasketItem> items = basket.getItems(itemNames);

        final int amountBought = items.stream()
                .map(item -> item.getQuantity().intValueExact())
                .reduce(0, Integer::sum);

        if (amountBought < quantity) {
            return AppliedOffer.NotApplicable();
        }

        final int timesToApply = amountBought / quantity;
        final int leftover = amountBought % quantity;
        final BigDecimal costOfItemsInOffer = price.multiply(new BigDecimal(timesToApply));
        final BigDecimal leftoverCost = findLeftoverCost(items, leftover);

        final BigDecimal costPreOffer = items.stream()
                .map(BasketItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new AppliedOffer(costPreOffer.subtract(costOfItemsInOffer).subtract(leftoverCost), true);
    }

    /**
     * Find the cost of the items not covered by the deal. For example, if quantity is 6 and 8 matching items
     * are in the basket, find the cost of the 2 most expensive, as it seems with these sort of offers the cheapest
     * are included before the more expensive in general. It might be nice to make that configurable, though.
     */
    private BigDecimal findLeftoverCost(List<BasketItem> items, int leftover) {
        items.sort(Comparator.comparing(BasketItem::getPricePer, BigDecimal::compareTo).reversed());

        int visited = 0;
        int i = 0;
        BigDecimal leftoverCost = BigDecimal.ZERO;
        while (visited < leftover) {
            BasketItem item = items.get(i);
            final int quantity = item.getQuantity().intValueExact();
            final int toAdd = Math.min(quantity, leftover - visited);
            visited += quantity;
            i++;
            leftoverCost = leftoverCost.add(item.getPricePer().multiply(new BigDecimal(toAdd)));
        }
        return leftoverCost;
    }

    @Override
    public void validate(PricingInfo pricingInfo) {
        for (String name : itemNames) {
            Item item = pricingInfo.getItem(name);
            if (item == null) {
                throw new OfferException("Could not find item " + name);
            }
            if (item.getPricingUnit() != PricingUnit.PER_ITEM) {
                throw new OfferException("Offer cannot be applied to items priced by weight");
            }
        }
    }

    public List<String> getItemNames() {
        return Collections.unmodifiableList(itemNames);
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
