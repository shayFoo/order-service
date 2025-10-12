package com.polarbookshop.order_service.domain.order;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderRepository {
    Flux<Order> findAll();

    Flux<Order> findAllByCreatedBy(String userId);

    Mono<Order> save(Order order);

    Mono<Order> dispatch(long id);
}
