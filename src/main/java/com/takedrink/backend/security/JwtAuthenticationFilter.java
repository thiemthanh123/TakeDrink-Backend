
package com.takedrink.backend.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger log =
          LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  public JwtAuthenticationFilter(
          JwtService jwtService,
          UserDetailsService userDetailsService
  ) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(
          HttpServletRequest request,
          HttpServletResponse response,
          FilterChain filterChain
  ) throws ServletException, IOException {

    String method = request.getMethod();
    String uri = request.getRequestURI();

    log.debug("[JWT] Processing request: {} {}", method, uri);

    String authorizationHeader =
            request.getHeader("Authorization");

    // 1. Không gửi Authorization header
    if (authorizationHeader == null) {
      log.debug("[JWT] Authorization header is missing");
      filterChain.doFilter(request, response);
      return;
    }

    // 2. Không sử dụng Bearer authentication
    if (!authorizationHeader.startsWith("Bearer ")) {
      log.debug("[JWT] Authorization scheme is not Bearer");
      filterChain.doFilter(request, response);
      return;
    }

    // 3. Lấy JWT
    String token = authorizationHeader.substring(7).trim();

    if (token.isEmpty()) {
      log.debug("[JWT] Token is empty");
      SecurityContextHolder.clearContext();
      filterChain.doFilter(request, response);
      return;
    }

    try {
      // 4. Đọc email từ JWT
      String email = jwtService.extractUsername(token);

      log.debug("[JWT] Token parsed successfully");

      // Chỉ xác thực nếu request chưa có Authentication
      if (email != null
              && SecurityContextHolder.getContext()
              .getAuthentication() == null) {

        // 5. Tải thông tin tài khoản hiện tại từ database
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(email);

        // 6. Kiểm tra JWT hợp lệ
        if (!jwtService.isTokenValid(token, userDetails)) {
          log.debug("[JWT] Token validation failed");
          SecurityContextHolder.clearContext();

          // 7. JWT hợp lệ nhưng tài khoản đã bị khóa
        } else if (!userDetails.isEnabled()) {
          log.debug("[JWT] Account is disabled");
          SecurityContextHolder.clearContext();

          writeUnauthorizedResponse(response);
          return;

          // 8. JWT hợp lệ và tài khoản đang hoạt động
        } else {
          UsernamePasswordAuthenticationToken authentication =
                  new UsernamePasswordAuthenticationToken(
                          userDetails,
                          null,
                          userDetails.getAuthorities()
                  );

          authentication.setDetails(
                  new WebAuthenticationDetailsSource()
                          .buildDetails(request)
          );

          SecurityContextHolder.getContext()
                  .setAuthentication(authentication);

          log.debug(
                  "[JWT] Authentication successful; authorities={}",
                  userDetails.getAuthorities()
          );
        }
      }

    } catch (ExpiredJwtException exception) {
      log.debug("[JWT] Token expired");
      SecurityContextHolder.clearContext();

    } catch (UsernameNotFoundException exception) {
      log.debug("[JWT] User associated with token was not found");
      SecurityContextHolder.clearContext();

    } catch (JwtException | IllegalArgumentException exception) {
      log.debug(
              "[JWT] Token is invalid or cannot be parsed: {}",
              exception.getClass().getSimpleName()
      );
      SecurityContextHolder.clearContext();
    }

    // Spring Security tiếp tục quyết định quyền truy cập
    filterChain.doFilter(request, response);
  }

  /**
   * Trả về HTTP 401 khi tài khoản đã bị vô hiệu hóa.
   * Không sử dụng ObjectMapper.
   */
  private void writeUnauthorizedResponse(
          HttpServletResponse response
  ) throws IOException {

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    String timestamp = Instant.now().toString();

    response.getWriter().write("""
                {
                    "success": false,
                    "message": "Account is disabled",
                    "data": null,
                    "errors": null,
                    "timestamp": "%s"
                }
                """.formatted(timestamp));
  }
}
