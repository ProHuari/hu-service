package com.huariservice.huariia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload to complete a password reset using a short-lived token.
 *
 * @param token       short-lived reset token
 * @param newPassword new plain-text password (between 8 and 72 characters)
 */
public record ResetPasswordRequest(
        @NotBlank(message = "Reset token is required.")
        String token,
        @NotBlank(message = "New password is required.")
        @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters.")
        String newPassword
) {}
