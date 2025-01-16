package com.querino.saga.inventory.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.querino.saga.inventory.domain.InventoryEvent;
import com.querino.saga.inventory.domain.exception.KafkaPublishException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class InventoryEventProducer {

    @Value("${kafka.topic.inventory}")
    private String inventoryTopic;
    private static final String INVENTORY_ERROR = "inventory-error";
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Logger logger = LoggerFactory.getLogger(InventoryEventProducer.class);
    private final ObjectMapper objectMapper;

    @Autowired
    public InventoryEventProducer(KafkaTemplate<String, String> kafkaTemplate,
                                 ObjectMapper objectMapper ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void send(InventoryEvent inventoryEvent) {
        try {
            kafkaTemplate
                    .send(inventoryTopic, inventoryEvent.toString(), objectMapper.writeValueAsString(inventoryEvent))
                    .thenAccept(item -> logger.info("Inventory event sent {} successfully to topic: {}", item, inventoryTopic))
                    .exceptionally(ex ->
                    {
                        throw new RuntimeException(ex);
                    });
        } catch (Exception e) {
            logger.error("Error while sending order to Kafka: {}", e.getMessage());
            throw new KafkaPublishException("Failed to publish order to Kafka", e);
        }
    }

    public void sendError(InventoryEvent inventoryEvent) {
        try {
            kafkaTemplate
                    .send(INVENTORY_ERROR, inventoryEvent.toString(), inventoryEvent.getTransactionId())
                    .thenAccept(item -> logger.info("Inventory error event {} sent successfully to topic: {}", item, inventoryTopic))
                    .exceptionally(ex ->
                    {
                        throw new RuntimeException(ex);
                    });
        } catch (Exception e) {
            logger.error("Error while sending event to Kafka: {}", e.getMessage());
            throw new KafkaPublishException("Failed to publish order to Kafka", e);
        }
    }
}
