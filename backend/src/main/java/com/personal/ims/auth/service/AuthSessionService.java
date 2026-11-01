package com.personal.ims.auth.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.personal.ims.auth.repository.AuthSessionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthSessionService {

    private final AuthSessionRepository repository;

    // public CreateSessionResponseDto createAuthSession(CreateSessionRequestDto
    // request) {

    // }

    public boolean isSessionValid(long sessionId, long userId) {
        return repository.findByIdAndUserId(sessionId, userId)
                .filter(session -> session.isValidAtNow(Instant.now()))
                .isPresent();
    }

}
