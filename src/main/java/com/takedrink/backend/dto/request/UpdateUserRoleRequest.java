package com.takedrink.backend.dto.request;

import com.takedrink.backend.enums.Role;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRoleRequest(

        @NotNull(message = "Role must not be null")
        Role role

) {
}
