package com.querino.saga.order.domain.model;

public enum OrderStatus {
    ORDER_CREATED,
    INVENTORY_RESERVED,
    PAYMENT_COMPLETED, PAYMENT_PROCESSED
}
