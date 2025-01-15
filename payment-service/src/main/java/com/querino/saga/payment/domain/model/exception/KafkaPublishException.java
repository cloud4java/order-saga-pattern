package com.querino.saga.payment.domain.model.exception;

public class KafkaPublishException extends RuntimeException {
    public KafkaPublishException(String s, Exception e) {
        super(s, e);
    }
}
