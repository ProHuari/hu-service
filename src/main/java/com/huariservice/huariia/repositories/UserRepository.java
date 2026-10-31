package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link User} accounts.
 *
 * <p>Inherits standard CRUD, paging and sorting operations from {@link JpaRepository}.
 * Query methods are derived automatically by Spring Data from their names.</p>
 *
 * <p>Note: there is intentionally no {@code findByEmailAndPassword} method. Passwords are stored
 * as salted BCrypt hashes, so they cannot be matched in SQL; the service loads the user by e-mail
 * and verifies the password with {@code PasswordEncoder.matches(...)}.</p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by login e-mail. Used during authentication.
     *
     * @param email the normalized (trimmed, lower-case) e-mail
     * @return the matching user, or {@link Optional#empty()} if none exists
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether an e-mail is already registered. Used to reject duplicate sign-ups
     * and profile updates with a clear 409 error before hitting the unique constraint.
     *
     * @param email the normalized e-mail
     * @return {@code true} if a user with this e-mail exists
     */
    boolean existsByEmail(String email);
}
