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

class BuyXGetYOfferTest {

    private ItemInfo itemInfo;

    private BasketBuilder basketBuilder;

    @BeforeEach
    void setUp() {
        itemInfo = new ItemInfo();
        itemInfo.registerItem("Beans", new BigDecimal("1.50"), PricingUnit.PER_ITEM);
        itemInfo.registerItem("Bread", new BigDecimal("1.76"), PricingUnit.PER_ITEM);
        itemInfo.registerItem("Onions", new BigDecimal("0.45"), PricingUnit.PER_KILOGRAM_WEIGHT);

        basketBuilder = new BasketBuilder(itemInfo);
    }

    @Test
    void constructionShouldFailIfAmountToBuyIsLessThanAmountToPayFor() {
        assertThrows(OfferException.class, () -> new BuyXGetYOffer("Beans", 1, 2));
    }

    @Test
    void constructionShouldFailIfAmountToBuyIsEqualToPayFor() {
        assertThrows(OfferException.class, () -> new BuyXGetYOffer("Beans", 1, 1));
    }

    @Test
    void constructionShouldFailIfAmountToPayForLessThan1() {
        assertThrows(OfferException.class, () -> new BuyXGetYOffer("Beans", 1, 0));
    }

    @Test
    void ShouldNotApplyToOtherItemTypes() {
        Offer offer = new BuyXGetYOffer("Bread", 2, 1);
        Basket basket = basketBuilder.withItem("Beans", 4).build();
        AppliedOffer res = offer.apply(basket);
        assertFalse(res.isApplicable());
        assertEquals(res.getSavings(), BigDecimal.ZERO);
    }

    @Test
    void ShouldNotApplyIfLessThanXBought() {
        Offer offer = new BuyXGetYOffer("Beans", 5, 2);
        Basket basket = basketBuilder.withItem("Beans", 4).build();
        AppliedOffer res = offer.apply(basket);
        assertFalse(res.isApplicable());
        assertEquals(res.getSavings(), BigDecimal.ZERO);
    }

    @Test
    void BuyOneGetOneFreeShouldWork() {
        Offer offer = new BuyXGetYOffer("Beans", 2, 1);
        Basket basket = basketBuilder.withItem("Beans", 2).build();
        AppliedOffer res = offer.apply(basket);
        assertTrue(res.isApplicable());
        assertEquals(res.getSavings(), new BigDecimal("1.50"));
    }

    @Test
    void BuyOneGetOneFreeShouldApplyTwice() {
        Offer offer = new BuyXGetYOffer("Beans", 2, 1);
        Basket basket = basketBuilder.withItem("Beans", 4).build();
        AppliedOffer res = offer.apply(basket);
        assertTrue(res.isApplicable());
        assertEquals(res.getSavings(), new BigDecimal("3.00"));
    }

    @Test
    void BuyOneGetOneFreeShouldIgnoreLeftovers() {
        Offer offer = new BuyXGetYOffer("Beans", 2, 1);
        Basket basket = basketBuilder.withItem("Beans", 5).build();
        AppliedOffer res = offer.apply(basket);
        assertTrue(res.isApplicable());
        assertEquals(res.getSavings(), new BigDecimal("3.00"));
    }

    @Test
    void ThreeForTwoShouldWork() {
        Offer offer = new BuyXGetYOffer("Beans", 3, 2);
        Basket basket = basketBuilder.withItem("Beans", 3).build();
        AppliedOffer res = offer.apply(basket);
        assertTrue(res.isApplicable());
        assertEquals(res.getSavings(), new BigDecimal("1.50"));
    }

    @Test
    void FiveForThreeShouldWork() {
        Offer offer = new BuyXGetYOffer("Beans", 5, 3);
        Basket basket = basketBuilder.withItem("Beans", 5).build();
        AppliedOffer res = offer.apply(basket);
        assertTrue(res.isApplicable());
        assertEquals(res.getSavings(), new BigDecimal("3.00"));
    }

    @Test
    void validateShouldNotAllowDiscountOnUnknownProduct() {
        Offer offer = new BuyXGetYOffer("Beans 2.0", 2, 1);
        assertThrows(OfferException.class, () -> offer.validate(itemInfo));
    }

    @Test
    void validateShouldNotAllowDiscountOnProductPricedByWeight() {
        Offer offer = new BuyXGetYOffer("Onions", 2, 1);
        assertThrows(OfferException.class, () -> offer.validate(itemInfo));
    }
}