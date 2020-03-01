package com.github.neiljustice.basketpricer;

import com.github.neiljustice.basketpricer.basket.Basket;
import com.github.neiljustice.basketpricer.basket.BasketBuilder;
import com.github.neiljustice.basketpricer.basket.PricingUnit;
import com.github.neiljustice.basketpricer.offers.Offer;
import com.github.neiljustice.basketpricer.offers.types.StaticDiscountOffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PricerTest {

    private PricingInfo pricingInfo;

    private BasketBuilder basketBuilder;

    @BeforeEach
    void setUp() {
        pricingInfo = new PricingInfo();
        pricingInfo.registerItem("Beans", new BigDecimal("1.50"), PricingUnit.PER_ITEM);
        pricingInfo.registerItem("Peas", new BigDecimal("0.30"), PricingUnit.PER_ITEM);
        pricingInfo.registerItem("Bananas", new BigDecimal("1.00"), PricingUnit.PER_KILOGRAM_WEIGHT);

        basketBuilder = new BasketBuilder(pricingInfo);
    }

    @Test
    void shouldPriceNullBasketCorrectly() {
        BasketPricing res = new Pricer(Collections.emptyList(), pricingInfo).priceBasket(null);
        assertPricing(res, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Test
    void shouldHandleNullOffersCollectionCorrectly() {
        assertThrows(NullPointerException.class, () -> new Pricer(null, pricingInfo));
    }

    @Test
    void shouldHandleNullPricingInfoCorrectly() {
        assertThrows(NullPointerException.class, () -> new Pricer(Collections.emptyList(), null));
    }

    @Test
    void shouldPriceEmptyBasketCorrectly() {
        BasketPricing res = getPricing(basketBuilder.build(), Collections.emptyList());
        assertPricing(res, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Test
    void shouldPriceOneItemCorrectly() {
        BasketPricing res = getPricing(basketBuilder.withItem("Beans").build(), Collections.emptyList());

        assertPricing(res, new BigDecimal("1.50"), BigDecimal.ZERO, new BigDecimal("1.50"));
    }

    @Test
    void shouldPriceManyItemsCorrectly() {
        BasketPricing res = getPricing(basketBuilder
                .withItem("Beans")
                .withItemByWeight("Bananas", new BigDecimal("3.000"))
                .withItem("Peas")
                .build(), Collections.emptyList());

        assertPricing(res, new BigDecimal("4.80"), BigDecimal.ZERO, new BigDecimal("4.80"));
    }

    @Test
    void shouldApplyOneOffer() {
        BasketPricing res = getPricing(basketBuilder
                .withItem("Beans")
                .withItemByWeight("Bananas", new BigDecimal("3.000"))
                .withItem("Peas")
                .build(), Collections.singletonList(
                new StaticDiscountOffer(new BigDecimal("0.50"), "Beans")
        ));

        assertPricing(res, new BigDecimal("4.80"), new BigDecimal("0.50"), new BigDecimal("4.30"));
    }

    @Test
    void shouldApplyManyOffers() {
        BasketPricing res = getPricing(basketBuilder
                .withItem("Beans")
                .withItemByWeight("Bananas", new BigDecimal("3.000"))
                .withItem("Peas")
                .build(), Arrays.asList(
                new StaticDiscountOffer(new BigDecimal("0.50"), "Beans"),
                new StaticDiscountOffer(new BigDecimal("0.15"), "Peas")
        ));

        assertPricing(res, new BigDecimal("4.80"), new BigDecimal("0.65"), new BigDecimal("4.15"));
    }

    private BasketPricing getPricing(Basket basket, List<Offer> offers) {
        return new Pricer(offers, pricingInfo).priceBasket(basket);
    }

    private void assertPricing(BasketPricing pricing, BigDecimal preOffers, BigDecimal savings, BigDecimal total) {
        assertEquals(pricing.getPreOffers(), preOffers);
        assertEquals(pricing.getSavings(), savings);
        assertEquals(pricing.getTotal(), total);
    }
}