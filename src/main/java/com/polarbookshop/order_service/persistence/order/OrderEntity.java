package com.polarbookshop.order_service.persistence.order;

import com.polarbookshop.order_service.domain.order.Order;
import com.polarbookshop.order_service.domain.order.OrderStatus;
import org.springframework.data.annotation.*;
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
        @CreatedBy
        String createdBy,
        @LastModifiedBy
        String modifiedBy,
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
                null, // populated by @CreatedDate
                null, // populated by @LastModifiedDate
                null,  // populated by @CreatedBy
                null, // populated by @LastModifiedBy
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
                this.createdBy,
                this.modifiedBy,
                this.version
        );
    }
}
