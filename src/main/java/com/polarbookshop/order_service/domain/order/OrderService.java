package com.polarbookshop.order_service.domain.order;

import com.polarbookshop.order_service.api.book.BookClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OrderService {
    private final OrderRepository repository;
    private final BookClient bookClient;

    public OrderService(OrderRepository repository, BookClient bookClient) {
        this.repository = repository;
        this.bookClient = bookClient;
    }

    public Flux<Order> getAllOrders() {
        return repository.findAll();
    }

    public Mono<Order> submitOrder(String isbn, int quantity) {
        return bookClient.getBookByIsbn(isbn)
                .map(book -> book.toAcceptedOrder(quantity))
                .defaultIfEmpty(Order.rejected(isbn, quantity))
                .flatMap(repository::submitOrder);
    }
}
