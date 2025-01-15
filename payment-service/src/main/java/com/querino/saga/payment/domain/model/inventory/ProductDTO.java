package com.querino.saga.payment.domain.model.inventory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductDTO {
    @JsonIgnore
    private Long id;
    private Long customerId;
    private String sku;
    private Integer quantity;
    private Double price;
}