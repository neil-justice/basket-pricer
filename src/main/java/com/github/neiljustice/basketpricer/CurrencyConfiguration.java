package com.github.neiljustice.basketpricer;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Static helper class to keep currency information in one place and enable easily
 * changing the currency and rounding mode used across the application.
 * <p>
 * This could eventually be read from a properties file or database.
 */
public final class CurrencyConfiguration {

    private static final Currency currency = Currency.getInstance("GBP");

    private CurrencyConfiguration() {
        // Disable instantiation
    }

    public static int getRoundingMode() {
        return BigDecimal.ROUND_HALF_EVEN;
    }

    public static Currency getCurrency() {
        return currency;
    }

    public static int getDecimalPlaces() {
        return currency.getDefaultFractionDigits();
    }

    public static BigDecimal scale(BigDecimal cost) {
        return cost.setScale(
                CurrencyConfiguration.getDecimalPlaces(),
                CurrencyConfiguration.getRoundingMode()
        );
    }
}
