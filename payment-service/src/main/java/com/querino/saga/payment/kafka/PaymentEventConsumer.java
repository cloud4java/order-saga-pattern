package com.querino.saga.payment.kafka;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querino.saga.payment.domain.model.*;
import com.querino.saga.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
public class PaymentEventConsumer {
    private static final String TOPIC_NAME = "inventory-topic";
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;
    public PaymentEventConsumer(PaymentService paymentService, ObjectMapper objectMapper) {
        this.paymentService = paymentService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = TOPIC_NAME)
    public void process(String message) {
        try {
            InventoryEvent inventoryEvent = objectMapper.readValue(message, InventoryEvent.class);
            log.info("Received inventory for payment processing: {}", inventoryEvent);
            if (inventoryEvent.getInventoryStatus() == InventoryStatus.INVENTORY_RESERVED) {
                var totalAmount = inventoryEvent.getInventoryItemDTO().getPrice() * inventoryEvent.getInventoryItemDTO().getQuantity();
                paymentService.debit(new PaymentDTO(null, PaymentStatus.PAYMENT_COMPLETED, BigDecimal.valueOf(totalAmount) , null));
            }
        } catch (Exception e) {
            log.error("Error processing payment for inventory {}: {}", message, e.getMessage());
        }
    }
}
