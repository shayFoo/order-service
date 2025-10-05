package com.polarbookshop.order_service.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.polarbookshop.order_service.domain.order.OrderStatus;
import com.polarbookshop.order_service.event.message.OrderDispatchedMessage;
import com.polarbookshop.order_service.persistence.order.R2DBCOrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestChannelBinderConfiguration.class)
public class OrderIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.6-alpine");

    @Container
    static RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:4.1.4-management-alpine");

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> String.format("r2dbc:postgresql://%s:%d/%s", postgres.getHost(), postgres.getMappedPort(5432), postgres.getDatabaseName()));
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);
        registry.add("spring.rabbitmq.host", rabbitmq::getHost);
        registry.add("spring.rabbitmq.port", rabbitmq::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitmq::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitmq::getAdminPassword);
        registry.add("polar.catalog-service-uri", () -> wireMock.baseUrl());
    }

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    R2DBCOrderRepository orderRepository;

    @Autowired
    OutputDestination output;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        orderRepository.deleteAll().as(StepVerifier::create).verifyComplete();
    }

    @Test
    void whenSubmitDispatchOrder_thenOrderSaved() throws IOException {
        wireMock.stubFor(WireMock.get("/books/1234567890")
                .willReturn(WireMock.okJson("""
                        {
                            "isbn": "1234567890",
                            "title": "Title",
                            "author": "Author",
                            "price": 9.90,
                            "publisher": "Polarsophia",
                            "availableCopies": 5
                        }
                        """)));

        EntityExchangeResult<OrderResponse> exchangeResult = webTestClient.post()
                .uri("/orders")
                .bodyValue(new OrderRequest("1234567890", 3))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(OrderResponse.class)
                .value(order -> {
                    assertThat(order).isNotNull();
                    assertThat(order.status()).isEqualTo(OrderStatus.ACCEPTED);
                    assertThat(order.bookIsbn()).isEqualTo("1234567890");
                    assertThat(order.quantity()).isEqualTo(3);
                })
                .returnResult();

        assertEventPublished(exchangeResult);
        StepVerifier.create(orderRepository.findAll())
                .expectNextMatches(order -> order.bookIsbn().equals("1234567890") && order.quantity() == 3)
                .expectComplete()
                .verify();
    }

    private void assertEventPublished(EntityExchangeResult<OrderResponse> exchangeResult) throws IOException {
        OrderResponse response = exchangeResult.getResponseBody();
        Message<byte[]> result = output.receive(500, "order-accepted");
        OrderDispatchedMessage message = objectMapper.readValue(result.getPayload(), OrderDispatchedMessage.class);
        Assertions.assertNotNull(response);
        assertThat(message.orderId()).isEqualTo(response.orderId());
    }
}
