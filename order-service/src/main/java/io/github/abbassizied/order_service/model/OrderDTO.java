package io.github.abbassizied.order_service.model;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDTO {

    private Long id;

    @NotNull
    private OrderStatus status;

    @NotNull
    private Long customer; // Keep as Long for customer ID

    private List<OrderItemDTO> orderItems;
}