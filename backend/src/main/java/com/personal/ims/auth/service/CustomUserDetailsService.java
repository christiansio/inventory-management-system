package com.personal.ims.auth.service;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.personal.ims.auth.entity.User;
import com.personal.ims.auth.repository.RolePermissionRepository;
import com.personal.ims.auth.repository.UserRepository;
import com.personal.ims.auth.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    private final RolePermissionRepository rolePermissionRepository;

    // Adapt the User entity to Spring Security's UserDetails principal,
    // including the user's ID and granted authorities.
    @Override
    public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // get the permissions from the role of the current user
        // get the permission code name of each role permission and
        // convert into Spring security authority
        // by using .map
        Collection<? extends GrantedAuthority> authorities = rolePermissionRepository.findByRole(user.getRole())
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getCode().name()))
                .toList();

        return new CustomUserDetails(user, authorities);

    }
}
