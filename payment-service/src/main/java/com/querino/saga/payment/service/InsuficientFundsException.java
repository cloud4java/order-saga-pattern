package com.querino.saga.payment.service;

public class InsuficientFundsException extends RuntimeException {
    public InsuficientFundsException(String insufficientFunds) {
    }

    public InsuficientFundsException(String insufficientFunds, Throwable cause) {
        super(insufficientFunds, cause);
    }
}
