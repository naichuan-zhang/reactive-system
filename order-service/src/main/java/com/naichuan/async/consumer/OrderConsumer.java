package com.naichuan.async.consumer;

import com.naichuan.async.producer.OrderProducer;
import com.naichuan.constants.OrderStatus;
import com.naichuan.domain.Order;
import com.naichuan.reactive.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class OrderConsumer {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProducer orderProducer;

    @KafkaListener(topics = "orders", groupId = "orders")
    public void consume(Order order) {
        log.info("Order received to process: {}", order);
        if (OrderStatus.INITIATION_SUCCESS.equals(order.getOrderStatus())) {
            orderRepository.findById(order.getId())
                    .map(o -> {
                        orderProducer.sendMessage(o.setOrderStatus(OrderStatus.RESERVE_INVENTORY));
                        return o.setOrderStatus(order.getOrderStatus())
                                .setResponseMessage(order.getResponseMessage());
                    })
                    .flatMap(orderRepository::save)
                    .subscribe();
        } else if (OrderStatus.INVENTORY_SUCCESS.equals(order.getOrderStatus())) {
            orderRepository.findById(order.getId())
                    .map(o -> {
                        orderProducer.sendMessage(o.setOrderStatus(OrderStatus.PREPARE_SHIPPING));
                        return o.setOrderStatus(order.getOrderStatus())
                                .setResponseMessage(order.getResponseMessage());
                    })
                    .flatMap(orderRepository::save)
                    .subscribe();
        } else if (OrderStatus.SHIPPING_FAILURE.equals(order.getOrderStatus())) {
            orderRepository.findById(order.getId())
                    .map(o -> {
                        orderProducer.sendMessage(o.setOrderStatus(OrderStatus.REVERT_INVENTORY));
                        return o.setOrderStatus(order.getOrderStatus())
                                .setResponseMessage(order.getResponseMessage());
                    })
                    .flatMap(orderRepository::save)
                    .subscribe();
        } else {
            orderRepository.findById(order.getId())
                    .map(o -> o.setOrderStatus(order.getOrderStatus())
                            .setResponseMessage(order.getResponseMessage()))
                    .flatMap(orderRepository::save)
                    .subscribe();
        }
    }
}
