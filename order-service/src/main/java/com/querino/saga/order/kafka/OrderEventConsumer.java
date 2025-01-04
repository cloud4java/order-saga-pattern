package com.querino.saga.order.kafka;

import com.querino.saga.order.domain.OrderEvent;
import com.querino.saga.order.domain.model.OrderStatus;
import com.querino.saga.order.service.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {
    private final OrderService orderService;

    public OrderEventConsumer(OrderService  orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "inventory-response")
    public void handleInventoryEvent(OrderEvent orderEvent) {
        if(orderEvent.getOrderStatus() == OrderStatus.INVENTORY_RESERVED) {
            orderService.updateOrderStatus(orderEvent.getOrderId(), OrderStatus.INVENTORY_RESERVED);
        }
    }

    @KafkaListener(topics = "payment-response")
    public void handlePaymentEvent(OrderEvent orderEvent) {
        if(orderEvent.getOrderStatus() == OrderStatus.PAYMENT_COMPLETED) {
            orderService.updateOrderStatus(orderEvent.getOrderId(), OrderStatus.PAYMENT_COMPLETED);
        }
    }
}
