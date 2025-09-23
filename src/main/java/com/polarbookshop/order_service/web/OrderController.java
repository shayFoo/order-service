package com.polarbookshop.order_service.web;

import com.polarbookshop.order_service.domain.order.Order;
import com.polarbookshop.order_service.domain.order.OrderService;
import com.polarbookshop.order_service.domain.order.OrderStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<Order> getAllOrders() {
        return service.getAllOrders();
    }

    @PostMapping
    public Mono<ResponseEntity<OrderResponse>> submitOrder(@RequestBody OrderRequest request) {
        // currently, all orders are rejected
        return service.submitOrder(request.isbn(), request.quantity())
                .map(OrderResponse::from)
                .map(resp -> resp.status() == OrderStatus.REJECTED
                        ? ResponseEntity.unprocessableEntity().body(resp)
                        : ResponseEntity.ok(resp)
                );
    }
}
