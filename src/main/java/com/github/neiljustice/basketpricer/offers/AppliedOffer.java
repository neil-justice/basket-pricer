package com.github.neiljustice.basketpricer.offers;

import java.math.BigDecimal;

public class AppliedOffer {
    private final BigDecimal savings;

    private final boolean isApplicable;

    public AppliedOffer(BigDecimal savings, boolean isApplicable) {
        this.savings = savings;
        this.isApplicable = isApplicable;
    }

    public BigDecimal getSavings() {
        return savings;
    }

    public boolean isApplicable() {
        return isApplicable;
    }
}