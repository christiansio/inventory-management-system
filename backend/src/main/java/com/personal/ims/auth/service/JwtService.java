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

    public UUID getSessionId(Claims claims) {
        // Json can't reliably write "UUIDs" and may be written as a "STRING"
        // so, we parse it as a STRING and then convert to UUID
        return UUID.fromString(claims.get("sid", String.class));
    }

    public boolean isTokenValid(
            Claims claims,
            Long userId) {

        return userId != null
                && getUserId(claims)
                        .equals(userId)
                && getSessionId(claims) != null;
    }

    public Claims extractClaims(String token) {

        return Jwts.parser() // build a parser using JJWT's API
                .verifyWith(key) // parser verifies token signature using key
                .build() // creates the configured parser
                .parseSignedClaims(token) // verify, checks expiry and parse a signed JWT containing claims
                .getPayload(); // returns the payload as a claims object
    }
}
