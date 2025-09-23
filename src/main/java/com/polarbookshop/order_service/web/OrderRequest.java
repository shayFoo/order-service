package com.polarbookshop.order_service.web;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderRequest(
        @NotBlank(message = "Book ISBN must be provided")
        String isbn,
        @NotNull(message = "Quantity must be provided")
        @Min(value = 1, message = "You must order at least one book")
        @Max(value = 5, message = "You cannot order more than 5 books at a time")
        Integer quantity
) {
}
