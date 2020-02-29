package com.github.neiljustice.basketpricer.offers;

import com.github.neiljustice.basketpricer.basket.Basket;

public interface Offer {

    AppliedOffer applyOffer(Basket basket);
}
