package com.querino.saga.payment.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryItemDTO {
    private Long id;
    private String customerName;
    private Integer quantity;
    private Double price;
}