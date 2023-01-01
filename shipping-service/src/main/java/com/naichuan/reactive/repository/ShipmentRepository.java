package com.naichuan.reactive.repository;

import com.naichuan.domain.Shipment;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ShipmentRepository extends ReactiveMongoRepository<Shipment, ObjectId> {
}
