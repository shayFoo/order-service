package com.polarbookshop.order_service.domain.order;

public record Order(
        long id,
        String bookIsbn,
        String bookName,
        double bookPrice,
        int quantity,
        OrderStatus status
) {
    public static Order rejected(long id, String bookIsbn, int quantity) {
        return new Order(id, bookIsbn, "", 0.0, quantity, OrderStatus.REJECTED);
    }

    public static Order accepted(long id, String bookIsbn, String bookName, double bookPrice, int quantity) {
        return new Order(id, bookIsbn, bookName, bookPrice, quantity, OrderStatus.ACCEPTED);
    }

    public boolean isAccepted() {
        return this.status == OrderStatus.ACCEPTED;
    }
}
