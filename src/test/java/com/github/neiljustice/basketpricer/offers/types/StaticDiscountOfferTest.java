package com.github.neiljustice.basketpricer.offers.types;

import com.github.neiljustice.basketpricer.PricingInfo;
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

    private PricingInfo pricingInfo;

    private BasketBuilder basketBuilder;

    @BeforeEach
    void setUp() {
        pricingInfo = new PricingInfo();
        pricingInfo.registerItem("Beans", new BigDecimal("1.50"), PricingUnit.PER_ITEM);
        pricingInfo.registerItem("Bread", new BigDecimal("1.76"), PricingUnit.PER_ITEM);

        basketBuilder = new BasketBuilder(pricingInfo);
    }

    @Test
    void shouldFailToConstructNegativeDiscounts() {
        assertThrows(OfferException.class, () -> new StaticDiscountOffer(new BigDecimal("-1.00"), "Bread"));
    }

    @Test
    void shouldFailToConstructZeroDiscounts() {
        assertThrows(OfferException.class, () -> new StaticDiscountOffer(new BigDecimal("0.00"), "Bread"));
    }

    @Test
    void shouldNotApplyIfItemNotPresent() {
        Offer offer = new StaticDiscountOffer(new BigDecimal("0.50"), "Bread");
        Basket basket = basketBuilder.withItem("Beans", 4).build();
        AppliedOffer res = offer.apply(basket);
        assertFalse(res.isApplicable());
        assertEquals(res.getSavings(), BigDecimal.ZERO);
    }

    @Test
    void shouldApplyOnceIfItemPresentOnce() {
        Offer offer = new StaticDiscountOffer(new BigDecimal("0.50"), "Bread");
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
        Offer offer = new StaticDiscountOffer(new BigDecimal("0.50"), "Bread");
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
        Offer offer = new StaticDiscountOffer(new BigDecimal("1.76"), "Bread");
        assertThrows(OfferException.class, () -> offer.validate(pricingInfo));
    }

    @Test
    void validateShouldNotAllowDiscountGreaterThanPrice() {
        Offer offer = new StaticDiscountOffer(new BigDecimal("2.00"), "Bread");
        assertThrows(OfferException.class, () -> offer.validate(pricingInfo));
    }

    @Test
    void validateShouldNotAllowDiscountOnUnknownProduct() {
        Offer offer = new StaticDiscountOffer(new BigDecimal("1.00"), "Fork Handles");
        assertThrows(OfferException.class, () -> offer.validate(pricingInfo));
    }
}