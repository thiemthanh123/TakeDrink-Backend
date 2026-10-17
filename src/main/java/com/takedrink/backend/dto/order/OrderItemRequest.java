package com.takedrink.backend.dto.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record OrderItemRequest(

        @NotNull(message = "Product ID is required")
        @Positive(message = "Product ID must be positive")
        Long productId,

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        Integer quantity,

        @Size(max = 500, message = "Item note must not exceed 500 characters")
        String note

) {
}
