package com.querino.saga.order.service;

import com.querino.saga.order.domain.OrderEvent;
import com.querino.saga.order.domain.OrderRepository;
import com.querino.saga.order.domain.model.Order;
import com.querino.saga.order.domain.model.dto.OrderDTO;
import com.querino.saga.order.domain.model.OrderStatus;
import com.querino.saga.order.kafka.OrderEventProducer;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer kafkaProducer;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderEventProducer kafkaProducer) {
        this.orderRepository = orderRepository;
        this.kafkaProducer = kafkaProducer;
    }

    public OrderDTO createOrder(@Valid OrderDTO orderDTO) {
        Order order = new Order();
        order.fromDTO(orderDTO);
        order.setTotalAmount(BigDecimal.valueOf(
                order.getOrderItems().stream().map(item -> item.getPrice()* item.getQuantity())
                        .reduce(0d, (a,b)-> a+b)

        ));
        Order savedOrder = orderRepository.save(order);


        // Publish the order to Kafka
        kafkaProducer.sendOrder(
                new OrderEvent(savedOrder.getId().toString(), OrderStatus.ORDER_CREATED, savedOrder.toDTO())
        );
        return savedOrder.toDTO();
    }

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream().map(
                entity -> entity.toDTO()
        ).toList();
    }

    public OrderDTO getOrderById(Long id) {
        return orderRepository.findById(id).map(
                entity -> entity.toDTO()
        ).orElseThrow();
    }

    public void updateOrderStatus(String orderId, OrderStatus orderStatus) {
        orderRepository.findById(Long.valueOf(orderId)).ifPresent(
                order -> {
                    order.setOrderStatus(orderStatus);
                    orderRepository.save(order);
                    kafkaProducer.sendOrder(new OrderEvent(orderId, orderStatus, order.toDTO()));
                });
    }
}

