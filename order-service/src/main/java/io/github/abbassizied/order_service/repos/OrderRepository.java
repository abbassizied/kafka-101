package io.github.abbassizied.order_service.repos;

import io.github.abbassizied.order_service.domain.Order;
import io.github.abbassizied.order_service.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderRepository extends JpaRepository<Order, Long> {

    boolean existsByStatus(OrderStatus status);

}
