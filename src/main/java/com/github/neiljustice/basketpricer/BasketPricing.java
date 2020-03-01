package com.github.neiljustice.basketpricer;

import com.github.neiljustice.basketpricer.basket.BasketItem;
import com.github.neiljustice.basketpricer.basket.PricingUnit;
import com.github.neiljustice.basketpricer.offers.AppliedOffer;

import java.math.BigDecimal;
import java.util.Collection;

public class BasketPricing {

    private final Collection<BasketItem> items;

    private final BigDecimal preOffers;

    private final Collection<AppliedOffer> offers;

    private final BigDecimal savings;

    private final BigDecimal total;

    public BasketPricing(Collection<BasketItem> items,
                         BigDecimal preOffers,
                         Collection<AppliedOffer> offers,
                         BigDecimal savings,
                         BigDecimal total) {
        this.items = items;
        this.preOffers = preOffers;
        this.offers = offers;
        this.savings = savings;
        this.total = total;
    }

    public Collection<BasketItem> getItems() {
        return items;
    }

    public BigDecimal getPreOffers() {
        return preOffers;
    }

    public Collection<AppliedOffer> getOffers() {
        return offers;
    }

    public BigDecimal getSavings() {
        return savings;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void printSummary() {
        final String currencySymbol = CurrencyConfiguration.getCurrency().getSymbol();
        System.out.println("Items:");
        for (BasketItem item : items) {
            if (item.getPricingUnit() == PricingUnit.PER_ITEM) {
                System.out.printf("%-35s %dx : %s%.2f\n", item.getName(), item.getQuantity().intValueExact(), currencySymbol, item.getPrice());
            } else {
                System.out.printf("%-35s %.3fkg : %s%.2f\n", item.getName(), item.getQuantity(), currencySymbol, item.getPrice());
            }
        }
        System.out.printf("%-35s %s%.2f\n\n", "Sub-total", currencySymbol, preOffers);
        System.out.println("Offers:");
        for (AppliedOffer offer : offers) {
            System.out.printf("%-35s -%s%.2f\n", offer.getName(), currencySymbol, offer.getSavings());
        }
        System.out.printf("%-35s -%s%.2f\n\n", "Total savings", currencySymbol, savings);
        System.out.printf("%-35s %s%.2f\n\n", "Total to pay", currencySymbol, total);
    }
}
