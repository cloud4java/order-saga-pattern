package com.querino.saga.payment.domain.model.exception;

public class InsuficientFundsException extends RuntimeException {
    public InsuficientFundsException(String insufficientFunds) {
    }

    public InsuficientFundsException(String insufficientFunds, Throwable cause) {
        super(insufficientFunds, cause);
    }
}
