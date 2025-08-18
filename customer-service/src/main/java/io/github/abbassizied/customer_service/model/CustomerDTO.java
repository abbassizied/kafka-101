package io.github.abbassizied.customer_service.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CustomerDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String name;

    @NotNull
    @Size(max = 255)
    @CustomerEmailUnique
    private String email;

    @NotNull
    @Size(max = 255)
    @CustomerPhoneUnique
    private String phone;

    @Size(max = 255)
    private String shippingAddress;

    @Size(max = 255)
    private String billingAddress;

}
