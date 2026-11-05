package com.huariservice.huariia.security;

import com.huariservice.huariia.exceptions.TooManyRequestsException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory rate limiter / brute-force protection service for authentication endpoints.
 * Blocks an identifier (e-mail or IP) after {@link #MAX_ATTEMPTS} consecutive failed login
 * attempts within {@link #LOCK_DURATION}.
 */
@Service
public class LoginAttemptService {

    public static final int MAX_ATTEMPTS = 5;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    private record AttemptRecord(int failures, Instant windowExpiresAt) {}

    private final Map<String, AttemptRecord> attempts = new ConcurrentHashMap<>();

    public void checkAllowed(String key) {
        if (key == null || key.isBlank()) {
            return;
        }
        String normalizedKey = normalize(key);
        AttemptRecord record = attempts.get(normalizedKey);
        if (record == null) {
            return;
        }
        if (Instant.now().isAfter(record.windowExpiresAt())) {
            attempts.remove(normalizedKey);
            return;
        }
        if (record.failures() >= MAX_ATTEMPTS) {
            throw new TooManyRequestsException(
                    "Muitas tentativas de login falharam. Conta temporariamente bloqueada por 15 minutos.");
        }
    }

    public void recordFailedAttempt(String key) {
        if (key == null || key.isBlank()) {
            return;
        }
        String normalizedKey = normalize(key);
        Instant now = Instant.now();
        attempts.compute(normalizedKey, (k, existing) -> {
            if (existing == null || now.isAfter(existing.windowExpiresAt())) {
                return new AttemptRecord(1, now.plus(LOCK_DURATION));
            }
            return new AttemptRecord(existing.failures() + 1, now.plus(LOCK_DURATION));
        });
    }

    public void recordSuccess(String key) {
        if (key == null || key.isBlank()) {
            return;
        }
        attempts.remove(normalize(key));
    }

    private String normalize(String key) {
        return key.trim().toLowerCase(Locale.ROOT);
    }
}

