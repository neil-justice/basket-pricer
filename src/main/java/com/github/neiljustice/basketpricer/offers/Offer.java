package com.github.neiljustice.basketpricer.offers;

import com.github.neiljustice.basketpricer.basket.Item;

import java.util.Collection;

public interface Offer {

    AppliedOffer applyOffer(Collection<Item> items);
}
