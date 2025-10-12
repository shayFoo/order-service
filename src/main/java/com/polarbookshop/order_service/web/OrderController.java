package com.polarbookshop.order_service.web;

import com.polarbookshop.order_service.domain.order.Order;
import com.polarbookshop.order_service.domain.order.OrderService;
import com.polarbookshop.order_service.domain.order.OrderStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
    public Flux<Order> getAllOrders(@AuthenticationPrincipal Jwt jwt) {
        return service.getOrdersForUser(jwt.getSubject());
    }

    @PostMapping
    public Mono<ResponseEntity<OrderResponse>> submitOrder(@RequestBody OrderRequest request) {
        return service.submitOrder(request.isbn(), request.quantity())
                .map(OrderResponse::from)
                .map(resp -> resp.status() == OrderStatus.REJECTED
                        ? ResponseEntity.unprocessableEntity().body(resp)
                        : ResponseEntity.ok(resp)
                );
    }
}
