package com.takedrink.backend.dto.product;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        Long categoryId,
        String categoryName,
        Boolean available,
        Instant createdAt,
        Instant updatedAt
) {
}
