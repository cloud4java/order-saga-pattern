package com.querino.saga.inventory.domain;

public enum OrderStatus {
    ORDER_CREATED,
    INVENTORY_RESERVED,
    PAYMENT_COMPLETED, PAYMENT_PROCESSED
}