package com.personal.ims.auth.service;

import java.time.Instant;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.personal.ims.auth.dto.request.CreateUserRequestDto;
import com.personal.ims.auth.dto.request.LoginRequestDto;
import com.personal.ims.auth.dto.response.CreateUserResponseDto;
import com.personal.ims.auth.dto.response.LoginResponseDto;
import com.personal.ims.auth.entity.Location;
import com.personal.ims.auth.entity.Role;
import com.personal.ims.auth.entity.User;
import com.personal.ims.auth.repository.LocationRepository;
import com.personal.ims.auth.repository.RoleRepository;
import com.personal.ims.auth.repository.UserRepository;
import com.personal.ims.auth.security.CustomUserDetails;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final LocationRepository locationRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthSessionService authSessionService;

    @Transactional
    public LoginResponseDto login(LoginRequestDto request) {

        // authenticate user using configured authentication manager
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.email(), request.password()));
        Instant now = Instant.now();

        // get userDetails from authenticated user
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long currentUserId = userDetails.getUserId();

        // create a session for the logged in user
        Long sessionId = authSessionService.createAuthSession(currentUserId, now);

        // create token using user id and session id
        String token = jwtService.createToken(currentUserId, sessionId);

        return new LoginResponseDto(token);
    }

    public CreateUserResponseDto createUser(CreateUserRequestDto request) {

        User user = new User();
        Location location = locationRepository.findById(request.locationId()).orElseThrow();
        Role role = roleRepository.findById(request.roleId()).orElseThrow();
        user.setEmail(request.email().trim().toLowerCase());

        user.setRole(role);

        user.setLocation(location);

        User saved = userRepository.save(user);

        return new CreateUserResponseDto(saved.getId());

    }

}
