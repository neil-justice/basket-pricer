package com.github.neiljustice.basketpricer;

import com.github.neiljustice.basketpricer.basket.Basket;
import com.github.neiljustice.basketpricer.basket.StandardItem;
import com.github.neiljustice.basketpricer.basket.Item;
import com.github.neiljustice.basketpricer.basket.WeightedItem;
import com.github.neiljustice.basketpricer.offers.Offer;
import com.github.neiljustice.basketpricer.offers.types.StaticDiscountOffer;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PricerTest {

    @Test
    void shouldPriceNullBasketCorrectly() {
        BasketPricing res = new Pricer(Collections.emptyList()).priceBasket(null);
        assertPricing(res, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Test
    void shouldPriceNullItemsCollectionCorrectly() {
        BasketPricing res = getPricing(Collections.emptyList(), null);
        assertPricing(res, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Test
    void shouldPriceEmptyBasketCorrectly() {
        BasketPricing res = getPricing(Collections.emptyList(), Collections.emptyList());
        assertPricing(res, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Test
    void shouldPriceOneItemCorrectly() {
        BasketPricing res = getPricing(Collections.singletonList(
                new StandardItem("Beans", new BigDecimal("1.50"))
        ), Collections.emptyList());

        assertPricing(res, new BigDecimal("1.50"), BigDecimal.ZERO, new BigDecimal("1.50"));
    }

    @Test
    void shouldPriceManyItemsCorrectly() {
        BasketPricing res = getPricing(Arrays.asList(
                new StandardItem("Beans", new BigDecimal("1.50")),
                new WeightedItem("Bananas", new BigDecimal("1.00"), new BigDecimal("3.000")),
                new StandardItem("Peas", new BigDecimal("0.30"))
        ), Collections.emptyList());

        assertPricing(res, new BigDecimal("4.80"), BigDecimal.ZERO, new BigDecimal("4.80"));
    }

    @Test
    void shouldApplyOneOffer() {
        BasketPricing res = getPricing(Arrays.asList(
                new StandardItem("Beans", new BigDecimal("1.50")),
                new WeightedItem("Bananas", new BigDecimal("1.00"), new BigDecimal("3.000")),
                new StandardItem("Peas", new BigDecimal("0.30"))
        ), Collections.singletonList(
                new StaticDiscountOffer(new BigDecimal("0.50"), "Beans")
        ));

        assertPricing(res, new BigDecimal("4.80"), new BigDecimal("0.50"), new BigDecimal("4.30"));
    }

    @Test
    void shouldApplyManyOffers() {
        BasketPricing res = getPricing(Arrays.asList(
                new StandardItem("Beans", new BigDecimal("1.50")),
                new WeightedItem("Bananas", new BigDecimal("1.00"), new BigDecimal("3.000")),
                new StandardItem("Peas", new BigDecimal("0.30"))
        ), Arrays.asList(
                new StaticDiscountOffer(new BigDecimal("0.50"), "Beans"),
                new StaticDiscountOffer(new BigDecimal("1.50"), "Peas")
        ));

        assertPricing(res, new BigDecimal("4.80"), new BigDecimal("2.00"), new BigDecimal("2.80"));
    }

    private BasketPricing getPricing(List<Item> items, List<Offer> offers) {
        return new Pricer(offers).priceBasket(new Basket(items));
    }

    private void assertPricing(BasketPricing pricing, BigDecimal preOffers, BigDecimal savings, BigDecimal total) {
        assertEquals(pricing.getPreOffers(), preOffers);
        assertEquals(pricing.getSavings(), savings);
        assertEquals(pricing.getTotal(), total);
    }
}