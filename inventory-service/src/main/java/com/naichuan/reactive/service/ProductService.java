package com.naichuan.reactive.service;

import com.naichuan.constants.OrderStatus;
import com.naichuan.domain.Order;
import com.naichuan.domain.Product;
import com.naichuan.reactive.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Mono<Order> handleOrder(Order order) {
        log.info("Handle order invoked with: {}", order);
        return Flux.fromIterable(order.getLineItems())
                .flatMap(l -> productRepository.findById(l.getProductId()))
                .flatMap(p -> {
                    int q = order.getLineItems()
                            .stream()
                            .filter(l -> l.getProductId().equals(p.getId()))
                            .findAny()
                            .get()
                            .getQuantity();
                    if (p.getStock() >= q) {
                        p.setStock(p.getStock() - q);
                        return productRepository.save(p);
                    } else {
                        return Mono.error(new RuntimeException("Product is out of stock: " + p.getId()));
                    }
                })
                .then(Mono.just(order.setOrderStatus(OrderStatus.SUCCESS)));
    }

    @Transactional
    public Mono<Order> revertOrder(Order order) {
        log.info("Revert order invoked with: {}", order);
        return Flux.fromIterable(order.getLineItems())
                .flatMap(l -> productRepository.findById(l.getProductId()))
                .flatMap(p -> {
                    int q = order.getLineItems()
                            .stream()
                            .filter(l -> l.getProductId().equals(p.getId()))
                            .collect(Collectors.toList())
                            .get(0)
                            .getQuantity();
                    p.setStock(p.getStock() + q);
                    return productRepository.save(p);
                })
                .then(Mono.just(order.setOrderStatus(OrderStatus.SUCCESS)));
    }

    public Flux<Product> getProducts() {
        return productRepository.findAll();
    }
}
