package com.github.neiljustice.basketpricer;

import com.github.neiljustice.basketpricer.basket.Basket;
import com.github.neiljustice.basketpricer.basket.BasketItem;
import com.github.neiljustice.basketpricer.offers.AppliedOffer;
import com.github.neiljustice.basketpricer.offers.Offer;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;

public class Pricer {

    private final Collection<Offer> offers;

    private final PricingInfo pricingInfo;

    public Pricer(Collection<Offer> offers, PricingInfo pricingInfo) {
        this.offers = Objects.requireNonNull(offers);
        this.pricingInfo = Objects.requireNonNull(pricingInfo);

        for (Offer offer : this.offers) {
            offer.validate(pricingInfo);
        }
    }

    public BasketPricing priceBasket(Basket basket) {
        if (basket == null) {
            return new BasketPricing(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        final BigDecimal preOffers = basket.getItems().stream()
                .map(BasketItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal savings = offers.stream()
                .map(offer -> offer.apply(basket))
                .filter(AppliedOffer::isApplicable)
                .map(AppliedOffer::getSavings)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal total = preOffers.subtract(savings);

        return new BasketPricing(preOffers, savings, total);
    }
}
