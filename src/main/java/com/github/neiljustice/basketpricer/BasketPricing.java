package com.github.neiljustice.basketpricer;

import java.math.BigDecimal;

public class BasketPricing {
    private final BigDecimal preOffers;

    private final BigDecimal savings;

    private final BigDecimal total;

    public BasketPricing(BigDecimal preOffers, BigDecimal savings, BigDecimal total) {
        this.preOffers = preOffers;
        this.savings = savings;
        this.total = total;
    }

    public BigDecimal getPreOffers() {
        return preOffers;
    }

    public BigDecimal getSavings() {
        return savings;
    }

    public BigDecimal getTotal() {
        return total;
    }
}
