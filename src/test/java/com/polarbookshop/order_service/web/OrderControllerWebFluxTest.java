package com.polarbookshop.order_service.web;

import com.polarbookshop.order_service.domain.order.Order;
import com.polarbookshop.order_service.domain.order.OrderService;
import com.polarbookshop.order_service.domain.order.OrderStatus;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

@WebFluxTest(OrderController.class)
public class OrderControllerWebFluxTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockitoBean
    private OrderService orderService;

    @Test
    void whenBookNotAvailableTHenRejectOrder() {
        OrderRequest orderRequest = new OrderRequest("1234567890", 3);
        Order expected = Order.rejected(orderRequest.isbn(), orderRequest.quantity());
        BDDMockito.given(orderService.submitOrder(orderRequest.isbn(), orderRequest.quantity()))
                .willReturn(Mono.just(expected));

        webTestClient.post()
                .uri("/orders")
                .bodyValue(orderRequest)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(Order.class).value(actual -> {
                    assertThat(actual).isNotNull();
                    assertThat(actual.status()).isEqualTo(OrderStatus.REJECTED);
                });
    }
}
