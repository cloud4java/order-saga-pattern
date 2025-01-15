package com.querino.saga.inventory.domain;

import com.querino.saga.inventory.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);
}