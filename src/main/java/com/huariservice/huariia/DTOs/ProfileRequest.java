package com.huariservice.huariia.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Incoming payload used by the authenticated user to edit their own profile.
 *
 * <p>Only name and e-mail can be changed here; changing the password has its own endpoint
 * (see {@link ChangePasswordRequest}).</p>
 *
 * @param name  new display name (required, max 100 chars)
 * @param email new login e-mail (required, valid format, max 100 chars, must stay unique)
 */
public record ProfileRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must be at most 100 characters")
        String name,

        @NotBlank(message = "E-mail is required")
        @Email(message = "Invalid e-mail")
        @Size(max = 100, message = "E-mail must be at most 100 characters")
        String email
) {}