package com.querino.saga.payment.kafka;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querino.saga.payment.domain.PaymentEvent;
import com.querino.saga.payment.domain.exception.KafkaPublishException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventProducer {

    private static final String TOPIC_NAME = "order-topic";
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Logger logger = LoggerFactory.getLogger(PaymentEventProducer.class);
    private final ObjectMapper objectMapper;

    @Autowired
    public PaymentEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendOrder(PaymentEvent paymentEvent) {
        try {
            kafkaTemplate
                    .send(TOPIC_NAME, String.valueOf(paymentEvent.getOrderId()), objectMapper.writeValueAsString(paymentEvent))
                    .thenAccept(item -> logger.info("Order sent {} successfully to topic: {}", item, TOPIC_NAME))
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
