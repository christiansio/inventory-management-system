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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.personal.ims.feature.auth.dto.request.LoginRequestDto;
import com.personal.ims.feature.auth.entity.Location;
import com.personal.ims.feature.auth.entity.Role;
import com.personal.ims.feature.auth.entity.User;
import com.personal.ims.feature.auth.enums.RoleClassification;
import com.personal.ims.feature.auth.repository.AuthSessionRepository;
import com.personal.ims.feature.auth.repository.LocationRepository;
import com.personal.ims.feature.auth.repository.RoleRepository;
import com.personal.ims.feature.auth.repository.UserRepository;
import com.personal.ims.feature.auth.service.AuthService;
import com.personal.ims.feature.auth.service.JwtService;

import jakarta.transaction.Transactional;

@ActiveProfiles("test")
@SpringBootTest
class AuthServiceTest {

    private static final String TEST_EMAIL = "auth-service-test@example.com";
    private static final String TEST_PASSWORD = "test-password";

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthSessionRepository authSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Value("${auth.session.expiration}")
    private long authSessionExpiration;

    @Test
    @Transactional
    void loginCreatesSessionAndSignedJwtForAuthenticatedUser() {
        var user = createTestUser();
        Instant startedAt = Instant.now();

        var response = authService.login(new LoginRequestDto(TEST_EMAIL, TEST_PASSWORD));
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
        createTestUser();
        long sessionsBefore = authSessionRepository.count();

        assertThatThrownBy(() -> authService.login(new LoginRequestDto(TEST_EMAIL, "incorrect-password")))
                .isInstanceOf(BadCredentialsException.class);

        assertThat(authSessionRepository.count()).isEqualTo(sessionsBefore);
    }

    private User createTestUser() {
        Role role = new Role();
        role.setName("AUTH_TEST");
        role.setClassification(RoleClassification.STAFF);
        role.setDescription("Role for AuthServiceTest");
        role = roleRepository.save(role);

        Location location = new Location();
        location.setName("AuthServiceTest Location");
        location.setAddress("Test address");
        location.setIsActive(true);
        location = locationRepository.save(location);

        User user = new User();
        user.setFirstName("Auth");
        user.setLastName("Test");
        user.setEmail(TEST_EMAIL);
        user.setPasswordHash(passwordEncoder.encode(TEST_PASSWORD));
        user.setRole(role);
        user.setLocation(location);
        user.setIsActive(true);
        return userRepository.save(user);
    }
}
