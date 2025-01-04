package com.querino.saga.inventory.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryItemDTO {
    private Long id;
    private Long customerId;
    private String sku;
    private Integer quantity;
    private Double price;
}