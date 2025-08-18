package io.github.abbassizied.order_service.service;

import io.github.abbassizied.order_service.domain.Order;
import io.github.abbassizied.order_service.domain.OrderItem;
import io.github.abbassizied.order_service.domain.Product;
import io.github.abbassizied.order_service.model.OrderItemDTO;
import io.github.abbassizied.order_service.repos.OrderItemRepository;
import io.github.abbassizied.order_service.repos.OrderRepository;
import io.github.abbassizied.order_service.repos.ProductRepository;
import io.github.abbassizied.order_service.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderItemService(final OrderItemRepository orderItemRepository,
            final OrderRepository orderRepository, final ProductRepository productRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public List<OrderItemDTO> findAll() {
        final List<OrderItem> orderItems = orderItemRepository.findAll(Sort.by("id"));
        return orderItems.stream()
                .map(orderItem -> mapToDTO(orderItem, new OrderItemDTO()))
                .toList();
    }

    public OrderItemDTO get(final Long id) {
        return orderItemRepository.findById(id)
                .map(orderItem -> mapToDTO(orderItem, new OrderItemDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final OrderItemDTO orderItemDTO) {
        final OrderItem orderItem = new OrderItem();
        mapToEntity(orderItemDTO, orderItem);
        return orderItemRepository.save(orderItem).getId();
    }

    public void update(final Long id, final OrderItemDTO orderItemDTO) {
        final OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(orderItemDTO, orderItem);
        orderItemRepository.save(orderItem);
    }

    public void delete(final Long id) {
        orderItemRepository.deleteById(id);
    }

    private OrderItemDTO mapToDTO(final OrderItem orderItem, final OrderItemDTO orderItemDTO) {
        orderItemDTO.setId(orderItem.getId());
        orderItemDTO.setQuantity(orderItem.getQuantity());
        orderItemDTO.setOrders(orderItem.getOrders() == null ? null : orderItem.getOrders().getId());
        orderItemDTO.setProduct(orderItem.getProduct() == null ? null : orderItem.getProduct().getId());
        return orderItemDTO;
    }

    private OrderItem mapToEntity(final OrderItemDTO orderItemDTO, final OrderItem orderItem) {
        orderItem.setQuantity(orderItemDTO.getQuantity());
        final Order orders = orderItemDTO.getOrders() == null ? null : orderRepository.findById(orderItemDTO.getOrders())
                .orElseThrow(() -> new NotFoundException("orders not found"));
        orderItem.setOrders(orders);
        final Product product = orderItemDTO.getProduct() == null ? null : productRepository.findById(orderItemDTO.getProduct())
                .orElseThrow(() -> new NotFoundException("product not found"));
        orderItem.setProduct(product);
        return orderItem;
    }

    public boolean productExists(final Long id) {
        return orderItemRepository.existsByProductId(id);
    }

}
