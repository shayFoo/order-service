package com.polarbookshop.order_service.api.book;

import com.polarbookshop.order_service.domain.order.Order;

public record Book(
        String isbn,
        String title,
        String author,
        double price
) {
    private static final int ORDER_ID_NOT_SET = 0;

    public Order toAcceptedOrder(int quantity) {
        return Order.accepted(
                ORDER_ID_NOT_SET,
                this.isbn,
                this.title + " by " + this.author,
                this.price,
                quantity
        );
    }
}
