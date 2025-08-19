package io.github.abbassizied.order_service.repos;

import io.github.abbassizied.order_service.domain.OrderItem;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Find by order ID
    List<OrderItem> findByOrderId(Long orderId);

    // Check if product exists in any order item
    boolean existsByProductId(Long productId);

    // If you need to find by Order entity (not just ID)
    boolean existsByOrderId(Long orderId);

    Long countByOrderId(Long orderId);
}