package com.polarbookshop.order_service.domain.order;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderRepository {
    Flux<Order> findAll();

    Mono<Order> submitOrder(Order order);
}
