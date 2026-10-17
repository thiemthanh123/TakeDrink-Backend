package com.takedrink.backend.service;

import com.takedrink.backend.dto.auth.AuthResponse;
import com.takedrink.backend.dto.auth.LoginRequest;
import com.takedrink.backend.dto.auth.RegisterRequest;

public interface AuthService {

  void register(RegisterRequest request);

  AuthResponse login(LoginRequest request);
}