
package com.takedrink.backend.dto.response;

public record UpdateUserStatusResponse(
        Long userId,
        String fullName,
        String email,
        Boolean active
) {
}
