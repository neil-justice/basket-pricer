package com.github.neiljustice.basketpricer.offers;

import com.github.neiljustice.basketpricer.basket.Basket;
import com.github.neiljustice.basketpricer.PricingInfo;

public interface Offer {

    AppliedOffer apply(Basket basket);

    void validate(PricingInfo pricingInfo);
}
