package com.naichuan.reactive.repository;

import com.naichuan.domain.Order;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface OrderRepository extends ReactiveMongoRepository<Order, ObjectId> {
}
