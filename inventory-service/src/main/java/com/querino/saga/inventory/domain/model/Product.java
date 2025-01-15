package com.querino.saga.inventory.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long customerId;
    private String sku;
    private Integer quantity;
    private Double price;

    public ProductDTO toDTO() {
        return new ProductDTO(id,  customerId, sku, quantity, price);
    }
    public Product fromDTO(ProductDTO productDTO) {
        this.id = productDTO.getId();
        this.customerId = productDTO.getCustomerId();
        this.sku = productDTO.getSku();
        this.quantity = productDTO.getQuantity();
        this.price = productDTO.getPrice();
        return this;
    }
}