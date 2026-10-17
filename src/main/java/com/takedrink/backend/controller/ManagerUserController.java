package com.takedrink.backend.controller;

import com.takedrink.backend.dto.ApiResponse;
import com.takedrink.backend.dto.request.UpdateUserRoleRequest;
import com.takedrink.backend.dto.response.UpdateUserRoleResponse;
import com.takedrink.backend.service.UserManagementService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.takedrink.backend.dto.response.ManagerUserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;
import com.takedrink.backend.exception.BadRequestException;
import com.takedrink.backend.dto.request.UpdateUserStatusRequest;
import com.takedrink.backend.dto.response.UpdateUserStatusResponse;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/manager/users")
public class ManagerUserController {

  private final UserManagementService userManagementService;

  public ManagerUserController(
          UserManagementService userManagementService
  ) {
    this.userManagementService = userManagementService;
  }

  @PatchMapping("/{userId}/role")
  @PreAuthorize("hasRole('MANAGER')")
  public ResponseEntity<ApiResponse<UpdateUserRoleResponse>> updateUserRole(
          @PathVariable Long userId,
          @Valid @RequestBody UpdateUserRoleRequest request
  ) {
    UpdateUserRoleResponse response =
            userManagementService.updateUserRole(userId, request);

    return ResponseEntity.ok(
            ApiResponse.success(
                    "User role updated successfully",
                    response
            )
    );
  }


  @GetMapping
  @PreAuthorize("hasRole('MANAGER')")
  public ResponseEntity<ApiResponse<Page<ManagerUserResponse>>> getUsers(
          @RequestParam(required = false) String keyword,
          @RequestParam(required = false) String role,
          @RequestParam(required = false) Boolean active,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size
  ) {
    if (page < 0 || size < 1 || size > 100) {
      throw new BadRequestException(
              "Page must be >= 0 and size must be between 1 and 100"
      );
    }

    Pageable pageable = PageRequest.of(
            page,
            size,
            Sort.by(Sort.Direction.DESC, "createdAt")
    );

    Page<ManagerUserResponse> users =
            userManagementService.getUsers(
                    keyword,
                    role,
                    active,
                    pageable
            );

    return ResponseEntity.ok(
            ApiResponse.success("Users retrieved successfully", users)
    );
  }

  @PatchMapping("/{userId}/status")
  @PreAuthorize("hasRole('MANAGER')")
  public ResponseEntity<ApiResponse<UpdateUserStatusResponse>>
  updateUserStatus(
          @PathVariable Long userId,
          @Valid @RequestBody UpdateUserStatusRequest request
  ) {
    UpdateUserStatusResponse response =
            userManagementService.updateUserStatus(userId, request);

    return ResponseEntity.ok(
            ApiResponse.success(
                    "User status updated successfully",
                    response
            )
    );
  }

}
