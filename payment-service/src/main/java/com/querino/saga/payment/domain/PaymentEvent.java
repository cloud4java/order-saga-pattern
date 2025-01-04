package com.querino.saga.payment.domain;

import com.querino.saga.payment.domain.model.PaymentDTO;
import com.querino.saga.payment.domain.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentEvent {
    private String orderId;
    private PaymentStatus paymentStatus;
    private PaymentDTO paymentDTO;
}
