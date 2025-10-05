package com.polarbookshop.order_service.event.function;

import com.polarbookshop.order_service.domain.order.OrderService;
import com.polarbookshop.order_service.event.message.OrderDispatchedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

@Configuration
public class OrderFunctions {

    private static final Logger log = LoggerFactory.getLogger(OrderFunctions.class);

    @Bean
    public Consumer<Flux<OrderDispatchedMessage>> dispatchOrder(OrderService orderService) {
        return flux -> orderService.consumeOrderDispatchedEvent(flux)
                .doOnNext(order -> log.info("Order with ID {} has been dispatched", order.id()))
                .subscribe();
    }
}
