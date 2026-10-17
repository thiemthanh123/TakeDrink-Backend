
package com.takedrink.backend.config;

import com.takedrink.backend.dto.ApiResponse;
import com.takedrink.backend.enums.Role;
import com.takedrink.backend.security.JwtAuthenticationFilter;
import com.takedrink.backend.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(
          HttpSecurity http,
          JwtService jwtService,
          UserDetailsService userDetailsService,
          ObjectMapper objectMapper,
          @Qualifier("publicPostPaths") List<String> publicPostPaths,
          @Qualifier("publicGetPaths") List<String> publicGetPaths,
          @Qualifier("managerPostPaths") List<String> managerPostPaths,
          @Qualifier("managerPutPaths") List<String> managerPutPaths,
          @Qualifier("managerPatchPaths") List<String> managerPatchPaths,
          @Qualifier("managerDeletePaths") List<String> managerDeletePaths
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

                    // 1. All manager APIs: MANAGER only
                    .requestMatchers("/api/manager/**")
                    .hasRole(Role.MANAGER.name())

                    // 2. Public: create orders
                    .requestMatchers(
                            HttpMethod.POST,
                            "/api/orders"
                    ).permitAll()

                    // 3. Public: register and login
                    .requestMatchers(
                            HttpMethod.POST,
                            publicPostPaths.toArray(String[]::new)
                    ).permitAll()

                    // 4. Public: view products and categories
                    .requestMatchers(
                            HttpMethod.GET,
                            publicGetPaths.toArray(String[]::new)
                    ).permitAll()

                    // 5. CUSTOMER: view own orders
                    .requestMatchers(
                            HttpMethod.GET,
                            "/api/orders/my-orders"
                    ).hasRole(Role.CUSTOMER.name())

                    // 6. STAFF and MANAGER: view orders
                    .requestMatchers(
                            HttpMethod.GET,
                            "/api/orders"
                    ).hasAnyRole(
                            Role.STAFF.name(),
                            Role.MANAGER.name()
                    )

                    // 7. STAFF and MANAGER: update order status
                    .requestMatchers(
                            HttpMethod.PATCH,
                            "/api/orders/*/status"
                    ).hasAnyRole(
                            Role.STAFF.name(),
                            Role.MANAGER.name()
                    )

                    // 8. CUSTOMER: cancel own order
                    .requestMatchers(
                            HttpMethod.PATCH,
                            "/api/orders/*/cancel"
                    ).hasRole(Role.CUSTOMER.name())

                    // 9. CUSTOMER: manage addresses
                    .requestMatchers(
                            "/api/addresses",
                            "/api/addresses/**"
                    ).hasRole(Role.CUSTOMER.name())

                    // 10. MANAGER: create products
                    .requestMatchers(
                            HttpMethod.POST,
                            managerPostPaths.toArray(String[]::new)
                    ).hasRole(Role.MANAGER.name())

                    // 11. MANAGER: update products
                    .requestMatchers(
                            HttpMethod.PUT,
                            managerPutPaths.toArray(String[]::new)
                    ).hasRole(Role.MANAGER.name())

                    // 12. MANAGER: partially update products
                    .requestMatchers(
                            HttpMethod.PATCH,
                            managerPatchPaths.toArray(String[]::new)
                    ).hasRole(Role.MANAGER.name())

                    // 13. MANAGER: delete products
                    .requestMatchers(
                            HttpMethod.DELETE,
                            managerDeletePaths.toArray(String[]::new)
                    ).hasRole(Role.MANAGER.name())

                    // 14. All remaining APIs require authentication
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