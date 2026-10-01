package com.rambabu.edm.orderservice;

import com.rambabu.edm.common.OrderEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * REST API of the order-service.
 *
 * Accepts new orders and publishes an {@link OrderEvent} to Kafka so
 * downstream services (notifications, inventory, billing) can react
 * without direct coupling.
 *
 *   POST /api/orders      -> create an order, publish OrderCreated event
 *   GET  /api/orders/{id} -> fetch an order
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderEventProducer producer;

    // In-memory order store (replace with a database for production)
    private final Map<String, OrderEvent> orders = new ConcurrentHashMap<>();

    public OrderController(OrderEventProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public ResponseEntity<OrderEvent> createOrder(
            @RequestBody Map<String, Object> request) {
        String orderId = UUID.randomUUID().toString();
        OrderEvent event = new OrderEvent(
                orderId,
                String.valueOf(request.getOrDefault("product", "")),
                ((Number) request.getOrDefault("quantity", 1)).intValue(),
                ((Number) request.getOrDefault("price", 0.0)).doubleValue());

        orders.put(orderId, event);
        producer.publishOrderCreated(event);

        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderEvent> getOrder(@PathVariable String id) {
        OrderEvent event = orders.get(id);
        return event == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(event);
    }
}
