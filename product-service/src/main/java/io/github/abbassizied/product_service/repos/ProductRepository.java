package io.github.abbassizied.product_service.repos;

import io.github.abbassizied.product_service.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepository extends JpaRepository<Product, Long> {
}
