package io.github.abbassizied.order_service.repos;

import io.github.abbassizied.order_service.domain.Order;
import io.github.abbassizied.order_service.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    OrderItem findFirstByOrders(Order order);

    boolean existsByProductId(Long id);

}
