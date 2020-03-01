package com.github.neiljustice.basketpricer.offers;

import com.github.neiljustice.basketpricer.ItemInfo;
import com.github.neiljustice.basketpricer.basket.Basket;

public interface Offer {

    AppliedOffer apply(Basket basket);

    void validate(ItemInfo itemInfo);
}
