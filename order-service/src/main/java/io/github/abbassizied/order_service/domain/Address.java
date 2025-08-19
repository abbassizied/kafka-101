package io.github.abbassizied.order_service.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Address {
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    public boolean isValid() {
        return street != null && city != null && postalCode != null && country != null;
    }

    public String getFormattedAddress() {
        if (!isValid())
            return "Invalid address";
        return String.format("%s, %s, %s %s, %s", street, city, state, postalCode, country);
    }
}