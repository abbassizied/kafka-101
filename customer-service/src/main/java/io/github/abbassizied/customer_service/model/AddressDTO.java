package io.github.abbassizied.customer_service.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDTO {
    @Size(max = 255)
    private String street;
    @Size(max = 255)
    private String city;
    @Size(max = 255)
    private String state;
    @Size(max = 255)
    private String postalCode;
    @Size(max = 255)
    private String country;
}
