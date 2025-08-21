package io.github.abbassizied.order_service.service;

import io.github.abbassizied.order_service.domain.CustomerReplica;
import io.github.abbassizied.order_service.domain.Order;
import io.github.abbassizied.order_service.domain.OrderItem;
import io.github.abbassizied.order_service.domain.ProductReplica;
import io.github.abbassizied.order_service.model.OrderDTO;
import io.github.abbassizied.order_service.model.OrderItemDTO;
import io.github.abbassizied.order_service.model.OrderStatus;
import io.github.abbassizied.order_service.repos.CustomerReplicaRepository;
import io.github.abbassizied.order_service.repos.OrderItemRepository;
import io.github.abbassizied.order_service.repos.OrderRepository;
import io.github.abbassizied.order_service.repos.ProductReplicaRepository;
import io.github.abbassizied.order_service.util.NotFoundException;
import io.github.abbassizied.order_service.util.ReferencedWarning;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerReplicaRepository customerRepository;
    private final ProductReplicaRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(final OrderRepository orderRepository,
                        final CustomerReplicaRepository customerRepository,
                        final ProductReplicaRepository productRepository,
                        final OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> findAll() {
        final List<Order> orders = orderRepository.findAll(Sort.by("id"));
        return orders.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderDTO get(final Long id) {
        return orderRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final OrderDTO orderDTO) {
        final Order order = new Order();
        mapToEntity(orderDTO, order);

        // 1) Save order first to get an ID
        Order savedOrder = orderRepository.save(order);

        // 2) Persist order items (if any)
        if (orderDTO.getOrderItems() != null) {
            for (OrderItemDTO itemDTO : orderDTO.getOrderItems()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setQuantity(itemDTO.getQuantity());
                orderItem.setOrder(savedOrder);

                // Resolve product from local replica (required)
                ProductReplica product = itemDTO.getProduct() == null ? null :
                        productRepository.findById(itemDTO.getProduct())
                                .orElseThrow(() -> new NotFoundException("product not found"));
                orderItem.setProduct(product);

                orderItemRepository.save(orderItem);
            }
        }

        return savedOrder.getId();
    }

    public void update(final Long id, final OrderDTO orderDTO) {
        final Order order = orderRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        // Update top-level fields (status, customer)
        mapToEntity(orderDTO, order);
        Order savedOrder = orderRepository.save(order);

        // Rebuild items if provided: delete existing then add fresh
        if (orderDTO.getOrderItems() != null) {
            List<OrderItem> existing = orderItemRepository.findByOrderId(id);
            if (!existing.isEmpty()) {
                orderItemRepository.deleteAll(existing);
            }

            for (OrderItemDTO itemDTO : orderDTO.getOrderItems()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setQuantity(itemDTO.getQuantity());
                orderItem.setOrder(savedOrder);

                ProductReplica product = itemDTO.getProduct() == null ? null :
                        productRepository.findById(itemDTO.getProduct())
                                .orElseThrow(() -> new NotFoundException("product not found"));
                orderItem.setProduct(product);

                orderItemRepository.save(orderItem);
            }
        }
    }

    public void delete(final Long id) {
        // Delete items first to avoid FK constraint violations
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(id);
        if (!orderItems.isEmpty()) {
            orderItemRepository.deleteAll(orderItems);
        }
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

        // Resolve replicated customer
        final CustomerReplica customer = orderDTO.getCustomer() == null ? null
                : customerRepository.findById(orderDTO.getCustomer())
                .orElseThrow(() -> new NotFoundException("customer not found"));
        order.setCustomer(customer);

        return order;
    }

    @Transactional(readOnly = true)
    public boolean statusExists(final OrderStatus status) {
        return orderRepository.existsByStatus(status);
    }

    @Transactional(readOnly = true)
    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Order order = orderRepository.findById(id).orElseThrow(NotFoundException::new);

        // Check if order has any items
        final List<OrderItem> orderItems = orderItemRepository.findByOrderId(id);
        if (!orderItems.isEmpty()) {
            referencedWarning.setKey("order.orderItems.referenced");
            referencedWarning.addParam(orderItems.size());
            return referencedWarning;
        }

        return null;
    }

    @Transactional(readOnly = true)
    public List<OrderItemDTO> getOrderItemsByOrderId(Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        return orderItems.stream()
                .map(this::mapOrderItemToDTO)
                .collect(Collectors.toList());
    }
}
