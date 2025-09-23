package com.polarbookshop.order_service.api.book;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@WireMockTest
public class BookClientTests {
    private BookClient bookClient;

    @BeforeEach
    void setup(WireMockRuntimeInfo runtimeInfo) {
        WebClient webClient = WebClient.builder()
                .baseUrl(runtimeInfo.getHttpBaseUrl())
                .build();
        this.bookClient = new BookClient(webClient);
    }

    @Test
    void whenBookExistsThenReturnBook() {
        String bookIsbn = "1234567890";
        stubFor(get("/books/" + bookIsbn)
                .willReturn(
                        okJson("""
                                {
                                    "isbn": "%s",
                                    "title": "Title",
                                    "author": "Author",
                                    "price": 9.90,
                                    "publisher": "Polarsophia"
                                }
                                """.formatted(bookIsbn))
                ));
        
        Mono<Book> book = bookClient.getBookByIsbn(bookIsbn);

        StepVerifier.create(book)
                .expectNextMatches(b -> b.isbn().equals(bookIsbn))
                .verifyComplete();
    }
}
