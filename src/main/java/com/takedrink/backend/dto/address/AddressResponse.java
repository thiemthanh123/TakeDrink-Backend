package com.takedrink.backend.dto.address;

import java.time.Instant;

public record AddressResponse(
        Long id,
        String recipientName,
        String phone,
        String addressLine,
        String ward,
        String district,
        String province,
        Boolean defaultAddress,
        Instant createdAt,
        Instant updatedAt
) {}
