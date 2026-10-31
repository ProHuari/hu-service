package com.huariservice.huariia.services;

import com.huariservice.huariia.dto.ChangePasswordRequest;
import com.huariservice.huariia.dto.ProfileRequest;
import com.huariservice.huariia.dto.UserRequest;
import com.huariservice.huariia.dto.UserResponse;
import com.huariservice.huariia.entities.User;
import com.huariservice.huariia.entities.enums.Role;
import com.huariservice.huariia.exceptions.ConflitoException;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.exceptions.RegraNegocioException;
import com.huariservice.huariia.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

/**
 * Business logic for user accounts: registration, profile management and password changes.
 *
 * <p>Passwords are never stored in plain text: they are hashed with the configured
 * {@link PasswordEncoder} (BCrypt) before being saved. E-mails are normalized (trimmed and
 * lower-cased) so uniqueness checks are case-insensitive.</p>
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Public sign-up. The created account always receives the {@link Role#STUDENT} role,
     * so nobody can self-register as an administrator.
     *
     * @param request validated registration payload
     * @return the created user (without password data)
     * @throws ConflitoException if the e-mail is already registered
     */
    public UserResponse registerStudent(UserRequest request) {
        return createUser(request, Role.STUDENT);
    }

    /**
     * Creates an administrator account. Only reachable by admins; the access rule lives in the
     * security configuration, not here.
     *
     * @param request validated registration payload
     * @return the created user (without password data)
     * @throws ConflitoException if the e-mail is already registered
     */
    public UserResponse registerAdmin(UserRequest request) {
        return createUser(request, Role.ADMIN);
    }

    /**
     * Lists every user.
     *
     * @return all users (empty list if none)
     */
    @Transactional(readOnly = true)
    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream().map(UserResponse::from).toList();
    }

    /**
     * Retrieves a single user.
     *
     * @param id user identifier
     * @return the user as a response DTO
     * @throws RecursoNaoEncontradoException if no user has this id
     */
    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return UserResponse.from(findUserOrThrow(id));
    }

    /**
     * Administrator operation: changes the name and e-mail and resets the password of any user.
     * <ol>
     *   <li>Loads the user (404 if missing).</li>
     *   <li>Normalizes the new e-mail and checks for duplicates only if it changed.</li>
     *   <li>Updates the name and e-mail and stores the BCrypt hash of the new password.</li>
     * </ol>
     *
     * @param id      user identifier
     * @param request validated payload with the new values
     * @return the updated user
     * @throws RecursoNaoEncontradoException if no user has this id
     * @throws ConflitoException             if the e-mail belongs to another user
     */
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = findUserOrThrow(id);
        String email = normalizeEmail(request.email());

        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new ConflitoException("This e-mail is already registered");
        }

        user.setName(request.name());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        return UserResponse.from(userRepository.save(user));
    }

    /**
     * Self-service operation (the id comes from the authenticated token): changes only the
     * name and the e-mail of the caller's own account.
     *
     * @param id      id of the authenticated user
     * @param request validated payload with the new name and e-mail
     * @return the updated user
     * @throws RecursoNaoEncontradoException if the user no longer exists
     * @throws ConflitoException             if the e-mail belongs to another user
     */
    public UserResponse updateProfile(Long id, ProfileRequest request) {
        User user = findUserOrThrow(id);
        String email = normalizeEmail(request.email());

        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new ConflitoException("This e-mail is already registered");
        }

        user.setName(request.name());
        user.setEmail(email);
        return UserResponse.from(userRepository.save(user));
    }

    /**
     * Changes the caller's password. The current password is required so that someone holding a
     * borrowed token cannot take over the account.
     * <ol>
     *   <li>Verifies the current password against the stored hash.</li>
     *   <li>Rejects a new password equal to the current one.</li>
     *   <li>Stores the BCrypt hash of the new password.</li>
     * </ol>
     *
     * @param id      id of the authenticated user
     * @param request current and new passwords
     * @throws RecursoNaoEncontradoException if the user no longer exists
     * @throws RegraNegocioException         if the current password is wrong or the new one is unchanged
     */
    public void changePassword(Long id, ChangePasswordRequest request) {
        User user = findUserOrThrow(id);

        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new RegraNegocioException("The current password is incorrect");
        }
        if (request.currentPassword().equals(request.newPassword())) {
            throw new RegraNegocioException("The new password must be different from the current one");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    /**
     * Deletes a user by id.
     *
     * @param id user identifier
     * @throws RecursoNaoEncontradoException if no user has this id
     */
    public void deleteById(Long id) {
        userRepository.delete(findUserOrThrow(id));
    }

    /**
     * Shared creation flow for students and admins.
     * <ol>
     *   <li>Normalizes the e-mail and rejects duplicates.</li>
     *   <li>Builds the entity, hashing the password.</li>
     *   <li>Assigns the given role and persists it.</li>
     * </ol>
     *
     * @param request validated registration payload
     * @param role    role to assign to the new account
     * @return the created user
     * @throws ConflitoException if the e-mail is already registered
     */
    private UserResponse createUser(UserRequest request, Role role) {
        String email = normalizeEmail(request.email());

        if (userRepository.existsByEmail(email)) {
            throw new ConflitoException("This e-mail is already registered");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(role);
        return UserResponse.from(userRepository.save(user));
    }

    /**
     * Loads a user or fails with a 404-mapped exception.
     *
     * @param id user identifier
     * @return the managed entity
     */
    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("User not found"));
    }

    /**
     * Trims and lower-cases an e-mail using {@link Locale#ROOT} so the result does not depend
     * on the JVM default locale.
     *
     * @param email raw e-mail
     * @return normalized e-mail
     */
    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}