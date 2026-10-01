package com.rambabu.edm.notificationservice;

import com.rambabu.edm.common.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumes order events from Kafka.
 *
 * Reacts to {@code OrderCreated} events published by order-service by
 * sending an order confirmation (email/SMS/push — simulated with a log
 * line here). New behavior can be added by deploying more consumers;
 * order-service never changes.
 */
@Component
public class OrderEventConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(OrderEventConsumer.class);

    /**
     * Handles an order-created event.
     *
     * @param event the consumed order event
     */
    @KafkaListener(topics = "order-events",
            groupId = "notification-service")
    public void handleOrderCreated(OrderEvent event) {
        log.info("Received order event: {}", event);
        sendConfirmation(event);
    }

    /**
     * Sends the order confirmation to the customer.
     * TODO: integrate a real email/SMS/push provider.
     */
    private void sendConfirmation(OrderEvent event) {
        String message = String.format(
                "Thank you! Your order %s for %d x %s ($%.2f) "
                        + "has been confirmed.",
                event.getOrderId(), event.getQuantity(),
                event.getProduct(), event.getPrice());
        log.info("Notification sent: {}", message);
    }
}
