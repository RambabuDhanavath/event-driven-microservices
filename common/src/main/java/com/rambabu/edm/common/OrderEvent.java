package com.rambabu.edm.common;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Shared event payload published to Kafka when an order is created.
 *
 * Both order-service (producer) and notification-service (consumer)
 * depend on this class so the event schema stays consistent.
 */
public class OrderEvent implements Serializable {

    private String eventId;
    private String orderId;
    private String product;
    private int quantity;
    private double price;
    private LocalDateTime occurredAt;

    public OrderEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredAt = LocalDateTime.now();
    }

    public OrderEvent(String orderId, String product,
            int quantity, double price) {
        this();
        this.orderId = orderId;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getProduct() { return product; }
    public void setProduct(String product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    @Override
    public String toString() {
        return "OrderEvent{eventId='" + eventId + "', orderId='"
                + orderId + "', product='" + product + "', quantity="
                + quantity + ", price=" + price + "}";
    }
}
