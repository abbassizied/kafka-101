package io.github.abbassizied.order_service.service;

import io.github.abbassizied.order_service.domain.Customer;
import io.github.abbassizied.order_service.domain.Order;
import io.github.abbassizied.order_service.domain.OrderItem;
import io.github.abbassizied.order_service.model.OrderDTO;
import io.github.abbassizied.order_service.model.OrderStatus;
import io.github.abbassizied.order_service.repos.CustomerRepository;
import io.github.abbassizied.order_service.repos.OrderItemRepository;
import io.github.abbassizied.order_service.repos.OrderRepository;
import io.github.abbassizied.order_service.util.NotFoundException;
import io.github.abbassizied.order_service.util.ReferencedWarning;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(final OrderRepository orderRepository,
            final CustomerRepository customerRepository,
            final OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public List<OrderDTO> findAll() {
        final List<Order> orders = orderRepository.findAll(Sort.by("id"));
        return orders.stream()
                .map(order -> mapToDTO(order, new OrderDTO()))
                .toList();
    }

    public OrderDTO get(final Long id) {
        return orderRepository.findById(id)
                .map(order -> mapToDTO(order, new OrderDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final OrderDTO orderDTO) {
        final Order order = new Order();
        mapToEntity(orderDTO, order);
        return orderRepository.save(order).getId();
    }

    public void update(final Long id, final OrderDTO orderDTO) {
        final Order order = orderRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(orderDTO, order);
        orderRepository.save(order);
    }

    public void delete(final Long id) {
        orderRepository.deleteById(id);
    }

    private OrderDTO mapToDTO(final Order order, final OrderDTO orderDTO) {
        orderDTO.setId(order.getId());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setCustomer(order.getCustomer() == null ? null : order.getCustomer().getId());
        return orderDTO;
    }

    private Order mapToEntity(final OrderDTO orderDTO, final Order order) {
        order.setStatus(orderDTO.getStatus());
        final Customer customer = orderDTO.getCustomer() == null ? null : customerRepository.findById(orderDTO.getCustomer())
                .orElseThrow(() -> new NotFoundException("customer not found"));
        order.setCustomer(customer);
        return order;
    }

    public boolean statusExists(final OrderStatus status) {
        return orderRepository.existsByStatus(status);
    }

    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Order order = orderRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final OrderItem ordersOrderItem = orderItemRepository.findFirstByOrders(order);
        if (ordersOrderItem != null) {
            referencedWarning.setKey("order.orderItem.orders.referenced");
            referencedWarning.addParam(ordersOrderItem.getId());
            return referencedWarning;
        }
        return null;
    }

}
