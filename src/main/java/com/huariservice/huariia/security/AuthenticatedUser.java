package com.huariservice.huariia.security;

/**
 * Identity of the authenticated user, extracted from a valid JWT and exposed
 * as the Spring Security principal (inject it with {@code @AuthenticationPrincipal}).
 *
 * @param userId id of the authenticated user (token subject)
 * @param email  e-mail of the authenticated user
 * @param role   role name of the authenticated user (e.g. {@code ADMIN}, {@code STUDENT})
 */
public record AuthenticatedUser(Long userId, String email, String role) {

    /**
     * Tells whether the authenticated user has the ADMIN role.
     *
     * @return {@code true} if the role is {@code ADMIN}
     */
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}
