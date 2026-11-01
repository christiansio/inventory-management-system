package com.personal.ims.auth.entity;

import java.time.Instant;

import com.personal.ims.core.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AuthSession extends BaseEntity {

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant revokedAt;

    public boolean isValidAtNow(Instant now) {
        return revokedAt == null && expiresAt.isAfter(now);
    }

}
