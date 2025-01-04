package com.querino.saga.payment.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InventoryEvent {
    private Long customerId;
    private InventoryStatus inventoryStatus;
    private InventoryItemDTO inventoryItemDTO;
}
