package com.github.neiljustice.basketpricer.offers.types;

import com.github.neiljustice.basketpricer.ItemInfo;
import com.github.neiljustice.basketpricer.basket.Basket;
import com.github.neiljustice.basketpricer.basket.BasketBuilder;
import com.github.neiljustice.basketpricer.basket.PricingUnit;
import com.github.neiljustice.basketpricer.offers.AppliedOffer;
import com.github.neiljustice.basketpricer.offers.Offer;
import com.github.neiljustice.basketpricer.offers.OfferException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StaticDiscountOfferTest {

    private ItemInfo itemInfo;

    private BasketBuilder basketBuilder;

    @BeforeEach
    void setUp() {
        itemInfo = new ItemInfo();
        itemInfo.registerItem("Beans", new BigDecimal("1.50"), PricingUnit.PER_ITEM);
        itemInfo.registerItem("Bread", new BigDecimal("1.76"), PricingUnit.PER_ITEM);

        basketBuilder = new BasketBuilder(itemInfo);
    }

    @Test
    void shouldFailToConstructNegativeDiscounts() {
        assertThrows(OfferException.class, () -> new StaticDiscountOffer("Bread", new BigDecimal("-1.00")));
    }

    @Test
    void shouldFailToConstructZeroDiscounts() {
        assertThrows(OfferException.class, () -> new StaticDiscountOffer("Bread", new BigDecimal("0.00")));
    }

    @Test
    void shouldNotApplyIfItemNotPresent() {
        Offer offer = new StaticDiscountOffer("Bread", new BigDecimal("0.50"));
        Basket basket = basketBuilder.withItem("Beans", 4).build();
        AppliedOffer res = offer.apply(basket);
        assertFalse(res.isApplicable());
        assertEquals(res.getSavings(), BigDecimal.ZERO);
    }

    @Test
    void shouldApplyOnceIfItemPresentOnce() {
        Offer offer = new StaticDiscountOffer("Bread", new BigDecimal("0.50"));
        Basket basket = basketBuilder
                .withItem("Beans", 3)
                .withItem("Bread")
                .build();
        AppliedOffer res = offer.apply(basket);
        assertTrue(res.isApplicable());
        assertEquals(res.getSavings(), new BigDecimal("0.50"));
    }


    @Test
    void shouldApplyForEachItemPresent() {
        Offer offer = new StaticDiscountOffer("Bread", new BigDecimal("0.50"));
        Basket basket = basketBuilder
                .withItem("Beans", 3)
                .withItem("Bread", 4)
                .build();
        AppliedOffer res = offer.apply(basket);
        assertTrue(res.isApplicable());
        assertEquals(res.getSavings(), new BigDecimal("2.00"));
    }

    @Test
    void validateShouldNotAllowDiscountEqualToPrice() {
        Offer offer = new StaticDiscountOffer("Bread", new BigDecimal("1.76"));
        assertThrows(OfferException.class, () -> offer.validate(itemInfo));
    }

    @Test
    void validateShouldNotAllowDiscountGreaterThanPrice() {
        Offer offer = new StaticDiscountOffer("Bread", new BigDecimal("2.00"));
        assertThrows(OfferException.class, () -> offer.validate(itemInfo));
    }

    @Test
    void validateShouldNotAllowDiscountOnUnknownProduct() {
        Offer offer = new StaticDiscountOffer("Fork Handles", new BigDecimal("1.00"));
        assertThrows(OfferException.class, () -> offer.validate(itemInfo));
    }
}