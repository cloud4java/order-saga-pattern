package com.querino.saga.order.domain.model;

import com.querino.saga.order.domain.model.dto.OrderDTO;
import com.querino.saga.order.domain.model.dto.OrderItemDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long customerId;
    private OrderStatus orderStatus;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
    private String transactionId;

    public OrderDTO toDTO() {
        List<OrderItemDTO> items = orderItems.stream().map(item -> item.toDto()).toList();
        return new OrderDTO(id, customerId, orderStatus, items, transactionId);
    }
    public Order fromDTO(OrderDTO orderDTO) {
        this.id = orderDTO.getId();
        this.customerId = orderDTO.getCustomerId();
        this.orderStatus = orderDTO.getOrderStatus();
        if(this.orderItems == null) this.orderItems = List.of();
        else this.orderItems.clear();

        this.orderItems = orderDTO.getOrderItemDTO().stream().map(item -> new OrderItem().fromDto(item)).toList();
        this.orderItems.forEach(item -> item.setOrder(this));
        this.transactionId = orderDTO.getTransactionId();
        return this;
    }
}