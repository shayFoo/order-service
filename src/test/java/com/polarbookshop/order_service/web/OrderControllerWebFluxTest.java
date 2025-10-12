package com.polarbookshop.order_service.web;

import com.polarbookshop.order_service.domain.order.Order;
import com.polarbookshop.order_service.domain.order.OrderService;
import com.polarbookshop.order_service.domain.order.OrderStatus;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.AccessTokenResponse;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

@WebFluxTest(OrderController.class)
@Testcontainers
public class OrderControllerWebFluxTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockitoBean
    private OrderService orderService;

    @Container
    private static final KeycloakContainer keycloakContainer =
            new KeycloakContainer("keycloak/keycloak:26.4")
                    .withRealmImportFile("test-realm-config.json");

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloakContainer.getAuthServerUrl() + "/realms/PolarBookshop");
    }

    private static AccessTokenResponse isabelleToken;

    @BeforeAll
    static void generateAccessToken() {
        String baseUrl = keycloakContainer.getAuthServerUrl()
                + "/realms/PolarBookshop/protocol/openid-connect/token";
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
        isabelleToken = authenticateWith("isabelle", webClient);
    }

    private static AccessTokenResponse authenticateWith(String username, WebClient webClient) {
        return webClient
                .post()
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("client_id", "polar-test")
                        .with("username", username)
                        .with("password", "password")
                )
                .retrieve()
                .bodyToMono(AccessTokenResponse.class)
                .block();
    }

    @Test
    void whenBookNotAvailableTHenRejectOrder() {
        OrderRequest orderRequest = new OrderRequest("1234567890", 3);
        Order expected = Order.rejected(1, orderRequest.isbn(), orderRequest.quantity());
        BDDMockito.given(orderService.submitOrder(orderRequest.isbn(), orderRequest.quantity()))
                .willReturn(Mono.just(expected));

        webTestClient.post()
                .uri("/orders")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(isabelleToken.getToken()))
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
