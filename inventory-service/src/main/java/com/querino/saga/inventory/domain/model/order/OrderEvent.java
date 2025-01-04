package com.querino.saga.inventory.domain.model.order;

import com.querino.saga.inventory.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderEvent {
    private String orderId;
    private OrderStatus orderStatus;
    private OrderDTO orderDTO;
}