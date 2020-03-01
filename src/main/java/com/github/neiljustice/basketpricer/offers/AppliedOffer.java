package com.github.neiljustice.basketpricer.offers;

import java.math.BigDecimal;

public class AppliedOffer {

    private final String name;

    private final BigDecimal savings;

    private final boolean isApplicable;

    public AppliedOffer(String name, BigDecimal savings, boolean isApplicable) {
        this.name = name;
        this.savings = savings;
        this.isApplicable = isApplicable;
    }

    public static AppliedOffer NotApplicable() {
        return new AppliedOffer("", BigDecimal.ZERO, false);
    }

    public String getName() {
        return name;
    }

    public BigDecimal getSavings() {
        return savings;
    }

    public boolean isApplicable() {
        return isApplicable;
    }
}
