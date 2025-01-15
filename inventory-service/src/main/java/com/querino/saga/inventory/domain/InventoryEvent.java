package com.querino.saga.inventory.domain;

import com.querino.saga.inventory.domain.model.InventoryStatus;
import com.querino.saga.inventory.domain.model.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InventoryEvent {
    private Long customerId;
    private InventoryStatus inventoryStatus;
    private List<ProductDTO> productDTO;
    private String transactionId;
}
