package com.personal.ims.auth.service;

import java.time.Instant;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.personal.ims.auth.dto.request.LoginRequestDto;
import com.personal.ims.auth.dto.response.CreateSessionResponseDto;
import com.personal.ims.auth.dto.response.LoginResponseDto;
import com.personal.ims.auth.repository.RoleRepository;
import com.personal.ims.auth.repository.UserRepository;
import com.personal.ims.auth.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthSessionService authSessionService;

    public LoginResponseDto login(LoginRequestDto request) {
        Instant now = Instant.now();

        // authenticate user using configured authentication manager
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                request.email(), request.password());

        // get userDetails from authenticated user
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long currentUserId = userDetails.getUserId();

        // create a session for the logged in user
        Long sessionId = authSessionService.createAuthSession(currentUserId, now);

        // create token using user id and session id
        String token = jwtService.createToken(currentUserId, sessionId);

        return new LoginResponseDto(token);
    }

}
