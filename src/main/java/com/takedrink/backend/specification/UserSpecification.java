package com.takedrink.backend.specification;

import com.takedrink.backend.entity.User;
import org.springframework.data.jpa.domain.Specification;

public final class UserSpecification {

  private UserSpecification() {
  }

  public static Specification<User> keywordContains(String keyword) {
    return (root, query, criteriaBuilder) -> {
      if (keyword == null || keyword.isBlank()) {
        return criteriaBuilder.conjunction();
      }

      String pattern = "%" + keyword.trim().toLowerCase() + "%";

      return criteriaBuilder.or(
              criteriaBuilder.like(
                      criteriaBuilder.lower(root.get("fullName")),
                      pattern
              ),
              criteriaBuilder.like(
                      criteriaBuilder.lower(root.get("email")),
                      pattern
              ),
              criteriaBuilder.like(
                      criteriaBuilder.lower(root.get("phone")),
                      pattern
              )
      );
    };
  }

  public static Specification<User> activeEquals(Boolean active) {
    return (root, query, criteriaBuilder) -> {
      if (active == null) {
        return criteriaBuilder.conjunction();
      }

      return criteriaBuilder.equal(root.get("active"), active);
    };
  }

  public static Specification<User> hasRole(String roleName) {
    return (root, query, criteriaBuilder) -> {
      if (roleName == null || roleName.isBlank()) {
        return criteriaBuilder.conjunction();
      }

      query.distinct(true);

      return criteriaBuilder.equal(
              criteriaBuilder.upper(
                      root.join("userRoles")
                              .join("role")
                              .get("name")
              ),
              roleName.trim().toUpperCase()
      );
    };
  }

}
