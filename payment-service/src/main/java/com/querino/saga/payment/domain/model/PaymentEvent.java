package com.querino.saga.payment.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentEvent {
    private Long customerId;
    private PaymentStatus paymentStatus;
    private PaymentDTO paymentDTO;
}
