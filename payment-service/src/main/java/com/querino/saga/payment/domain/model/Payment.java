package com.querino.saga.payment.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private PaymentStatus paymentStatus;
    private BigDecimal amount;
    private Long orderId;

    public PaymentDTO toDTO() {
        return new PaymentDTO(id, paymentStatus, amount, orderId);
    }

    public Payment fromDTO(PaymentDTO paymentDTO) {
        this.id = paymentDTO.getId();
        this.paymentStatus = paymentDTO.getPaymentStatus();
        this.amount = paymentDTO.getAmount();
        this.orderId = paymentDTO.getOrderId();
        return this;
    }
}