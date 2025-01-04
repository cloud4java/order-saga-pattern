package com.querino.saga.inventory.controller;

import com.querino.saga.inventory.domain.model.InventoryItemDTO;
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
    public ResponseEntity<InventoryItemDTO> addItem(@Valid @RequestBody InventoryItemDTO inventoryItemDTO) {
        InventoryItemDTO createdOrder = inventoryService.createItem(inventoryItemDTO);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<InventoryItemDTO>> getAllOrders() {
        List<InventoryItemDTO> orders = inventoryService.getAllItems();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryItemDTO> getOrderById(@PathVariable Long id) {
        InventoryItemDTO order = inventoryService.getItemById(id);
        return ResponseEntity.ok(order);
    }
}
