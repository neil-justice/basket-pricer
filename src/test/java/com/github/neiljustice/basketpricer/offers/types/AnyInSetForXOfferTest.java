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
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class AnyInSetForXOfferTest {

    private ItemInfo itemInfo;

    private BasketBuilder basketBuilder;

    @BeforeEach
    void setUp() {
        itemInfo = new ItemInfo();
        itemInfo.registerItem("Ale 1", new BigDecimal("1.51"), PricingUnit.PER_ITEM);
        itemInfo.registerItem("Ale 2", new BigDecimal("1.76"), PricingUnit.PER_ITEM);
        itemInfo.registerItem("Ale 3", new BigDecimal("1.55"), PricingUnit.PER_ITEM);
        itemInfo.registerItem("Expensive Ale", new BigDecimal("2.45"), PricingUnit.PER_ITEM);
        itemInfo.registerItem("Cheap Ale", new BigDecimal("1.05"), PricingUnit.PER_ITEM);
        itemInfo.registerItem("Beans", new BigDecimal("1.50"), PricingUnit.PER_ITEM);
        itemInfo.registerItem("Potatoes", new BigDecimal("1.31"), PricingUnit.PER_KILOGRAM_WEIGHT);

        basketBuilder = new BasketBuilder(itemInfo);
    }

    @Test
    void constructionShouldFailIfNoItems() {
        assertThrows(OfferException.class, () -> new AnyInSetForXOffer(Collections.emptyList(), 2, new BigDecimal("2.99")));
    }

    @Test
    void constructionShouldFailIfQuantityLessThan2() {
        assertThrows(OfferException.class, () ->  new AnyInSetForXOffer(Arrays.asList("Ale 1", "Ale 2"), 1, new BigDecimal("2.99")));
    }

    @Test
    void constructionShouldFailIfPriceNotPositive() {
        assertThrows(OfferException.class, () ->  new AnyInSetForXOffer(Arrays.asList("Ale 1", "Ale 2"), 2, new BigDecimal("-2.99")));
    }

    @Test
    void ShouldNotApplyToOtherItemTypes() {
        Offer offer = new AnyInSetForXOffer(Arrays.asList("Ale 1", "Ale 2"), 2, new BigDecimal("2.99"));
        Basket basket = basketBuilder.withItem("Beans", 4).build();
        AppliedOffer res = offer.apply(basket);
        assertFalse(res.isApplicable());
        assertEquals(res.getSavings(), BigDecimal.ZERO);
    }

    @Test
    void ShouldNotApplyIfLessThanXBought() {
        Offer offer = new AnyInSetForXOffer(Arrays.asList("Ale 1", "Ale 2"), 5, new BigDecimal("2.99"));
        Basket basket = basketBuilder.withItem("Ale 1", 4).build();
        AppliedOffer res = offer.apply(basket);
        assertFalse(res.isApplicable());
        assertEquals(res.getSavings(), BigDecimal.ZERO);
    }

    @Test
    void ShouldApplyIfXBought() {
        Offer offer = new AnyInSetForXOffer(Arrays.asList(
                "Ale 1",
                "Ale 2",
                "Ale 3",
                "Expensive Ale",
                "Cheap Ale"
        ), 6, new BigDecimal("9.00"));
        Basket basket = basketBuilder
                .withItem("Ale 2", 6)
                .build();
        AppliedOffer res = offer.apply(basket);
        assertTrue(res.isApplicable());
        assertEquals(res.getSavings(), new BigDecimal("1.56"));
    }

    @Test
    void ShouldNotApplyToMostExpensiveIfLeftovers() {
        Offer offer = new AnyInSetForXOffer(Arrays.asList(
                "Ale 1",
                "Ale 2",
                "Ale 3",
                "Expensive Ale",
                "Cheap Ale"
        ), 6, new BigDecimal("9.00"));
        Basket basket = basketBuilder
                .withItem("Ale 1", 6)
                .withItem("Ale 2", 2)
                .withItem("Expensive Ale", 2)
                .build();
        AppliedOffer res = offer.apply(basket);
        assertTrue(res.isApplicable());
        assertEquals(res.getSavings(), new BigDecimal("0.06"));
    }

    @Test
    void ShouldApplyIfVarietyBought() {
        Offer offer = new AnyInSetForXOffer(Arrays.asList(
                "Ale 1",
                "Ale 2",
                "Ale 3",
                "Expensive Ale",
                "Cheap Ale"
        ), 6, new BigDecimal("9.00"));
        Basket basket = basketBuilder
                .withItem("Ale 1", 2)
                .withItem("Ale 2", 2)
                .withItem("Ale 3", 2)
                .build();
        AppliedOffer res = offer.apply(basket);
        assertTrue(res.isApplicable());
        assertEquals(res.getSavings(), new BigDecimal("0.64"));
    }

    @Test
    void ShouldApplyTwice() {
        Offer offer = new AnyInSetForXOffer(Arrays.asList(
                "Ale 1",
                "Ale 2",
                "Ale 3",
                "Expensive Ale",
                "Cheap Ale"
        ), 6, new BigDecimal("9.00"));
        Basket basket = basketBuilder
                .withItem("Ale 1", 6)
                .withItem("Ale 2", 2)
                .withItem("Expensive Ale", 2)
                .withItem("Cheap Ale", 2)
                .build();
        AppliedOffer res = offer.apply(basket);
        assertTrue(res.isApplicable());
        assertEquals(res.getSavings(), new BigDecimal("1.58"));
    }

    @Test
    void validateShouldNotAllowDiscountOnUnknownProduct() {
        Offer offer = new AnyInSetForXOffer(Arrays.asList("Ale 1", "Nonexistent Ale"), 3, new BigDecimal("2.00"));
        assertThrows(OfferException.class, () -> offer.validate(itemInfo));
    }

    @Test
    void validateShouldNotAllowDiscountOnProductPricedByWeight() {
        Offer offer = new AnyInSetForXOffer(Arrays.asList("Beans", "Potatoes"), 3, new BigDecimal("2.00"));
        assertThrows(OfferException.class, () -> offer.validate(itemInfo));
    }
}