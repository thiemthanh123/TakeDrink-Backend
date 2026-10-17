package com.takedrink.backend.service.impl;

import com.takedrink.backend.dto.request.UpdateUserRoleRequest;
import com.takedrink.backend.dto.response.ManagerUserResponse;
import com.takedrink.backend.dto.response.UpdateUserRoleResponse;
import com.takedrink.backend.entity.Role;
import com.takedrink.backend.entity.User;
import com.takedrink.backend.entity.UserRole;
import com.takedrink.backend.exception.BadRequestException;
import com.takedrink.backend.exception.UserNotFoundException;
import com.takedrink.backend.repository.RoleRepository;
import com.takedrink.backend.repository.UserRepository;
import com.takedrink.backend.repository.UserRoleRepository;
import com.takedrink.backend.service.UserManagementService;
import com.takedrink.backend.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.takedrink.backend.dto.request.UpdateUserStatusRequest;
import com.takedrink.backend.dto.response.UpdateUserStatusResponse;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final UserRoleRepository userRoleRepository;

  @Override
  @Transactional(readOnly = true)
  public Page<ManagerUserResponse> getUsers(
          String keyword,
          String role,
          Boolean active,
          Pageable pageable
  ) {
    // 1. Validate role filter
    if (role != null && !role.isBlank()) {
      try {
        com.takedrink.backend.enums.Role.valueOf(
                role.trim().toUpperCase()
        );
      } catch (IllegalArgumentException exception) {
        throw new BadRequestException(
                "Invalid role: " + role
        );
      }
    }

    // 2. Build dynamic search conditions
    Specification<User> specification =
            UserSpecification.keywordContains(keyword)
                    .and(UserSpecification.activeEquals(active))
                    .and(UserSpecification.hasRole(role));

    // 3. Query users with pagination
    return userRepository.findAll(specification, pageable)
            .map(this::toManagerUserResponse);
  }

  // Convert User entity to response DTO
  private ManagerUserResponse toManagerUserResponse(User user) {
    List<String> roles = user.getUserRoles()
            .stream()
            .map(UserRole::getRole)
            .map(Role::getName)
            .distinct()
            .toList();

    return new ManagerUserResponse(
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getPhone(),
            user.getActive(),
            roles,
            user.getCreatedAt(),
            user.getUpdatedAt()
    );
  }

  @Override
  @Transactional
  public UpdateUserRoleResponse updateUserRole(
          Long userId,
          UpdateUserRoleRequest request
  ) {
    // 1. Find user
    User user = userRepository.findById(userId)
            .orElseThrow(() ->
                    new UserNotFoundException(
                            "User not found with id: " + userId
                    )
            );

    // 2. Get requested role
    com.takedrink.backend.enums.Role requestedRole =
            request.role();

    // 3. Find role in database
    Role role = roleRepository.findByNameIgnoreCase(
                    requestedRole.name()
            )
            .orElseThrow(() ->
                    new BadRequestException(
                            "Role not found: " + requestedRole.name()
                    )
            );

    // 4. Check whether role is already assigned
    boolean alreadyAssigned = userRoleRepository
            .findByUser_Id(userId)
            .stream()
            .anyMatch(userRole ->
                    userRole.getRole().getId().equals(role.getId())
            );

    // 5. Assign role if necessary
    if (!alreadyAssigned) {
      UserRole userRole = new UserRole();
      userRole.setUser(user);
      userRole.setRole(role);
      userRole.setAssignedAt(Instant.now());

      userRoleRepository.save(userRole);
    }

    // 6. Get updated roles
    List<String> roles = userRoleRepository
            .findByUser_Id(userId)
            .stream()
            .map(userRole -> userRole.getRole().getName())
            .distinct()
            .toList();

    // 7. Return response
    return new UpdateUserRoleResponse(
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            roles
    );
  }

  @Override
  @Transactional
  public UpdateUserStatusResponse updateUserStatus(
          Long userId,
          UpdateUserStatusRequest request
  ) {
    // 1. Find user
    User user = userRepository.findById(userId)
            .orElseThrow(() ->
                    new UserNotFoundException(
                            "User not found with id: " + userId
                    )
            );

    // 2. Update active status
    user.setActive(request.active());

    // 3. Save updated user
    User updatedUser = userRepository.save(user);

    // 4. Return response
    return new UpdateUserStatusResponse(
            updatedUser.getId(),
            updatedUser.getFullName(),
            updatedUser.getEmail(),
            updatedUser.getActive()
    );
  }

}
