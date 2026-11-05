package com.huariservice.huariia.services;

import com.huariservice.huariia.dto.ForgotPasswordRequest;
import com.huariservice.huariia.dto.ForgotPasswordResponse;
import com.huariservice.huariia.dto.ResetPasswordRequest;
import com.huariservice.huariia.entities.User;
import com.huariservice.huariia.exceptions.BusinessRuleException;
import com.huariservice.huariia.exceptions.ResourceNotFoundException;
import com.huariservice.huariia.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service handling the "Forgot My Password" flow using short-lived (15-minute) single-use tokens.
 */
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final Duration TOKEN_VALIDITY = Duration.ofMinutes(15);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private record ResetTokenEntry(Long userId, Instant expiresAt) {}

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Map<String, ResetTokenEntry> activeTokens = new ConcurrentHashMap<>();

    @Transactional(readOnly = true)
    public ForgotPasswordResponse requestPasswordReset(ForgotPasswordRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase(Locale.ROOT);
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for the given e-mail"));

        String resetToken = generateShortToken();
        activeTokens.put(resetToken, new ResetTokenEntry(user.getId(), Instant.now().plus(TOKEN_VALIDITY)));

        return new ForgotPasswordResponse(
                "Password reset token generated successfully (valid for 15 minutes).",
                resetToken
        );
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String token = request.token().trim();
        ResetTokenEntry entry = activeTokens.remove(token);

        if (entry == null || Instant.now().isAfter(entry.expiresAt())) {
            throw new BusinessRuleException("Invalid or expired password reset token");
        }

        User user = userRepository.findById(entry.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    private String generateShortToken() {
        byte[] bytes = new byte[16];
        SECURE_RANDOM.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }
}

