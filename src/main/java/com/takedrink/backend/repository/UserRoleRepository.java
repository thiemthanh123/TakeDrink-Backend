package com.takedrink.backend.repository;

import com.takedrink.backend.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository
        extends JpaRepository<UserRole, UserRole.UserRoleId> {
}