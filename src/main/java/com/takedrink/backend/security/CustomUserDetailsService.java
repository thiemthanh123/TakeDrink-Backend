package com.takedrink.backend.security;

import com.takedrink.backend.entity.User;
import com.takedrink.backend.entity.UserRole;
import com.takedrink.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String email)
          throws UsernameNotFoundException {

    User user = userRepository
            .findWithRolesByEmailIgnoreCase(email.trim())
            .orElseThrow(() ->
                    new UsernameNotFoundException(
                            "User not found"
                    )
            );

    var authorities = user.getUserRoles()
            .stream()
            .map(UserRole::getRole)
            .map(role -> {
              String roleName = role.getName()
                      .trim()
                      .toUpperCase(Locale.ROOT);

              if (!roleName.startsWith("ROLE_")) {
                roleName = "ROLE_" + roleName;
              }

              return new SimpleGrantedAuthority(roleName);
            })
            .toList();

    return org.springframework.security.core.userdetails.User
            .withUsername(user.getEmail())
            .password(user.getPasswordHash())
            .authorities(authorities)
            .disabled(!Boolean.TRUE.equals(user.getActive()))
            .build();
  }
}