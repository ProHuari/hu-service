package com.huariservice.huariia.dto;

/**
 * Response returned when requesting a password reset token.
 *
 * @param message    informational message
 * @param resetToken short-lived reset token
 */
public record ForgotPasswordResponse(
        String message,
        String resetToken
) {}
