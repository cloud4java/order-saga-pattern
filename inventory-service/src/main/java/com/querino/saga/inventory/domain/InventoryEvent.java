package com.querino.saga.inventory.domain;

import com.querino.saga.inventory.domain.model.InventoryItemDTO;
import com.querino.saga.inventory.domain.model.InventoryStatus;
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
    private List<InventoryItemDTO> inventoryItemDTO;
}
