package com.querino.saga.inventory.domain.model.order;

import com.querino.saga.inventory.domain.OrderStatus;
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
    private BigDecimal totalAmount;
    private List<OrderItemDTO> orderItemDTO;
    private String transactionId;
}