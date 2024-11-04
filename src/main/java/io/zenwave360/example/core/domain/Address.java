package io.zenwave360.example.core.domain;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/** */
@lombok.Getter
@lombok.Setter
// @Embeddable // json embedded
public class Address implements Serializable {

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(max = 254)
    @Column(name = "street", nullable = false, length = 254)
    private String street;

    @NotNull
    @Size(max = 254)
    @Column(name = "city", nullable = false, length = 254)
    private String city;

}
