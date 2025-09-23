package com.polarbookshop.order_service.persistence.order;

import com.polarbookshop.order_service.domain.order.Order;
import com.polarbookshop.order_service.domain.order.OrderRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class R2DBCOrderRepositoryAdaptor implements OrderRepository {
    private final R2DBCOrderRepository repository;

    public R2DBCOrderRepositoryAdaptor(R2DBCOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public Flux<Order> findAll() {
        return repository.findAll()
                .map(OrderEntity::toDomain);
    }

    @Override
    public Mono<Order> submitOrder(Order order) {
        return Mono.just(order)
                .map(OrderEntity::of)
                .flatMap(repository::save)
                .map(OrderEntity::toDomain);
    }
}
