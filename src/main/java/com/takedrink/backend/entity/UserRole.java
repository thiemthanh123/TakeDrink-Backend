package com.takedrink.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "user_roles")
@Getter
@Setter
@IdClass(UserRole.UserRoleId.class)
public class UserRole {

  @Id
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Id
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "role_id", nullable = false)
  private Role role;

  @Column(name = "assigned_at", nullable = false, updatable = false)
  private Instant assignedAt;

  public static class UserRoleId implements Serializable {

    private Long user;
    private Long role;

    public UserRoleId() {
    }

    public UserRoleId(Long user, Long role) {
      this.user = user;
      this.role = role;
    }

    public Long getUser() {
      return user;
    }

    public void setUser(Long user) {
      this.user = user;
    }

    public Long getRole() {
      return role;
    }

    public void setRole(Long role) {
      this.role = role;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof UserRoleId that)) return false;
      return Objects.equals(user, that.user)
              && Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
      return Objects.hash(user, role);
    }
  }
}