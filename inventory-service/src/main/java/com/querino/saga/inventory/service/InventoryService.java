package com.querino.saga.inventory.service;

import com.querino.saga.inventory.domain.InventoryEvent;
import com.querino.saga.inventory.domain.InventoryRepository;
import com.querino.saga.inventory.domain.model.InventoryItem;
import com.querino.saga.inventory.domain.model.InventoryItemDTO;
import com.querino.saga.inventory.domain.model.InventoryStatus;
import com.querino.saga.inventory.domain.model.order.OrderEvent;
import com.querino.saga.inventory.kafka.InventoryEventProducer;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

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

    public InventoryItemDTO createItem(@Valid InventoryItemDTO inventoryItemDTO) {
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.fromDTO(inventoryItemDTO);
        InventoryItem savedInventoryItem = inventoryRepository.save(inventoryItem);

        kafkaProducer.send(
                new InventoryEvent(savedInventoryItem.getId(), InventoryStatus.INVENTORY_UPDATED, List.of(savedInventoryItem.toDTO()))
        );
        return savedInventoryItem.toDTO();
    }

    public List<InventoryItemDTO> getAllItems() {
        return inventoryRepository.findAll().stream().map(
                entity -> entity.toDTO()
        ).toList();
    }

    public InventoryItemDTO getItemById(Long id) {
        return inventoryRepository.findById(id).map(
                entity -> entity.toDTO()
        ).orElseThrow();
    }

    public void updateQuantity(OrderEvent orderEvent) {
        orderEvent.getOrderDTO().getOrderItemDTO().forEach(
                item -> {
                    InventoryItem itemDb = inventoryRepository.findBySku(item.getSku()).orElseThrow();
                    if(itemDb.getQuantity() < item.getQuantity()){
                        throw new RuntimeException("Not enough quantity for item: " + item.getSku());
                    }
                    itemDb.setQuantity(itemDb.getQuantity() - item.getQuantity());
                    log.info("Updating quantity for item: {}", itemDb);
                    inventoryRepository.save(itemDb);
                }
        );

        List<InventoryItemDTO> itemDTOS = orderEvent.getOrderDTO().getOrderItemDTO().stream()
                .map(
                item -> new InventoryItemDTO(
                        null, orderEvent.getOrderDTO().getCustomerId(), item.getSku(), item.getQuantity(), item.getPrice()
                )).toList();
        InventoryEvent inventoryEvent = new InventoryEvent(orderEvent.getOrderDTO().getCustomerId(), InventoryStatus.INVENTORY_UPDATED, itemDTOS);

        kafkaProducer.send(inventoryEvent);
    }
}

