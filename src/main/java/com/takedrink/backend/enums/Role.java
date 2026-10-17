package com.takedrink.backend.enums;

public enum Role {
  CUSTOMER,
  STAFF,
  MANAGER;

  public String getAuthority() {
    return "ROLE_" + this.name();
  }
}