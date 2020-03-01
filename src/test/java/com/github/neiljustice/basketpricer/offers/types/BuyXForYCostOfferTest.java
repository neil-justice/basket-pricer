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

class BuyXForYCostOfferTest {

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
    void constructionShouldFailIfQuantityLessThan2() {
        assertThrows(OfferException.class, () -> new BuyXForYCostOffer("Beans", 1, new BigDecimal("2.99")));
    }

    @Test
    void constructionShouldFailIfPriceNotPositive() {
        assertThrows(OfferException.class, () -> new BuyXForYCostOffer("Beans", 2, new BigDecimal("-2.99")));
    }

    @Test
    void ShouldNotApplyToOtherItemTypes() {
        Offer offer = new BuyXForYCostOffer("Bread", 2, new BigDecimal("1.00"));
        Basket basket = basketBuilder.withItem("Beans", 4).build();
        AppliedOffer res = offer.apply(basket);
        assertFalse(res.isApplicable());
        assertEquals(res.getSavings(), BigDecimal.ZERO);
    }

    @Test
    void ShouldNotApplyIfLessThanXBought() {
        Offer offer = new BuyXForYCostOffer("Beans", 5, new BigDecimal("3.00"));
        Basket basket = basketBuilder.withItem("Beans", 4).build();
        AppliedOffer res = offer.apply(basket);
        assertFalse(res.isApplicable());
        assertEquals(res.getSavings(), BigDecimal.ZERO);
    }

    @Test
    void ShouldApplyIfXBought() {
        Offer offer = new BuyXForYCostOffer("Beans", 5, new BigDecimal("3.00"));
        Basket basket = basketBuilder.withItem("Beans", 5).build();
        AppliedOffer res = offer.apply(basket);
        assertTrue(res.isApplicable());
        assertEquals(res.getSavings(), new BigDecimal("4.50"));
    }

    @Test
    void ShouldNotApplyToLeftoversIfXPlus1Bought() {
        Offer offer = new BuyXForYCostOffer("Beans", 5, new BigDecimal("3.00"));
        Basket basket = basketBuilder.withItem("Beans", 6).build();
        AppliedOffer res = offer.apply(basket);
        assertTrue(res.isApplicable());
        assertEquals(res.getSavings(), new BigDecimal("4.50"));
    }

    @Test
    void ShouldApplyTwiceIf2XBought() {
        Offer offer = new BuyXForYCostOffer("Beans", 5, new BigDecimal("3.00"));
        Basket basket = basketBuilder.withItem("Beans", 10).build();
        AppliedOffer res = offer.apply(basket);
        assertTrue(res.isApplicable());
        assertEquals(res.getSavings(), new BigDecimal("9.00"));
    }

    @Test
    void validateShouldNotAllowDiscountOnUnknownProduct() {
        Offer offer = new BuyXForYCostOffer("Nonexistent Item", 3, new BigDecimal("2.00"));
        assertThrows(OfferException.class, () -> offer.validate(itemInfo));
    }

    @Test
    void validateShouldNotAllowDiscountOnProductPricedByWeight() {
        Offer offer = new BuyXForYCostOffer("Onions", 3, new BigDecimal("2.00"));
        assertThrows(OfferException.class, () -> offer.validate(itemInfo));
    }

    @Test
    void validateShouldNotAllowDiscountWhichIncreasesPrice() {
        Offer offer = new BuyXForYCostOffer("Beans", 3, new BigDecimal("6.00"));
        assertThrows(OfferException.class, () -> offer.validate(itemInfo));
    }

    @Test
    void validateShouldNotAllowDiscountSameAsPrice() {
        Offer offer = new BuyXForYCostOffer("Beans", 3, new BigDecimal("4.50"));
        assertThrows(OfferException.class, () -> offer.validate(itemInfo));
    }
}