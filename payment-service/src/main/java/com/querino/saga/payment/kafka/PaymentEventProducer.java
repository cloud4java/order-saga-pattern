package com.querino.saga.payment.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.querino.saga.payment.domain.model.PaymentEvent;
import com.querino.saga.payment.domain.model.exception.KafkaPublishException;
import com.querino.saga.payment.domain.model.inventory.InventoryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventProducer {

    private static final String TOPIC_NAME = "order-topic";
    private static final String PAYMENT_ERROR = "payment-error";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Logger logger = LoggerFactory.getLogger(PaymentEventProducer.class);
    private final ObjectMapper objectMapper;

    @Autowired
    public PaymentEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendError(InventoryEvent inventoryEvent) {
        try {
            kafkaTemplate
                    .send(PAYMENT_ERROR, inventoryEvent.toString(), objectMapper.writeValueAsString(inventoryEvent))
                    .thenAccept(result -> logger.info("Inventory sent {} successfully to topic: {}", result, "inventory-topic"))
                    .exceptionally(ex ->
                    {
                        throw new RuntimeException(ex);
                    });
        } catch (Exception e) {
            logger.error("Error while error event to Kafka: {}", e.getMessage());
            throw new KafkaPublishException("Failed to publish payment event to Kafka", e);
        }
    }
}
