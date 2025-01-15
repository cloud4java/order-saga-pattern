package com.querino.saga.inventory.domain.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String s) {
        super(s);
    }

    public InsufficientStockException(String s, Exception e) {
        super(s, e);
    }
}
