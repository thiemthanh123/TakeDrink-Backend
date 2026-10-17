package com.takedrink.backend.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(
        @NotNull(message = "Active status must not be null")
        Boolean active
) {
}
