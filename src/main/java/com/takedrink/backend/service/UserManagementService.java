package com.takedrink.backend.service;

import com.takedrink.backend.dto.request.UpdateUserRoleRequest;
import com.takedrink.backend.dto.response.UpdateUserRoleResponse;
import com.takedrink.backend.dto.response.ManagerUserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.takedrink.backend.dto.request.UpdateUserStatusRequest;
import com.takedrink.backend.dto.response.UpdateUserStatusResponse;

public interface UserManagementService {

  UpdateUserRoleResponse updateUserRole(
          Long userId,
          UpdateUserRoleRequest request
  );

  Page<ManagerUserResponse> getUsers(
          String keyword,
          String role,
          Boolean active,
          Pageable pageable
  );

  UpdateUserStatusResponse updateUserStatus(
          Long userId,
          UpdateUserStatusRequest request
  );

}
