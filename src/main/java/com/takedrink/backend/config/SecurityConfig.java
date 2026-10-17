package com.takedrink.backend.config;

import tools.jackson.databind.ObjectMapper;
import com.takedrink.backend.dto.ApiResponse;
import com.takedrink.backend.security.JwtAuthenticationFilter;
import com.takedrink.backend.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(
          HttpSecurity http,
          JwtService jwtService,
          UserDetailsService userDetailsService,
          ObjectMapper objectMapper
  ) throws Exception {

    JwtAuthenticationFilter jwtFilter =
            new JwtAuthenticationFilter(
                    jwtService,
                    userDetailsService
            );

    AuthenticationEntryPoint authenticationEntryPoint =
            (request, response, exception) ->
                    writeSecurityError(
                            response,
                            objectMapper,
                            HttpServletResponse.SC_UNAUTHORIZED,
                            "Authentication required"
                    );

    AccessDeniedHandler accessDeniedHandler =
            (request, response, exception) ->
                    writeSecurityError(
                            response,
                            objectMapper,
                            HttpServletResponse.SC_FORBIDDEN,
                            "You do not have permission to access this resource"
                    );

    http
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )

            .exceptionHandling(exception -> exception
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler)
            )

            .authorizeHttpRequests(auth -> auth

                    // Public: đăng ký và đăng nhập
                    .requestMatchers(
                            HttpMethod.POST,
                            "/api/auth/register",
                            "/api/auth/login"
                    ).permitAll()

                    // Public: khách xem menu sản phẩm
                    .requestMatchers(
                            HttpMethod.GET,
                            "/api/products",
                            "/api/products/**"
                    ).permitAll()

                    // Public: xem danh mục
                    .requestMatchers(
                            HttpMethod.GET,
                            "/api/categories",
                            "/api/categories/**"
                    ).permitAll()

                    // Chỉ MANAGER được tạo, sửa, xóa sản phẩm
                    .requestMatchers(
                            HttpMethod.POST,
                            "/api/products",
                            "/api/products/**"
                    ).hasRole("MANAGER")

                    .requestMatchers(
                            HttpMethod.PUT,
                            "/api/products",
                            "/api/products/**"
                    ).hasRole("MANAGER")

                    .requestMatchers(
                            HttpMethod.PATCH,
                            "/api/products",
                            "/api/products/**"
                    ).hasRole("MANAGER")

                    .requestMatchers(
                            HttpMethod.DELETE,
                            "/api/products",
                            "/api/products/**"
                    ).hasRole("MANAGER")

                    // Các API còn lại phải đăng nhập
                    .anyRequest().authenticated()
            )

            .addFilterBefore(
                    jwtFilter,
                    UsernamePasswordAuthenticationFilter.class
            );

    return http.build();
  }

  private void writeSecurityError(
          HttpServletResponse response,
          ObjectMapper objectMapper,
          int status,
          String message
  ) throws IOException {

    response.setStatus(status);
    response.setCharacterEncoding(
            StandardCharsets.UTF_8.name()
    );
    response.setContentType(
            MediaType.APPLICATION_JSON_VALUE
    );

    objectMapper.writeValue(
            response.getOutputStream(),
            ApiResponse.error(message)
    );
  }
}