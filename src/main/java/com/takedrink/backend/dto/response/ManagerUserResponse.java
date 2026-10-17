package com.takedrink.backend.dto.response;

import java.time.Instant;
import java.util.List;

public record ManagerUserResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        Boolean active,
        List<String> roles,
        Instant createdAt,
        Instant updatedAt
) {
}
