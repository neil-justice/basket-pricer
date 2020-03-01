package com.github.neiljustice.basketpricer;

import com.github.neiljustice.basketpricer.basket.Basket;
import com.github.neiljustice.basketpricer.basket.BasketItem;
import com.github.neiljustice.basketpricer.offers.AppliedOffer;
import com.github.neiljustice.basketpricer.offers.Offer;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Pricer {

    private final Collection<Offer> offers;

    private final ItemInfo itemInfo;

    public Pricer(Collection<Offer> offers, ItemInfo itemInfo) {
        this.offers = Objects.requireNonNull(offers);
        this.itemInfo = Objects.requireNonNull(itemInfo);

        for (Offer offer : this.offers) {
            offer.validate(itemInfo);
        }
    }

    public BasketPricing priceBasket(Basket basket) {
        if (basket == null) {
            return new BasketPricing(Collections.emptyList(), BigDecimal.ZERO, Collections.emptyList(), BigDecimal.ZERO, BigDecimal.ZERO);
        }

        final Collection<BasketItem> items = basket.getItems();

        final BigDecimal preOffers = items.stream()
                .map(BasketItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final List<AppliedOffer> appliedOffers = offers.stream()
                .map(offer -> offer.apply(basket))
                .filter(AppliedOffer::isApplicable)
                .collect(Collectors.toList());

        final BigDecimal savings = appliedOffers.stream()
                .map(AppliedOffer::getSavings)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal total = preOffers.subtract(savings);

        return new BasketPricing(items, preOffers, appliedOffers, savings, total);
    }
}
