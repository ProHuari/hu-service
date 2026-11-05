package com.huariservice.huariia.security;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory blacklist for revoked JWT tokens (e.g., after user logout).
 * Automatically prunes expired tokens on check/insert.
 */
@Service
public class TokenBlacklistService {

    private final Map<String, Instant> revokedTokens = new ConcurrentHashMap<>();

    public void revokeToken(String token, Instant expiresAt) {
        if (token == null || token.isBlank()) {
            return;
        }
        Instant expiry = (expiresAt != null) ? expiresAt : Instant.now().plusSeconds(7200);
        revokedTokens.put(token, expiry);
        cleanupExpired();
    }

    public boolean isRevoked(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        Instant expiry = revokedTokens.get(token);
        if (expiry == null) {
            return false;
        }
        if (Instant.now().isAfter(expiry)) {
            revokedTokens.remove(token);
            return false;
        }
        return true;
    }

    private void cleanupExpired() {
        Instant now = Instant.now();
        revokedTokens.entrySet().removeIf(entry -> now.isAfter(entry.getValue()));
    }
}

