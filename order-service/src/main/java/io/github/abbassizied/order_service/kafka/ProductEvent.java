// src/main/java/io/github/abbassizied/order_service/kafka/ProductEvent.java
package io.github.abbassizied.order_service.kafka;

import lombok.Data;

@Data
public class ProductEvent {
    private String eventType; // CREATED, UPDATED, DELETED
    private Long productId;
    private String name;
    private Integer quantity;
    private Double price;
}
