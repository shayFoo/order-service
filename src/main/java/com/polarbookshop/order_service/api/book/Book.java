package com.polarbookshop.order_service.api.book;

import com.polarbookshop.order_service.domain.order.Order;

public record Book(
        String isbn,
        String title,
        String author,
        double price
) {
    public Order toAcceptedOrder(int quantity) {
        return Order.accepted(
                0,
                this.isbn,
                this.title + " by " + this.author,
                this.price,
                quantity
        );
    }
}
