package com.huariservice.huariia.DTOs;

/**
 * Response returned after a successful login.
 *
 * @param token  signed JWT to be sent in the {@code Authorization} header
 * @param type   token type, always {@code "Bearer"}
 * @param userId id of the authenticated user
 * @param role   role name of the authenticated user (e.g. {@code ADMIN}, {@code STUDENT})
 */
public record LoginResponse(String token, String type, Long userId, String role) {

    /**
     * Factory that builds a Bearer login response.
     *
     * @param token  the generated JWT
     * @param userId id of the authenticated user
     * @param role   role name of the authenticated user
     * @return a new {@code LoginResponse} with {@code type = "Bearer"}
     */
    public static LoginResponse of(String token, Long userId, String role) {
        return new LoginResponse(token, "Bearer", userId, role);
    }
}
