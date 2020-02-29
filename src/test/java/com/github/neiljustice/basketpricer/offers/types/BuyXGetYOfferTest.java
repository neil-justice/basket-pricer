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

class BuyXGetYOfferTest {

    @Test
    void constructionShouldFailIfAmountToBuyIsLessThanAmountToPayFor() {
        assertThrows(OfferException.class, () -> new BuyXGetYOffer(1, 2, "Beans"));
    }

    @Test
    void constructionShouldFailIfAmountToBuyIsEqualToPayFor() {
        assertThrows(OfferException.class, () -> new BuyXGetYOffer(1, 1, "Beans"));
    }

    @Test
    void constructionShouldFailIfAmountToPayForLessThan1() {
        assertThrows(OfferException.class, () -> new BuyXGetYOffer(1, 0, "Beans"));
    }

    @Test
    void ShouldNotApplyToOtherItemTypes() {
        Offer offer = new BuyXGetYOffer(2, 1, "Bread");
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
    void ShouldNotApplyIfLessThanXBought() {
        Offer offer = new BuyXGetYOffer(5, 2, "Beans");
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
    void BuyOneGetOneFreeShouldWork() {
        Offer offer = new BuyXGetYOffer(2, 1, "Beans");
        Basket basket = new Basket(Arrays.asList(
                new StandardItem("Beans", new BigDecimal("1.50")),
                new StandardItem("Beans", new BigDecimal("1.50"))
        ));
        AppliedOffer res = offer.applyOffer(basket);
        assertTrue(res.isApplicable());
        assertEquals(res.getSavings(), new BigDecimal("1.50"));
    }

    @Test
    void BuyOneGetOneFreeShouldApplyTwice() {
        Offer offer = new BuyXGetYOffer(2, 1, "Beans");
        Basket basket = new Basket(Arrays.asList(
                new StandardItem("Beans", new BigDecimal("1.50")),
                new StandardItem("Beans", new BigDecimal("1.50")),
                new StandardItem("Beans", new BigDecimal("1.50")),
                new StandardItem("Beans", new BigDecimal("1.50"))
        ));
        AppliedOffer res = offer.applyOffer(basket);
        assertTrue(res.isApplicable());
        assertEquals(res.getSavings(), new BigDecimal("3.00"));
    }

    @Test
    void BuyOneGetOneFreeShouldIgnoreLeftovers() {
        Offer offer = new BuyXGetYOffer(2, 1, "Beans");
        Basket basket = new Basket(Arrays.asList(
                new StandardItem("Beans", new BigDecimal("1.50")),
                new StandardItem("Beans", new BigDecimal("1.50")),
                new StandardItem("Beans", new BigDecimal("1.50")),
                new StandardItem("Beans", new BigDecimal("1.50")),
                new StandardItem("Beans", new BigDecimal("1.50"))
        ));
        AppliedOffer res = offer.applyOffer(basket);
        assertTrue(res.isApplicable());
        assertEquals(res.getSavings(), new BigDecimal("3.00"));
    }

    @Test
    void ThreeForTwoShouldWork() {
        Offer offer = new BuyXGetYOffer(3, 2, "Beans");
        Basket basket = new Basket(Arrays.asList(
                new StandardItem("Beans", new BigDecimal("1.50")),
                new StandardItem("Beans", new BigDecimal("1.50")),
                new StandardItem("Beans", new BigDecimal("1.50"))
        ));
        AppliedOffer res = offer.applyOffer(basket);
        assertTrue(res.isApplicable());
        assertEquals(res.getSavings(), new BigDecimal("1.50"));
    }

    @Test
    void FiveForThreeShouldWork() {
        Offer offer = new BuyXGetYOffer(5, 3, "Beans");
        Basket basket = new Basket(Arrays.asList(
                new StandardItem("Beans", new BigDecimal("1.50")),
                new StandardItem("Beans", new BigDecimal("1.50")),
                new StandardItem("Beans", new BigDecimal("1.50")),
                new StandardItem("Beans", new BigDecimal("1.50")),
                new StandardItem("Beans", new BigDecimal("1.50"))
        ));
        AppliedOffer res = offer.applyOffer(basket);
        assertTrue(res.isApplicable());
        assertEquals(res.getSavings(), new BigDecimal("3.00"));
    }
}