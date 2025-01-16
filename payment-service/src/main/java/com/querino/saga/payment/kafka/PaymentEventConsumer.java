package com.querino.saga.payment.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.querino.saga.payment.domain.model.inventory.InventoryEvent;
import com.querino.saga.payment.domain.model.inventory.InventoryStatus;
import com.querino.saga.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PaymentEventConsumer {

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    public PaymentEventConsumer(PaymentService paymentService, ObjectMapper objectMapper) {
        this.paymentService = paymentService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.inventory.topic}")
    public void process(String message) {
        try {
            InventoryEvent inventoryEvent = objectMapper.readValue(message, InventoryEvent.class);
            log.info("Received inventory for payment processing: {}", inventoryEvent);

            if (inventoryEvent.getInventoryStatus() == InventoryStatus.INVENTORY_RESERVED) {
                paymentService.debit(inventoryEvent);
            }
        } catch (Exception e) {
            log.error("Error processing payment for the event {}: {}", message, e.getMessage());
        }
    }

}
