package com.polarbookshop.order_service.domain.order;

public record Order(
        String bookIsbn,
        String bookName,
        double bookPrice,
        int quantity,
        OrderStatus status
) {
    public static Order rejected(String bookIsbn, int quantity) {
        return new Order(bookIsbn, "", 0.0, quantity, OrderStatus.REJECTED);
    }

    public static Order accepted(String bookIsbn, String bookName, double bookPrice, int quantity) {
        return new Order(bookIsbn, bookName, bookPrice, quantity, OrderStatus.ACCEPTED);
    }

    public boolean isAccepted() {
        return this.status == OrderStatus.ACCEPTED;
    }
}
