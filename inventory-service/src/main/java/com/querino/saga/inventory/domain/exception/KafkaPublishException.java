package com.querino.saga.inventory.domain.exception;

public class KafkaPublishException extends RuntimeException {
    public KafkaPublishException(String s, Exception e) {
        super(s, e);
    }
    public KafkaPublishException(String s) {
        super(s);
    }
}
