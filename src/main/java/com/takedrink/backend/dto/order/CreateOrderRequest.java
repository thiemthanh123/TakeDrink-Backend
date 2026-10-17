package com.takedrink.backend.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateOrderRequest(

        @NotBlank(message = "Order type is required")
        String orderType,

        @Positive(message = "Dining table ID must be positive")
        Long diningTableId,

        @Positive(message = "Address ID must be positive")
        Long addressId,

        @Size(max = 150, message = "Recipient name must not exceed 150 characters")
        String recipientName,

        @Size(max = 20, message = "Recipient phone must not exceed 20 characters")
        String recipientPhone,

        @Size(max = 500, message = "Delivery address must not exceed 500 characters")
        String deliveryAddress,

        @Size(max = 1000, message = "Note must not exceed 1000 characters")
        String note,

        @NotEmpty(message = "Order items must not be empty")
        List<@Valid OrderItemRequest> items

) {
}
