package com.huariservice.huariia.dto;

import com.huariservice.huariia.entities.User;
import com.huariservice.huariia.entities.enums.Role;

/**
 * Outgoing representation of a {@link User}. The password hash is intentionally omitted.
 *
 * @param id    database identifier
 * @param name  display name
 * @param email login e-mail
 * @param role  authorization role
 */
public record UserResponse(
        Long id,
        String name,
        String email,
        Role role
) {

    /**
     * Converts a {@link User} entity into its API representation, dropping sensitive fields.
     *
     * @param user the entity to convert (must not be {@code null})
     * @return a new immutable {@code UserResponse}
     */
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
