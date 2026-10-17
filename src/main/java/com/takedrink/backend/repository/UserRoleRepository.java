package com.takedrink.backend.repository;

import com.takedrink.backend.entity.UserRole;
import com.takedrink.backend.entity.UserRole.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository
        extends JpaRepository<UserRole, UserRoleId> {

  List<UserRole> findByUser_Id(Long userId);

  void deleteByUser_Id(Long userId);
}