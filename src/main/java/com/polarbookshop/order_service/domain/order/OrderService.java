package com.polarbookshop.order_service.domain.order;

import com.polarbookshop.order_service.api.book.BookClient;
import com.polarbookshop.order_service.event.bridge.OrderAcceptedEventPublisher;
import com.polarbookshop.order_service.event.message.OrderDispatchedMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OrderService {
    private final OrderRepository repository;
    private final BookClient bookClient;
    private final OrderAcceptedEventPublisher eventPublisher;

    public OrderService(OrderRepository repository, BookClient bookClient, OrderAcceptedEventPublisher eventPublisher) {
        this.repository = repository;
        this.bookClient = bookClient;
        this.eventPublisher = eventPublisher;
    }

    public Flux<Order> getAllOrders() {
        return repository.findAll();
    }

    @Transactional
    public Mono<Order> submitOrder(String isbn, int quantity) {
        return bookClient.getBookByIsbn(isbn)
                .map(book -> book.toAcceptedOrder(quantity))
                .defaultIfEmpty(Order.rejected(0, isbn, quantity))
                .flatMap(repository::save)
                .doOnNext(eventPublisher::publishOrderAcceptedEvent);
    }

    public Flux<Order> consumeOrderDispatchedEvent(Flux<OrderDispatchedMessage> flux) {
        return flux.map(OrderDispatchedMessage::orderId)
                .flatMap(repository::dispatch);
    }
}
