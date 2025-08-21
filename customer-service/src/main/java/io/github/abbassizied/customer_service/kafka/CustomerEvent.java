package io.github.abbassizied.customer_service.kafka;

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
    private String shippingAddress; // formatted address
    private String billingAddress;  // formatted address
}

