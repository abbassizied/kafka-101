package io.github.abbassizied.order_service.service;

import io.github.abbassizied.order_service.domain.Customer;
import io.github.abbassizied.order_service.domain.Order;
import io.github.abbassizied.order_service.domain.OrderItem;
import io.github.abbassizied.order_service.model.OrderDTO;
import io.github.abbassizied.order_service.model.OrderItemDTO;
import io.github.abbassizied.order_service.model.OrderStatus;
import io.github.abbassizied.order_service.repos.CustomerRepository;
import io.github.abbassizied.order_service.repos.OrderItemRepository;
import io.github.abbassizied.order_service.repos.OrderRepository;
import io.github.abbassizied.order_service.util.NotFoundException;
import io.github.abbassizied.order_service.util.ReferencedWarning;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
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
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO get(final Long id) {
        return orderRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(NotFoundException::new);
    }

    @Transactional
    public Long create(final OrderDTO orderDTO) {
        final Order order = new Order();
        mapToEntity(orderDTO, order);

        // Save order first to get ID
        Order savedOrder = orderRepository.save(order);

        // Save order items if provided
        if (orderDTO.getOrderItems() != null) {
            for (OrderItemDTO itemDTO : orderDTO.getOrderItems()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setQuantity(itemDTO.getQuantity());
                orderItem.setOrder(savedOrder);
                // Note: You'll need to fetch the Product entity here
                // You might need a ProductRepository or Feign client
                orderItemRepository.save(orderItem);
            }
        }

        return savedOrder.getId();
    }

    @Transactional
    public void update(final Long id, final OrderDTO orderDTO) {
        final Order order = orderRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(orderDTO, order);

        // Update order items if provided
        if (orderDTO.getOrderItems() != null) {
            // Clear existing items and add new ones
            order.getOrderItems().clear();

            for (OrderItemDTO itemDTO : orderDTO.getOrderItems()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setQuantity(itemDTO.getQuantity());
                orderItem.setOrder(order);
                // Note: You'll need to fetch the Product entity here
                order.getOrderItems().add(orderItem);
            }
        }

        orderRepository.save(order);
    }

    @Transactional
    public void delete(final Long id) {
        // First delete order items to avoid constraint violations
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(id);
        orderItemRepository.deleteAll(orderItems);

        // Then delete the order
        orderRepository.deleteById(id);
    }

    private OrderDTO mapToDTO(final Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setCustomer(order.getCustomer() == null ? null : order.getCustomer().getId());

        // Map order items
        if (order.getOrderItems() != null) {
            List<OrderItemDTO> itemDTOs = order.getOrderItems().stream()
                    .map(this::mapOrderItemToDTO)
                    .collect(Collectors.toList());
            orderDTO.setOrderItems(itemDTOs);
        }

        return orderDTO;
    }

    private OrderItemDTO mapOrderItemToDTO(final OrderItem orderItem) {
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setId(orderItem.getId());
        orderItemDTO.setQuantity(orderItem.getQuantity());
        orderItemDTO.setOrder(orderItem.getOrder().getId());
        orderItemDTO.setProduct(orderItem.getProduct() != null ? orderItem.getProduct().getId() : null);
        return orderItemDTO;
    }

    private Order mapToEntity(final OrderDTO orderDTO, final Order order) {
        order.setStatus(orderDTO.getStatus());

        final Customer customer = orderDTO.getCustomer() == null ? null
                : customerRepository.findById(orderDTO.getCustomer())
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

        // Check if order has any items
        final List<OrderItem> orderItems = orderItemRepository.findByOrderId(id);
        if (!orderItems.isEmpty()) {
            referencedWarning.setKey("order.orderItems.referenced");
            referencedWarning.addParam(orderItems.size());
            return referencedWarning;
        }

        return null;
    }

    // Additional helper methods
    public List<OrderItemDTO> getOrderItemsByOrderId(Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        return orderItems.stream()
                .map(this::mapOrderItemToDTO)
                .collect(Collectors.toList());
    }
}