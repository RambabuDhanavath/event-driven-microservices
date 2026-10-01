# Event-Driven Microservices ⚡

> 🎓 **Academic/personal project** — a master's-era-style software engineering project demonstrating event-driven architecture with Spring Boot and Apache Kafka.

## Overview

A showcase of **event-driven microservices**: independent Spring Boot services that communicate asynchronously through **Apache Kafka** topics instead of direct REST calls. Placing an order publishes an event; downstream services react to it — the same pattern used in modern e-commerce and fintech systems.

## 🏗️ Architecture

```
                    ┌─────────────────────────────────────────┐
                    │            Apache Kafka                 │
                    │                                         │
                    │   Topic: order-events                   │
                    │   Topic: notification-events            │
                    └─────────────────────────────────────────┘
                          ▲                       │
                          │ publish               │ subscribe
                          │                       ▼
┌──────────────────┐    ┌─────────┐    ┌──────────────────────┐
│  order-service   │───▶│  Kafka  │───▶│ notification-service │
│  (REST API)      │    │ Broker  │    │ (event consumer)     │
│                  │    └─────────┘    │                      │
│ POST /api/orders │                   │ sends confirmations  │
│ GET  /api/orders │                   │ on order events      │
└──────────────────┘                   └──────────────────────┘
```

### Event flow

1. Client calls `POST /api/orders` on **order-service**.
2. `order-service` validates the order and publishes an `OrderCreated` event to the `order-events` Kafka topic.
3. **notification-service** consumes the event and sends an order confirmation (email/SMS/push — simulated here).
4. Services stay decoupled: add new consumers (analytics, inventory, billing) without touching order-service.

## 🧩 Services

| Service | Responsibility | Key classes |
|---|---|---|
| `order-service` | REST API for orders; publishes order events | `OrderController`, `OrderEventProducer` |
| `notification-service` | Consumes order events; sends notifications | `OrderEventConsumer` |
| `common` | Shared event model | `OrderEvent` |

## 🛠️ Tech Stack

- **Java 17**, **Spring Boot 3**
- **Apache Kafka** (Spring Kafka) — event backbone
- **Docker & Docker Compose** — one-command local environment
- **REST APIs** — service entry points
- **Maven** — builds

## 📁 Project Structure

```
event-driven-microservices/
├── common/
│   └── src/main/java/com/rambabu/edm/common/
│       └── OrderEvent.java            # Shared event payload
├── order-service/
│   └── src/main/java/com/rambabu/edm/orderservice/
│       ├── OrderController.java       # POST/GET /api/orders
│       └── OrderEventProducer.java    # Publishes to Kafka
├── notification-service/
│   └── src/main/java/com/rambabu/edm/notificationservice/
│       └── OrderEventConsumer.java    # @KafkaListener consumer
├── docker-compose.yml                 # Kafka, Zookeeper, services
└── README.md
```

## 🚀 How to Run (docker-compose)

### Prerequisites

- Docker and Docker Compose
- Java 17 + Maven (to build the jars)

### Steps

1. Build each service:

   ```bash
   cd order-service && mvn clean package && cd ..
   cd notification-service && mvn clean package && cd ..
   ```

2. Start the whole platform (Kafka, Zookeeper, both services):

   ```bash
   docker-compose up --build
   ```

3. Place an order:

   ```bash
   curl -X POST http://localhost:8081/api/orders \
     -H "Content-Type: application/json" \
     -d '{"product": "Book", "quantity": 2, "price": 19.99}'
   ```

4. Watch the `notification-service` logs — it consumes the `OrderCreated` event and logs the confirmation.

### Ports

| Service | Port |
|---|---|
| order-service | 8081 |
| notification-service | 8082 |
| Kafka | 9092 |
| Zookeeper | 2181 |

## 🗺️ Roadmap

- [ ] Inventory service consuming order events
- [ ] Payment service with saga pattern
- [ ] Schema Registry (Avro) for event evolution
- [ ] Dead-letter topics and retry policies
- [ ] Observability: distributed tracing with Micrometer
- [ ] Kubernetes manifests

## 📄 License

MIT
