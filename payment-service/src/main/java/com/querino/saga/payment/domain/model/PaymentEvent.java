package com.querino.saga.payment.domain.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentEvent {
    private Long id;
    private Long orderId;
    private String customerName;
    private PaymentStatus paymentStatus;
    private BigDecimal totalAmount;
}