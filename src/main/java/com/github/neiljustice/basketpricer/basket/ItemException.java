package com.github.neiljustice.basketpricer.basket;

public class ItemException extends RuntimeException {
    public ItemException(String s) {
        super(s);
    }

    public ItemException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
