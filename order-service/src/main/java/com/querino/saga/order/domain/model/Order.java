package com.querino.saga.order.domain.model;

import com.querino.saga.order.domain.model.dto.OrderDTO;
import com.querino.saga.order.domain.model.dto.OrderItemDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    public OrderDTO toDTO() {
        List<OrderItemDTO> items = orderItems.stream().map(item -> item.toDto()).toList();
        return new OrderDTO(id, customerId, orderStatus, totalAmount, items);
    }
    public Order fromDTO(OrderDTO orderDTO) {
        this.id = orderDTO.getId();
        this.customerId = orderDTO.getCustomerId();
        this.orderStatus = orderDTO.getOrderStatus();
        this.totalAmount = orderDTO.getTotalAmount();
        if(this.orderItems == null) this.orderItems = List.of();
        else this.orderItems.clear();

        this.orderItems = orderDTO.getOrderItemDTO().stream().map(item -> new OrderItem().fromDto(item)).toList();
        this.orderItems.forEach(item -> item.setOrder(this));
        return this;
    }
}