package com.personal.ims.auth.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey key;
    private final long tokenValidity;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long tokenValidity) {

        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.tokenValidity = tokenValidity;

    }

    public String createToken(Long userId, UUID sessionId) {

        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + tokenValidity);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("sid", sessionId)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    public Long getUserId(Claims claims) {
        return Long.valueOf(claims.getSubject());
    }

    public Long getSessionId(Claims claims) {
        return claims.get("sid", Long.class);
    }

    public boolean isTokenValid(
            Claims claims,
            Long userId) {

        return userId != null
                && Long.valueOf(claims.getSubject())
                        .equals(userId)
                && claims.get("sid", Long.class) != null;
    }

    public Claims extractClaims(String token) {

        return Jwts.parser() // build a parser using JJWT's API
                .verifyWith(key) // parser verifies token signature using key
                .build() // creates the configured parser
                .parseSignedClaims(token) // verify, checks expiry and parse a signed JWT containing claims
                .getPayload(); // returns the payload as a claims object
    }
}
