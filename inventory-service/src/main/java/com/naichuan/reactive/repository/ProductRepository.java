package com.naichuan.reactive.repository;

import com.naichuan.domain.Product;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductRepository extends ReactiveMongoRepository<Product, ObjectId> {
}
