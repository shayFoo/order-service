package com.polarbookshop.order_service.persistence.order;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface R2DBCOrderRepository extends ReactiveCrudRepository<OrderEntity, Long> {
    Flux<OrderEntity> findAllByCreatedBy(String username);
}
