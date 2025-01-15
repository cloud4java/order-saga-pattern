package com.querino.saga.inventory.service;

import com.querino.saga.inventory.domain.InventoryEvent;
import com.querino.saga.inventory.domain.InventoryRepository;
import com.querino.saga.inventory.domain.exception.InsufficientStockException;
import com.querino.saga.inventory.domain.exception.ResourceNotFoundException;
import com.querino.saga.inventory.domain.model.InventoryStatus;
import com.querino.saga.inventory.domain.model.Product;
import com.querino.saga.inventory.domain.model.ProductDTO;
import com.querino.saga.inventory.domain.model.order.OrderEvent;
import com.querino.saga.inventory.domain.model.order.OrderItemDTO;
import com.querino.saga.inventory.kafka.InventoryEventProducer;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryEventProducer kafkaProducer;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository, InventoryEventProducer kafkaProducer) {
        this.inventoryRepository = inventoryRepository;
        this.kafkaProducer = kafkaProducer;
    }

    public ProductDTO createItem(@Valid ProductDTO productDTO) {
        Optional<Product> product = inventoryRepository.findBySku(productDTO.getSku());
        if(product.isPresent()) {
            Product entity = product.get();
            entity.setQuantity(entity.getQuantity() + productDTO.getQuantity());
            return inventoryRepository.save(entity).toDTO();
        } else {
            return inventoryRepository.save(new Product().fromDTO(productDTO)).toDTO();
        }
    }

    public List<ProductDTO> getAllItems() {
        return inventoryRepository.findAll().stream().map(
                entity -> entity.toDTO()
        ).toList();
    }

    public ProductDTO getItemById(Long id) {
        return inventoryRepository.findById(id).map(
                entity -> entity.toDTO()
        ).orElseThrow();
    }

    public void updateQuantity(OrderEvent orderEvent) {
        orderEvent.getOrderDTO().getOrderItemDTO().forEach(
                item -> {
                    try {
                        Product itemDb = inventoryRepository.findBySku(item.getSku()).orElseThrow(
                                () -> new ResourceNotFoundException("Item not found by sku: {}" + item.getSku())
                        );
                        validateStockQuantity(item, itemDb);
                        itemDb.setQuantity(itemDb.getQuantity() - item.getQuantity());
                        log.info("Updating quantity for item: {}", itemDb);
                        inventoryRepository.save(itemDb);
                    } catch (InsufficientStockException | ResourceNotFoundException e) {
                        log.error(String.format("Insufficient stock for the sku: %s", item.getSku()), e);
                        sendError(orderEvent, item);
                    } catch (Exception e) {
                        log.error(String.format("Unexpected error processing the item sku: %s", item.getSku()), e);
                        sendError(orderEvent, item);
                    }
                }
        );

        List<ProductDTO> itemDTOS = orderEvent.getOrderDTO().getOrderItemDTO().stream()
                .map(
                        item -> new ProductDTO(
                                null, orderEvent.getOrderDTO().getCustomerId(), item.getSku(), item.getQuantity(), item.getPrice()
                        )).toList();

        kafkaProducer.send(
                new InventoryEvent(orderEvent.getOrderDTO().getCustomerId(), InventoryStatus.INVENTORY_RESERVED, itemDTOS, orderEvent.getOrderDTO().getTransactionId())
        );
    }

    private void sendError(OrderEvent orderEvent, OrderItemDTO item) {
        kafkaProducer.sendError(
                new InventoryEvent(
                        orderEvent.getOrderDTO().getCustomerId(),
                        InventoryStatus.INVENTORY_REJECTED,
                        List.of(new ProductDTO(null, orderEvent.getOrderDTO().getCustomerId(), item.getSku(), item.getQuantity(), item.getPrice())),
                        orderEvent.getOrderDTO().getTransactionId()
                )
        );
    }

    private static void validateStockQuantity(OrderItemDTO item, Product itemDb) {
        if (itemDb.getQuantity() < item.getQuantity()) {
            throw new InsufficientStockException("Not enough quantity for item: " + item.getSku());
        }
    }

    public void handleTransactionError(InventoryEvent inventoryEvent) {
        if(inventoryEvent.getProductDTO() == null ||inventoryEvent.getProductDTO().isEmpty())
            return;
        try {
            inventoryEvent.getProductDTO().forEach(
                    item -> {
                        Product itemDb = inventoryRepository.findBySku(item.getSku()).orElseThrow(
                                () -> new ResourceNotFoundException("Item not found by sku: {}" + item.getSku())
                        );
                        itemDb.setQuantity(itemDb.getQuantity() + item.getQuantity());
                        log.info("Updating quantity for item: {}", itemDb);
                        inventoryRepository.save(itemDb);
                    }
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            kafkaProducer.sendError(
                    new InventoryEvent(
                            inventoryEvent.getCustomerId(),
                            InventoryStatus.INVENTORY_REJECTED,
                            inventoryEvent.getProductDTO(),
                            inventoryEvent.getTransactionId()
                    )
            );
        }
    }
}

