package com.takedrink.backend.repository;

import com.takedrink.backend.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmailIgnoreCase(String email);

  boolean existsByEmailIgnoreCase(String email);

  @EntityGraph(attributePaths = {"userRoles", "userRoles.role"})
  Optional<User> findWithRolesByEmailIgnoreCase(String email);
}