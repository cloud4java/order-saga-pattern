package com.querino.saga.order.domain.model;

import com.querino.saga.order.domain.model.dto.OrderItemDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class OrderItem {
    @Id
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String sku;
    private Integer quantity;
    private Double price;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    public OrderItemDTO toDto() {
        return new OrderItemDTO(id, sku, quantity, price);
    }
    public OrderItem fromDto(OrderItemDTO orderItemDTO) {
        this.id = orderItemDTO.getId();
        this.sku = orderItemDTO.getSku();
        this.quantity = orderItemDTO.getQuantity();
        this.price = orderItemDTO.getPrice();
        return this;
    }
}
