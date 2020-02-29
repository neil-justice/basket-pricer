package com.github.neiljustice.basketpricer.offers.types;

import com.github.neiljustice.basketpricer.basket.Basket;
import com.github.neiljustice.basketpricer.basket.StandardItem;
import com.github.neiljustice.basketpricer.offers.AppliedOffer;
import com.github.neiljustice.basketpricer.offers.Offer;
import com.github.neiljustice.basketpricer.offers.OfferException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class StaticDiscountOfferTest {

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
        Basket basket = new Basket(Arrays.asList(
                new StandardItem("Beans", new BigDecimal("1.50")),
                new StandardItem("Beans", new BigDecimal("1.50")),
                new StandardItem("Beans", new BigDecimal("1.50")),
                new StandardItem("Beans", new BigDecimal("1.50"))
        ));
        AppliedOffer res = offer.applyOffer(basket);
        assertFalse(res.isApplicable());
        assertEquals(res.getSavings(), BigDecimal.ZERO);
    }

    @Test
    void shouldApplyOnceIfItemPresentOnce() {
        Offer offer = new StaticDiscountOffer(new BigDecimal("0.50"), "Bread");
        Basket basket = new Basket(Arrays.asList(
                new StandardItem("Bread", new BigDecimal("1.76")),
                new StandardItem("Beans", new BigDecimal("1.50")),
                new StandardItem("Beans", new BigDecimal("1.50")),
                new StandardItem("Beans", new BigDecimal("1.50"))
        ));
        AppliedOffer res = offer.applyOffer(basket);
        assertTrue(res.isApplicable());
        assertEquals(res.getSavings(), new BigDecimal("0.50"));
    }


    @Test
    void shouldApplyForEachItemPresent() {
        Offer offer = new StaticDiscountOffer(new BigDecimal("0.50"), "Bread");
        Basket basket = new Basket(Arrays.asList(
                new StandardItem("Bread", new BigDecimal("1.76")),
                new StandardItem("Bread", new BigDecimal("1.76")),
                new StandardItem("Bread", new BigDecimal("1.76")),
                new StandardItem("Bread", new BigDecimal("1.76")),
                new StandardItem("Beans", new BigDecimal("1.50")),
                new StandardItem("Beans", new BigDecimal("1.50")),
                new StandardItem("Beans", new BigDecimal("1.50"))
        ));
        AppliedOffer res = offer.applyOffer(basket);
        assertTrue(res.isApplicable());
        assertEquals(res.getSavings(), new BigDecimal("2.00"));
    }
}