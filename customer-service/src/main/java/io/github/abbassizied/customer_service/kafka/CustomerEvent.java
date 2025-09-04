package io.github.abbassizied.customer_service.kafka;

import io.github.abbassizied.customer_service.domain.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEvent {
    private String eventType; // CREATED, UPDATED, DELETED
    private Long customerId;
    private String name;
    private String email;
    private String phone;
    private Address shippingAddress;
    private Address billingAddress;
}

