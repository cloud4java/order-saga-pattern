package com.querino.saga.order.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.querino.saga.order.domain.OrderEvent;
import com.querino.saga.order.domain.exception.KafkaPublishException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderEventProducer {
    @Value("${kafka.topic.order}")
    private String orderTopic;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Logger logger = LoggerFactory.getLogger(OrderEventProducer.class);
    private final ObjectMapper objectMapper;
    @Autowired
    public OrderEventProducer(KafkaTemplate<String, String> kafkaTemplate,
    ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendOrder(OrderEvent orderEvent) {
        try {
            kafkaTemplate
                    .send(orderTopic, orderEvent.toString(), objectMapper.writeValueAsString(orderEvent))
                    .thenAccept(item -> logger.info("Order sent {} successfully to topic: {}", item, orderTopic))
                    .exceptionally(ex ->
                    {
                        throw new RuntimeException(ex);
                    });
        } catch (Exception e) {
            logger.error("Error while sending order to Kafka: {}", e.getMessage());
            throw new RuntimeException(new KafkaPublishException("Failed to publish order to Kafka", e));
        }
    }
}
