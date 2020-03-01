package com.github.neiljustice.basketpricer.demo;

import com.github.neiljustice.basketpricer.BasketPricing;
import com.github.neiljustice.basketpricer.Pricer;
import com.github.neiljustice.basketpricer.ItemInfo;
import com.github.neiljustice.basketpricer.basket.Basket;
import com.github.neiljustice.basketpricer.basket.BasketBuilder;
import com.github.neiljustice.basketpricer.basket.PricingUnit;
import com.github.neiljustice.basketpricer.offers.Offer;
import com.github.neiljustice.basketpricer.offers.types.AnyInSetForXOffer;
import com.github.neiljustice.basketpricer.offers.types.BuyXForYCostOffer;
import com.github.neiljustice.basketpricer.offers.types.BuyXGetYOffer;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Demo {

    /**
     * This example is taken from the exercise instructions.
     */
    public void example1() {
        final ItemInfo itemInfo = new ItemInfo();
        itemInfo.registerItem("Beans", new BigDecimal("0.50"), PricingUnit.PER_ITEM);
        itemInfo.registerItem("Coca-Cola", new BigDecimal("0.70"), PricingUnit.PER_ITEM);
        itemInfo.registerItem("Oranges", new BigDecimal("1.99"), PricingUnit.PER_KILOGRAM_WEIGHT);

        final List<Offer> offers = Arrays.asList(
                new BuyXGetYOffer("Beans", 3, 2),
                new BuyXForYCostOffer("Coca-Cola", 2, new BigDecimal("1.00"))
        );

        final Basket basket = new BasketBuilder(itemInfo)
                .withItem("Beans", 3)
                .withItem("Coca-Cola", 2)
                .withItemByWeightKilos("Oranges", new BigDecimal("0.200"))
                .build();

        final BasketPricing pricing = new Pricer(offers, itemInfo).priceBasket(basket);
        pricing.printSummary();
    }

    /**
     * This example is taken from a real offer found in an online supermarket.
     */
    public void example2() {
        final ItemInfo itemInfo = new ItemInfo();
        itemInfo.registerItem("Watercress, Rocket & Spinach Salad", new BigDecimal("2.00"), PricingUnit.PER_ITEM);
        itemInfo.registerItem("Earthy Beetroot Salad", new BigDecimal("2.00"), PricingUnit.PER_ITEM);
        itemInfo.registerItem("Coleslaw with Yoghurt Dressing", new BigDecimal("2.00"), PricingUnit.PER_ITEM);
        itemInfo.registerItem("Moroccan Cous Cous Salad", new BigDecimal("2.25"), PricingUnit.PER_ITEM);
        itemInfo.registerItem("Potato with Yogurt & Mint", new BigDecimal("2.00"), PricingUnit.PER_ITEM);

        final List<Offer> offers = Collections.singletonList(
                new AnyInSetForXOffer(Arrays.asList(
                        "Watercress, Rocket & Spinach Salad",
                        "Earthy Beetroot Salad",
                        "Coleslaw with Yoghurt Dressing",
                        "Moroccan Cous Cous Salad",
                        "Potato with Yogurt & Mint"
                ), 2, new BigDecimal("3.00"))
        );

        final Basket basket = new BasketBuilder(itemInfo)
                .withItem("Potato with Yogurt & Mint", 1)
                .withItem("Moroccan Cous Cous Salad", 2)
                .build();

        final BasketPricing pricing = new Pricer(offers, itemInfo).priceBasket(basket);
        pricing.printSummary();
    }

    public static void main(String[] args) {
        new Demo().example1();
        new Demo().example2();
    }
}
