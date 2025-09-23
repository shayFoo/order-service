package com.polarbookshop.order_service.persistence.order;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface R2DBCOrderRepository extends ReactiveCrudRepository<OrderEntity, Long> {
}
