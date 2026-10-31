package com.huariservice.huariia.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Incoming payload used by the authenticated user to change their password.
 *
 * <p>The current password is required to prove that the request comes from the account owner
 * (protects against hijacked sessions).</p>
 *
 * @param currentPassword the password currently in use (required)
 * @param newPassword     the new password (required, 6–72 chars; BCrypt only considers the first 72 bytes)
 */
public record ChangePasswordRequest(
        @NotBlank(message = "Current password is required")
        String currentPassword,

        @NotBlank(message = "New password is required")
        @Size(min = 6, max = 72, message = "New password must be between 6 and 72 characters")
        String newPassword
) {}