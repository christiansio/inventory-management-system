package com.personal.ims.auth.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.personal.ims.auth.entity.AuthSession;
import com.personal.ims.auth.repository.AuthSessionRepository;

import jakarta.transaction.Transactional;

@Service
public class AuthSessionService {

    private final AuthSessionRepository repository;
    private final long authSessionExp;

    public AuthSessionService(
            AuthSessionRepository repository,
            @Value("${auth.session.expiration}") long authSessionExp) {
        this.repository = repository;
        this.authSessionExp = authSessionExp;
    }

    @Transactional
    public Long createAuthSession(Long userId, Instant loggedInTime) {
        AuthSession session = new AuthSession();

        session.setUserId(userId);

        // use the logged in time + session expiry to set expiresAt field
        session.setExpiresAt(loggedInTime.plusMillis(authSessionExp));

        // after setting the values save and flush so the id will be generated
        // immediately and be
        // returned by the service
        AuthSession saved = repository.saveAndFlush(session);

        return saved.getId();
    }

    public boolean isSessionValid(long sessionId, long userId) {

        // validate session by checking if it is not revoked and current time is before
        // expiry
        return repository.findByIdAndUserId(sessionId, userId)
                .filter(session -> session.isValidAtNow(Instant.now()))
                .isPresent();
    }

}
