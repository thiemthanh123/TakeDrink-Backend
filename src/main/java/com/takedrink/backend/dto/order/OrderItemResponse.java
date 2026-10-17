package com.takedrink.backend.dto.order;

import java.math.BigDecimal;

public record OrderItemResponse(

        Long id,

        Long productId,

        String productName,

        BigDecimal unitPrice,

        Integer quantity,

        BigDecimal subtotal,

        String note

) {
}
