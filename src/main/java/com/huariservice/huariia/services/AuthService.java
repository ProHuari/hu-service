package com.huariservice.huariia.services;

import com.huariservice.huariia.dto.LoginRequest;
import com.huariservice.huariia.dto.LoginResponse;
import com.huariservice.huariia.dto.RefreshTokenRequest;
import com.huariservice.huariia.entities.User;
import com.huariservice.huariia.exceptions.CredenciaisInvalidasException;
import com.huariservice.huariia.repositories.UserRepository;
import com.huariservice.huariia.security.LoginAttemptService;
import com.huariservice.huariia.security.TokenBlacklistService;
import com.huariservice.huariia.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;

/**
 * Authentication use case: validates credentials, enforces brute-force protection,
 * issues access/refresh JWTs, and handles token refresh and revocation (logout).
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final LoginAttemptService loginAttemptService;
    private final TokenBlacklistService tokenBlacklistService;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase(Locale.ROOT);

        if (loginAttemptService != null) {
            loginAttemptService.checkAllowed(normalizedEmail);
        }

        User user = userRepository
                .findByEmail(normalizedEmail)
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseGet(() -> {
                    if (loginAttemptService != null) {
                        loginAttemptService.recordFailedAttempt(normalizedEmail);
                    }
                    throw new CredenciaisInvalidasException();
                });

        if (loginAttemptService != null) {
            loginAttemptService.recordSuccess(normalizedEmail);
        }

        String token = tokenService.gerarToken(user);
        String refreshToken = tokenService.gerarRefreshToken(user);
        return LoginResponse.of(token, refreshToken, user.getId(), user.getRole().name());
    }

    @Transactional(readOnly = true)
    public LoginResponse refresh(RefreshTokenRequest request) {
        String rawRefreshToken = request.refreshToken().trim();

        if (tokenBlacklistService != null && tokenBlacklistService.isRevoked(rawRefreshToken)) {
            throw new CredenciaisInvalidasException();
        }

        Long userId = tokenService.validarRefreshToken(rawRefreshToken)
                .orElseThrow(CredenciaisInvalidasException::new);

        User user = userRepository.findById(userId)
                .orElseThrow(CredenciaisInvalidasException::new);

        if (tokenBlacklistService != null) {
            Instant expiry = tokenService.extrairExpiracao(rawRefreshToken).orElse(null);
            tokenBlacklistService.revokeToken(rawRefreshToken, expiry);
        }

        String newAccessToken = tokenService.gerarToken(user);
        String newRefreshToken = tokenService.gerarRefreshToken(user);
        return LoginResponse.of(newAccessToken, newRefreshToken, user.getId(), user.getRole().name());
    }

    public void logout(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return;
        }
        String token = authorizationHeader.substring(7).trim();
        if (tokenBlacklistService != null && !token.isEmpty()) {
            Instant expiry = tokenService.extrairExpiracao(token).orElse(null);
            tokenBlacklistService.revokeToken(token, expiry);
        }
    }
}
