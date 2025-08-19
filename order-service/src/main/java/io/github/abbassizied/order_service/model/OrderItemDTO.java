package io.github.abbassizied.order_service.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDTO {

    private Long id;

    @NotNull
    private Integer quantity;

    @NotNull
    private Long order;

    @NotNull
    private Long product;
}