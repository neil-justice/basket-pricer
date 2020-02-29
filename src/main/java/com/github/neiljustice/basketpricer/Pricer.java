package com.github.neiljustice.basketpricer;

import com.github.neiljustice.basketpricer.basket.Basket;
import com.github.neiljustice.basketpricer.basket.Item;
import com.github.neiljustice.basketpricer.offers.AppliedOffer;
import com.github.neiljustice.basketpricer.offers.Offer;

import java.math.BigDecimal;
import java.util.List;

public class Pricer {

    private final List<Offer> offers;

    public Pricer(List<Offer> offers) {
        this.offers = offers;
    }

    public BasketPricing priceBasket(Basket basket) {
        if (basket == null || basket.getItems() == null || basket.getItems().isEmpty()) {
            return new BasketPricing(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        final BigDecimal preOffers = basket.getItems().stream()
                .map(Item::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal savings = offers.stream()
                .map(offer -> offer.applyOffer(basket))
                .filter(AppliedOffer::isApplicable)
                .map(AppliedOffer::getSavings)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal total = preOffers.subtract(savings);

        return new BasketPricing(preOffers, savings, total);
    }
}
