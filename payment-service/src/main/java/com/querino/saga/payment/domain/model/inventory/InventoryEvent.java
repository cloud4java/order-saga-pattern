package com.querino.saga.payment.domain.model.inventory;

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
    String transactionId;
}
