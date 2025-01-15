package com.querino.saga.inventory.controller;

import com.querino.saga.inventory.domain.model.ProductDTO;
import com.querino.saga.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<ProductDTO> addItem(@Valid @RequestBody ProductDTO productDTO) {
        ProductDTO createdOrder = inventoryService.createItem(productDTO);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllItems() {
        List<ProductDTO> orders = inventoryService.getAllItems();
        return ResponseEntity.ok(orders);
    }
}
