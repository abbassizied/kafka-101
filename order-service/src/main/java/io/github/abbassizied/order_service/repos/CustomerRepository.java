package io.github.abbassizied.order_service.repos;

import io.github.abbassizied.order_service.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
