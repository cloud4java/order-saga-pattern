package com.querino.saga.inventory.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Long id;
    private String customerName;
    private OrderStatus orderStatus;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
}