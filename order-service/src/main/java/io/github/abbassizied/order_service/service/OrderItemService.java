package io.github.abbassizied.order_service.service;

import io.github.abbassizied.order_service.domain.Order;
import io.github.abbassizied.order_service.domain.OrderItem;
import io.github.abbassizied.order_service.domain.ProductReplica;
import io.github.abbassizied.order_service.model.OrderItemDTO;
import io.github.abbassizied.order_service.repos.OrderItemRepository;
import io.github.abbassizied.order_service.repos.OrderRepository;
import io.github.abbassizied.order_service.repos.ProductReplicaRepository;
import io.github.abbassizied.order_service.util.NotFoundException;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductReplicaRepository productRepository;

    public OrderItemService(final OrderItemRepository orderItemRepository,
                            final OrderRepository orderRepository,
                            final ProductReplicaRepository productRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public List<OrderItemDTO> findAll() {
        final List<OrderItem> orderItems = orderItemRepository.findAll(Sort.by("id"));
        return orderItems.stream()
                .map(this::mapToDTO)
                .toList();
    }

    public OrderItemDTO get(final Long id) {
        return orderItemRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new NotFoundException("OrderItem with id " + id + " not found"));
    }

    public Long create(final OrderItemDTO orderItemDTO) {
        final OrderItem orderItem = new OrderItem();
        mapToEntity(orderItemDTO, orderItem);
        return orderItemRepository.save(orderItem).getId();
    }

    public void update(final Long id, final OrderItemDTO orderItemDTO) {
        final OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("OrderItem with id " + id + " not found"));
        mapToEntity(orderItemDTO, orderItem);
        orderItemRepository.save(orderItem);
    }

    public void delete(final Long id) {
        if (!orderItemRepository.existsById(id)) {
            throw new NotFoundException("OrderItem with id " + id + " not found");
        }
        orderItemRepository.deleteById(id);
    }

    private OrderItemDTO mapToDTO(final OrderItem orderItem) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(orderItem.getId());
        dto.setQuantity(orderItem.getQuantity());
        dto.setOrder(orderItem.getOrder() != null ? orderItem.getOrder().getId() : null);
        dto.setProduct(orderItem.getProduct() != null ? orderItem.getProduct().getId() : null);
        return dto;
    }

    private OrderItem mapToEntity(final OrderItemDTO dto, final OrderItem entity) {
        entity.setQuantity(dto.getQuantity());

        final Order order = dto.getOrder() == null ? null :
                orderRepository.findById(dto.getOrder())
                        .orElseThrow(() -> new NotFoundException("Order with id " + dto.getOrder() + " not found"));
        entity.setOrder(order);

        final ProductReplica product = dto.getProduct() == null ? null :
                productRepository.findById(dto.getProduct())
                        .orElseThrow(() -> new NotFoundException("Product with id " + dto.getProduct() + " not found"));
        entity.setProduct(product);

        return entity;
    }

    // === Utility methods ===
    public boolean productExists(final Long productId) {
        return orderItemRepository.existsByProductId(productId);
    }

    public List<OrderItemDTO> findByOrderId(final Long orderId) {
        return orderItemRepository.findByOrderId(orderId).stream()
                .map(this::mapToDTO)
                .toList();
    }

    public boolean existsByOrderId(final Long orderId) {
        return orderItemRepository.existsByOrderId(orderId);
    }

    public Long countByOrderId(final Long orderId) {
        return orderItemRepository.countByOrderId(orderId);
    }
}
