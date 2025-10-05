package com.polarbookshop.order_service.event.bridge;

import com.polarbookshop.order_service.domain.order.Order;
import com.polarbookshop.order_service.event.message.OrderAcceptedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class OrderAcceptedEventPublisher {

    private final StreamBridge streamBridge;
    private final static Logger log = LoggerFactory.getLogger(OrderAcceptedEventPublisher.class);

    public OrderAcceptedEventPublisher(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void publishOrderAcceptedEvent(Order order) {
        // Only publish event for accepted orders
        if (!order.isAccepted()) {
            return;
        }

        OrderAcceptedMessage orderAcceptedMessage = new OrderAcceptedMessage(order.id());
        
        log.info("Publishing OrderAcceptedEvent for order ID: {}", order.id());
        boolean result = streamBridge.send("acceptOrder-out-0", orderAcceptedMessage);
        log.info("Result of sending OrderAcceptedEvent for order ID {}: {}", order.id(), result ? "Success" : "Failure");
    }
}
