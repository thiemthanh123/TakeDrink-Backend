package com.takedrink.backend.dto.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderCode,
        String orderType,
        String status,
        BigDecimal totalAmount,
        String recipientName,
        String recipientPhone,
        String deliveryAddress,
        String note,
        Long diningTableId,
        Long addressId,
        Instant createdAt,
        List<OrderItemResponse> items
) {
}
