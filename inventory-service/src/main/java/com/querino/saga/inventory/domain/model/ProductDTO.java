package com.querino.saga.inventory.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    @JsonIgnore
    private Long id;
    private Long customerId;
    private String sku;
    private Integer quantity;
    private Double price;
}