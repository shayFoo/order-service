package com.polarbookshop.order_service.web;

import com.polarbookshop.order_service.domain.order.Order;
import com.polarbookshop.order_service.domain.order.OrderStatus;

public record OrderResponse(
        long orderId,
        String bookIsbn,
        String bookName,
        double bookPrice,
        int quantity,
        OrderStatus status
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.id(),
                order.bookIsbn(),
                order.bookName(),
                order.bookPrice(),
                order.quantity(),
                order.status()
        );
    }
}
