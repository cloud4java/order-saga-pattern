package com.querino.saga.inventory.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long customerId;
    private String sku;
    private Integer quantity;
    private Double price;

    public InventoryItemDTO toDTO() {
        return new InventoryItemDTO(id,  customerId, sku, quantity, price);
    }
    public InventoryItem fromDTO(InventoryItemDTO inventoryItemDTO) {
        this.id = inventoryItemDTO.getId();
        this.customerId = inventoryItemDTO.getCustomerId();
        this.sku = inventoryItemDTO.getSku();
        this.quantity = inventoryItemDTO.getQuantity();
        this.price = inventoryItemDTO.getPrice();
        return this;
    }
}