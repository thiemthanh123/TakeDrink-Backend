
package com.takedrink.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PathsConfig {

  // Public: register and login
  @Bean(name = "publicPostPaths")
  public List<String> publicPostPaths() {
    return List.of(
            "/api/auth/register",
            "/api/auth/login"
    );
  }

  // Public: view menu
  @Bean(name = "publicGetPaths")
  public List<String> publicGetPaths() {
    return List.of(
            "/api/products",
            "/api/products/**",
            "/api/categories",
            "/api/categories/**"
    );
  }

  // MANAGER: create products
  @Bean(name = "managerPostPaths")
  public List<String> managerPostPaths() {
    return List.of(
            "/api/products",
            "/api/products/**"
    );
  }

  // MANAGER: update products using PUT
  @Bean(name = "managerPutPaths")
  public List<String> managerPutPaths() {
    return List.of(
            "/api/products",
            "/api/products/**"
    );
  }

  // MANAGER: partially update products using PATCH
  @Bean(name = "managerPatchPaths")
  public List<String> managerPatchPaths() {
    return List.of(
            "/api/products",
            "/api/products/**"
    );
  }

  // MANAGER: delete products
  @Bean(name = "managerDeletePaths")
  public List<String> managerDeletePaths() {
    return List.of(
            "/api/products",
            "/api/products/**"
    );
  }
}
