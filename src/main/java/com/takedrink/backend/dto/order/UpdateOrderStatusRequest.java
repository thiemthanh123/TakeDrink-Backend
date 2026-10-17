
package com.takedrink.backend.dto.order;

import com.takedrink.backend.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(

        @NotNull(message = "Order status is required")
        OrderStatus status

) {}
