package com.huariservice.huariia.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Credentials sent to the authentication endpoint to obtain a JWT.
 *
 * @param email    registered e-mail (required, valid format)
 * @param password plain-text password, compared against the stored BCrypt hash (required)
 */
public record LoginRequest(
        @NotBlank(message = "E-mail is required")
        @Email(message = "Invalid e-mail")
        String email,

        @NotBlank(message = "Password is required")
        String password
) {}