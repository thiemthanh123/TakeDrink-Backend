package com.takedrink.backend.service.impl;

import com.takedrink.backend.dto.auth.AuthResponse;
import com.takedrink.backend.dto.auth.LoginRequest;
import com.takedrink.backend.dto.auth.RegisterRequest;
import com.takedrink.backend.entity.Role;
import com.takedrink.backend.entity.User;
import com.takedrink.backend.entity.UserRole;
import com.takedrink.backend.exception.BadRequestException;
import com.takedrink.backend.exception.UnauthorizedException;
import com.takedrink.backend.repository.RoleRepository;
import com.takedrink.backend.repository.UserRepository;
import com.takedrink.backend.repository.UserRoleRepository;
import com.takedrink.backend.security.CustomUserDetailsService;
import com.takedrink.backend.security.JwtService;
import com.takedrink.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final UserRoleRepository userRoleRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final CustomUserDetailsService customUserDetailsService;

  // Đăng ký tài khoản
  @Override
  @Transactional
  public void register(RegisterRequest request) {

    // 1. Chuẩn hóa email
    String email = request.email()
            .trim()
            .toLowerCase(Locale.ROOT);

    // 2. Kiểm tra email đã tồn tại
    if (userRepository.existsByEmailIgnoreCase(email)) {
      throw new BadRequestException("Email already exists");
    }

    // 3. Tìm role CUSTOMER
    Role customerRole = roleRepository
            .findByNameIgnoreCase("CUSTOMER")
            .orElseThrow(() ->
                    new IllegalStateException(
                            "Role CUSTOMER does not exist"
                    )
            );

    // 4. Tạo User
    User user = new User();
    user.setFullName(request.fullName().trim());
    user.setEmail(email);

    // Mã hóa mật khẩu trước khi lưu
    user.setPasswordHash(
            passwordEncoder.encode(request.password())
    );

    // Chuẩn hóa số điện thoại
    user.setPhone(
            request.phone() == null || request.phone().isBlank()
                    ? null
                    : request.phone().trim()
    );

    // 5. Lưu User
    User savedUser = userRepository.save(user);

    // 6. Tạo liên kết User - Role
    UserRole userRole = new UserRole();
    userRole.setUser(savedUser);
    userRole.setRole(customerRole);
    userRole.setAssignedAt(Instant.now());

    // 7. Lưu liên kết User - Role
    userRoleRepository.save(userRole);
  }

  // Đăng nhập
  @Override
  @Transactional(readOnly = true)
  public AuthResponse login(LoginRequest request) {

    // 1. Chuẩn hóa email
    String email = request.email()
            .trim()
            .toLowerCase(Locale.ROOT);

    // 2. Tìm tài khoản
    User user = userRepository
            .findByEmailIgnoreCase(email)
            .orElseThrow(() ->
                    new UnauthorizedException(
                            "Invalid email or password"
                    )
            );

    // 3. Kiểm tra mật khẩu
    if (!passwordEncoder.matches(
            request.password(),
            user.getPasswordHash())) {

      throw new UnauthorizedException(
              "Invalid email or password"
      );
    }

    // 4. Kiểm tra trạng thái tài khoản
    if (!Boolean.TRUE.equals(user.getActive())) {
      throw new UnauthorizedException(
              "Account is disabled"
      );
    }

    // 5. Tải thông tin xác thực và quyền từ database
    UserDetails userDetails =
            customUserDetailsService.loadUserByUsername(email);

    // 6. Tạo JWT access token
    String token = jwtService.generateToken(userDetails);

    // 7. Trả kết quả đăng nhập
    return new AuthResponse(
            token,
            "Bearer",
            900
    );
  }
}