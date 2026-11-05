package com.huariservice.huariia.dto;

/**
 * Response returned after a successful login or token refresh.
 *
 * @param token        signed JWT access token to be sent in the {@code Authorization} header
 * @param refreshToken signed JWT refresh token used to obtain a new access token
 * @param type         token type, always {@code "Bearer"}
 * @param userId       id of the authenticated user
 * @param role         role name of the authenticated user (e.g. {@code ADMIN}, {@code STUDENT})
 */
public record LoginResponse(String token, String refreshToken, String type, Long userId, String role) {

    public LoginResponse(String token, String type, Long userId, String role) {
        this(token, null, type, userId, role);
    }

    public static LoginResponse of(String token, Long userId, String role) {
        return new LoginResponse(token, null, "Bearer", userId, role);
    }

    public static LoginResponse of(String token, String refreshToken, Long userId, String role) {
        return new LoginResponse(token, refreshToken, "Bearer", userId, role);
    }
}
