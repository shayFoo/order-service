package com.polarbookshop.order_service.persistence.order;

import com.polarbookshop.order_service.domain.order.Order;
import com.polarbookshop.order_service.domain.order.OrderStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@Table(name = "orders")
public record OrderEntity(
        @Id
        Long id,
        @NonNull
        String bookIsbn,
        @NonNull
        String bookName,
        double bookPrice,
        int quantity,
        @NonNull
        OrderStatus status,
        @CreatedDate
        LocalDateTime createdAt,
        @LastModifiedDate
        LocalDateTime updatedAt,
        @Version
        int version
) {
    public static OrderEntity of(Order order) {
        return new OrderEntity(
                null,
                order.bookIsbn(),
                order.bookName(),
                order.bookPrice(),
                order.quantity(),
                order.status(),
                null,
                null,
                0
        );
    }

    public Order toDomain() {
        return new Order(
                this.id,
                this.bookIsbn,
                this.bookName,
                this.bookPrice,
                this.quantity,
                this.status
        );
    }

    public OrderEntity dispatched() {
        return new OrderEntity(
                this.id,
                this.bookIsbn,
                this.bookName,
                this.bookPrice,
                this.quantity,
                OrderStatus.DISPATCHED,
                this.createdAt,
                this.updatedAt,
                this.version
        );
    }
}
