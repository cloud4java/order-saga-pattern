package com.querino.saga.order.kafka;

import com.querino.saga.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventConsumer {
    private static final String INVENTORY_ERROR = "inventory-error";
    private final OrderService orderService;

    public OrderEventConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = INVENTORY_ERROR)
    public void handlePaymentEvent(String transactionErroId) {
        try {
            orderService.handleTransactionError(transactionErroId);
        } catch (Exception e) {
            log.error("Error while handling inventory error: {}", e.getMessage());
        }
    }
}
