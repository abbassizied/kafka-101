// src/main/java/io/github/abbassizied/order_service/domain/CustomerReplica.java
package io.github.abbassizied.order_service.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@Entity
@Table(name = "Customers")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class CustomerReplica {

    @Id
    @Column(nullable = false, updatable = false)
    // NOTE: NO @GeneratedValue – we set the ID from the event
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phone;

    @Embedded
    @AttributeOverride(name = "street", column = @Column(name = "shipping_street"))
    @AttributeOverride(name = "city", column = @Column(name = "shipping_city"))
    @AttributeOverride(name = "state", column = @Column(name = "shipping_state"))
    @AttributeOverride(name = "postalCode", column = @Column(name = "shipping_postal_code"))
    @AttributeOverride(name = "country", column = @Column(name = "shipping_country"))
    private Address shippingAddress;

    @Embedded
    @AttributeOverride(name = "street", column = @Column(name = "billing_street"))
    @AttributeOverride(name = "city", column = @Column(name = "billing_city"))
    @AttributeOverride(name = "state", column = @Column(name = "billing_state"))
    @AttributeOverride(name = "postalCode", column = @Column(name = "billing_postal_code"))
    @AttributeOverride(name = "country", column = @Column(name = "billing_country"))
    private Address billingAddress;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;
}
