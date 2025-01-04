package com.querino.saga.payment.domain.exception;

public class KafkaPublishException extends Throwable {
    public KafkaPublishException(String s, Exception e) {
        super(s, e);
    }
}
