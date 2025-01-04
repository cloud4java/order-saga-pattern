package com.querino.saga.order.domain;

import com.querino.saga.order.domain.model.dto.OrderDTO;
import com.querino.saga.order.domain.model.OrderStatus;
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
