package com.personal.ims.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.personal.ims.auth.entity.AuthSession;

public interface AuthSessionRepository extends JpaRepository<AuthSession, Long> {

    Optional<AuthSession> findByIdAndUserId(Long sessionId, Long userId);
}
