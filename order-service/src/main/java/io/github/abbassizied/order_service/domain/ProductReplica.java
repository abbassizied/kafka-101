// src/main/java/io/github/abbassizied/order_service/domain/ProductReplica.java
package io.github.abbassizied.order_service.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@Entity
@Table(name = "ProductsReplica")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class ProductReplica {

    @Id
    @Column(nullable = false, updatable = false)
    // NOTE: NO @GeneratedValue – we set the ID from the event
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double price;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;
}
