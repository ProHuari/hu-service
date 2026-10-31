package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Data access for {@link User} entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by e-mail (used during login).
     *
     * @param email the normalized (trimmed, lower-case) e-mail
     * @return the matching user, or empty if none exists
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether an e-mail is already registered (prevents duplicate sign-ups).
     *
     * @param email the normalized (trimmed, lower-case) e-mail
     * @return {@code true} if a user with this e-mail exists
     */
    boolean existsByEmail(String email);
}
