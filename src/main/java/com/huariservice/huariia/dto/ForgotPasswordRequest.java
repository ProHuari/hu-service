package com.huariservice.huariia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request payload to initiate the password recovery flow.
 *
 * @param email user account e-mail
 */
public record ForgotPasswordRequest(
        @NotBlank(message = "E-mail is required.")
        @Email(message = "Invalid e-mail format.")
        String email
) {}

