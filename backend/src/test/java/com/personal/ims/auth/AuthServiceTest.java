package com.personal.ims.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;

import com.personal.ims.auth.dto.request.LoginRequestDto;
import com.personal.ims.auth.repository.AuthSessionRepository;
import com.personal.ims.auth.repository.UserRepository;
import com.personal.ims.auth.service.AuthService;
import com.personal.ims.auth.service.JwtService;

import jakarta.transaction.Transactional;

@ActiveProfiles("test")
@SpringBootTest(args = "--seed-admin")
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthSessionRepository authSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Value("${ADMIN_EMAIL}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    @Value("${auth.session.expiration}")
    private long authSessionExpiration;

    @Test
    @Transactional
    void loginCreatesSessionAndSignedJwtForAuthenticatedUser() {
        var user = userRepository.findByEmail(adminEmail).orElseThrow();
        Instant startedAt = Instant.now();

        var response = authService.login(new LoginRequestDto(adminEmail, adminPassword));
        Instant finishedAt = Instant.now();

        var claims = jwtService.extractClaims(response.token());
        Long tokenUserId = jwtService.getUserId(claims);
        UUID tokenSessionId = jwtService.getSessionId(claims);
        var session = authSessionRepository.findById(tokenSessionId).orElseThrow();

        assertThat(claims.getSubject()).isEqualTo(user.getId().toString());
        assertThat(tokenUserId).isEqualTo(user.getId());

        // The sid is taken from the verified JWT, then resolved independently
        // through persistence; the session must belong to the authenticated user.
        assertThat(session.getId()).isEqualTo(tokenSessionId);
        assertThat(session.getUserId()).isEqualTo(user.getId());
        assertThat(session.getCreatedAt()).isNotNull();
        assertThat(session.getCreatedAt()).isBetween(startedAt, finishedAt);
        assertThat(session.getExpiresAt()).isBetween(
                startedAt.plusMillis(authSessionExpiration),
                finishedAt.plusMillis(authSessionExpiration));
        assertThat(session.getRevokedAt()).isNull();
    }

    @Test
    @Transactional
    void loginWithInvalidPasswordDoesNotCreateSession() {
        long sessionsBefore = authSessionRepository.count();

        assertThatThrownBy(() -> authService.login(new LoginRequestDto(adminEmail, "incorrect-password")))
                .isInstanceOf(BadCredentialsException.class);

        assertThat(authSessionRepository.count()).isEqualTo(sessionsBefore);
    }
}
