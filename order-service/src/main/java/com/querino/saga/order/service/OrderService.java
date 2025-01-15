package com.querino.saga.order.service;

import com.querino.saga.order.domain.OrderEvent;
import com.querino.saga.order.domain.OrderRepository;
import com.querino.saga.order.domain.model.Order;
import com.querino.saga.order.domain.model.OrderStatus;
import com.querino.saga.order.domain.model.dto.OrderDTO;
import com.querino.saga.order.kafka.OrderEventProducer;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer kafkaProducer;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderEventProducer kafkaProducer) {
        this.orderRepository = orderRepository;
        this.kafkaProducer = kafkaProducer;
    }

    public OrderDTO createOrder(@Valid OrderDTO orderDTO) {

        Order entity = new Order().fromDTO(orderDTO);
        entity.setTransactionId(UUID.randomUUID().toString());
        Order savedOrder = orderRepository.save(entity);

        kafkaProducer.sendOrder(
                new OrderEvent(savedOrder.getId().toString(), OrderStatus.ORDER_CREATED, savedOrder.toDTO())
        );
        return savedOrder.toDTO();
    }

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(Order::toDTO).toList();
    }

    public OrderDTO getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(Order::toDTO)
                .orElseThrow();
    }

    public void handleTransactionError(String transactionErrorId) {
        Order order = orderRepository.findOneByTransactionId(transactionErrorId).orElseThrow();
        order.setOrderStatus(OrderStatus.ORDER_CANCELLED);
        orderRepository.save(order);
    }
}

