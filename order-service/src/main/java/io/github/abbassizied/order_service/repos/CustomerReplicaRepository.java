// src/main/java/io/github/abbassizied/order_service/repos/CustomerReplicaRepository.java
package io.github.abbassizied.order_service.repos;

import io.github.abbassizied.order_service.domain.CustomerReplica;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerReplicaRepository extends JpaRepository<CustomerReplica, Long> {
}
