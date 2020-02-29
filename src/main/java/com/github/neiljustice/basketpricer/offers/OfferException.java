package com.github.neiljustice.basketpricer.offers;

public class OfferException extends RuntimeException {
    public OfferException(String s) {
        super(s);
    }

    public OfferException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
