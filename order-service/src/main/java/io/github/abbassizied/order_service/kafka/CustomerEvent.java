// src/main/java/io/github/abbassizied/order_service/kafka/CustomerEvent.java
package io.github.abbassizied.order_service.kafka;

import lombok.Data;

@Data
public class CustomerEvent {
    private String eventType; // CREATED, UPDATED, DELETED
    private Long customerId;
    private String name;
    private String email;
    private String phone;
    private String shippingAddress;
    private String billingAddress;
}
