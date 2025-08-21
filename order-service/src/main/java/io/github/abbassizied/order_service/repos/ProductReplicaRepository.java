// src/main/java/io/github/abbassizied/order_service/repos/ProductReplicaRepository.java
package io.github.abbassizied.order_service.repos;

import io.github.abbassizied.order_service.domain.ProductReplica;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductReplicaRepository extends JpaRepository<ProductReplica, Long> {}
