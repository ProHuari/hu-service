package com.huariservice.huariia.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for refreshing an access token.
 *
 * @param refreshToken the issued refresh token
 */
public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token is required.")
        String refreshToken
) {}
