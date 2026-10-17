package com.takedrink.backend.controller;

import com.takedrink.backend.dto.ApiResponse;
import com.takedrink.backend.dto.auth.AuthResponse;
import com.takedrink.backend.dto.auth.LoginRequest;
import com.takedrink.backend.dto.auth.RegisterRequest;
import com.takedrink.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  // 1. Đăng ký tài khoản
  @PostMapping("/register")
  public ResponseEntity<ApiResponse<Void>> register(
          @Valid @RequestBody RegisterRequest request
  ) {
    authService.register(request);

    ApiResponse<Void> response = ApiResponse.success(
            "Registration successful",
            null
    );

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
  }

  // 2. Đăng nhập
  @PostMapping("/login")
  public ResponseEntity<ApiResponse<AuthResponse>> login(
          @Valid @RequestBody LoginRequest request
  ) {
    AuthResponse authResponse = authService.login(request);

    ApiResponse<AuthResponse> response = ApiResponse.success(
            "Login successful",
            authResponse
    );

    return ResponseEntity.ok(response);
  }
}