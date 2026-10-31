package com.huariservice.huariia.security;

import com.huariservice.huariia.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

/**
 * Issues and validates HMAC-signed JWTs.
 * The token subject is the user id; the {@code email} and {@code role} claims
 * carry the identity.
 */
@Service
public class TokenService {

    private static final String EMAIL_CLAIM = "email";
    private static final String ROLE_CLAIM = "role";

    private final SecretKey key;
    private final long expirationMinutes;

    /**
     * Creates the token service.
     *
     * @param secret            HMAC secret (at least 32 bytes), from
     *                          {@code app.jwt.secret}
     * @param expirationMinutes token lifetime in minutes, from
     *                          {@code app.jwt.expiration-minutes} (default 120)
     */
    public TokenService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-minutes:120}") long expirationMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
    }

    /**
     * Generates a signed JWT for the given user.
     *
     * @param user the authenticated user
     * @return the compact, signed JWT
     */
    public String generateToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim(EMAIL_CLAIM, user.getEmail())
                .claim(ROLE_CLAIM, user.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES)))
                .signWith(key)
                .compact();
    }

    /**
     * Validates a JWT (signature and expiration) and extracts the user identity.
     *
     * @param token the compact JWT
     * @return the authenticated user, or empty if the token is invalid, tampered
     *         with or expired
     */
    public Optional<AuthenticatedUser> validate(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return Optional.of(new AuthenticatedUser(
                    Long.valueOf(claims.getSubject()),
                    claims.get(EMAIL_CLAIM, String.class),
                    claims.get(ROLE_CLAIM, String.class)));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
