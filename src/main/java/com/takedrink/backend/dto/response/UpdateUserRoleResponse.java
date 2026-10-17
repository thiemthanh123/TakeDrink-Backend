
package com.takedrink.backend.dto.response;

import java.util.List;

public record UpdateUserRoleResponse(
        Long userId,
        String fullName,
        String email,
        List<String> roles
) {
}
