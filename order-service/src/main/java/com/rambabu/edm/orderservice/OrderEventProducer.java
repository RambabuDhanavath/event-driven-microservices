package com.rambabu.edm.orderservice;

import com.rambabu.edm.common.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Publishes order events to Kafka.
 *
 * Decouples order-service from its consumers: notification-service
 * (and any future inventory/billing services) react to the
 * {@code order-events} topic instead of being called directly.
 */
@Component
public class OrderEventProducer {

    private static final Logger log =
            LoggerFactory.getLogger(OrderEventProducer.class);
    private static final String TOPIC = "order-events";

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public OrderEventProducer(
            KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /** Publishes an OrderCreated event to the order-events topic. */
    public void publishOrderCreated(OrderEvent event) {
        kafkaTemplate.send(TOPIC, event.getOrderId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish {}", event, ex);
                    } else {
                        log.info("Published {} to {}", event, TOPIC);
                    }
                });
    }
}
