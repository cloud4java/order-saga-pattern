package com.querino.saga.inventory.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.querino.saga.inventory.domain.InventoryEvent;
import com.querino.saga.inventory.domain.OrderStatus;
import com.querino.saga.inventory.domain.model.order.OrderEvent;
import com.querino.saga.inventory.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InventoryEventConsumer {
    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    public InventoryEventConsumer(InventoryService inventoryService, ObjectMapper objectMapper) {
        this.inventoryService = inventoryService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.order.topic}")
    public void handleInventoryEvent(String message) {
        log.info("Processing message from topic 'order', message: {}", message);
        try {
            OrderEvent orderEvent = objectMapper.readValue(message, OrderEvent.class);
            if (orderEvent.getOrderStatus() == OrderStatus.ORDER_CREATED) {
                inventoryService.updateQuantity(orderEvent);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "${kafka.payment.error}")
    public void handlePaymentEvent(String message) {
        log.info("Processing payment error message: {}", message);
        try {
            InventoryEvent inventoryEvent = objectMapper.readValue(message, InventoryEvent.class);
            inventoryService.handleTransactionError(inventoryEvent);
        } catch (Exception e) {
            log.error("Error while handling inventory error: {}", e.getMessage());
        }
    }
}
