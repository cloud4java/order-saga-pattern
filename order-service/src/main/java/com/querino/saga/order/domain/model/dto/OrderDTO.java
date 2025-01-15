package com.querino.saga.order.domain.model.dto;

import com.querino.saga.order.domain.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Long id;
    private Long customerId;
    private OrderStatus orderStatus;
    private List<OrderItemDTO> orderItemDTO;
    private String transactionId;
}