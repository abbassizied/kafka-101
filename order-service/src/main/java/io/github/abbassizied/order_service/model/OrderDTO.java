package io.github.abbassizied.order_service.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OrderDTO {

    private Long id;

    @NotNull
    @OrderStatusUnique
    private OrderStatus status;

    @NotNull
    private Long customer;

}
