package io.github.abbassizied.order_service.repos;

import io.github.abbassizied.order_service.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepository extends JpaRepository<Product, Long> {
}
