package com.huariservice.huariia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Incoming payload used to register a new user account.
 *
 * @param name     display name (required, max 100 chars)
 * @param email    unique login e-mail (required, valid format, max 100 chars)
 * @param password plain-text password, hashed with BCrypt before storage
 *                 (required, 6–72 chars; BCrypt only considers the first 72 bytes)
 */
public record UserRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must be at most 100 characters")
        String name,

        @NotBlank(message = "E-mail is required")
        @Email(message = "Invalid e-mail")
        @Size(max = 100, message = "E-mail must be at most 100 characters")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 72, message = "Password must be between 6 and 72 characters")
        String password
) {}
