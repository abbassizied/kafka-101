package io.github.abbassizied.product_service.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductEvent {
    private String eventType; // CREATED, UPDATED, DELETED
    private Long productId;
    private String name;
    private Integer quantity;
    private Double price;
}
