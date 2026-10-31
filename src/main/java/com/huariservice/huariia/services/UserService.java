package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.ChangePasswordRequest;
import com.huariservice.huariia.DTOs.ProfileRequest;
import com.huariservice.huariia.DTOs.UserRequest;
import com.huariservice.huariia.DTOs.UserResponse;
import com.huariservice.huariia.entities.User;
import com.huariservice.huariia.entities.enums.Role;
import com.huariservice.huariia.exceptions.BusinessRuleException;
import com.huariservice.huariia.exceptions.ConflictException;
import com.huariservice.huariia.exceptions.ResourceNotFoundException;
import com.huariservice.huariia.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

/**
 * Business rules for user accounts: sign-up, admin management, profile and password changes.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private static final String EMAIL_TAKEN = "Esse e-mail já está cadastrado";
    private static final String USER_NOT_FOUND = "Esse usuário não foi cadastrado";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Public sign-up: the role is always {@link Role#STUDENT}.
     *
     * @param request name, e-mail and password
     * @return the created user
     * @throws ConflictException if the e-mail is already registered
     */
    public UserResponse createStudent(UserRequest request) {
        return create(request, Role.STUDENT);
    }

    /**
     * Creates an administrator. Only ADMIN users reach this method (enforced in SecurityConfig).
     *
     * @param request name, e-mail and password
     * @return the created administrator
     * @throws ConflictException if the e-mail is already registered
     */
    public UserResponse createAdmin(UserRequest request) {
        return create(request, Role.ADMIN);
    }

    /**
     * Lists all users.
     *
     * @return every registered user
     */
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(UserResponse::from).toList();
    }

    /**
     * Finds a user by id.
     *
     * @param id the user id
     * @return the user
     * @throws ResourceNotFoundException if the user does not exist
     */
    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return UserResponse.from(getUser(id));
    }

    /**
     * Admin operation: updates name and e-mail and resets the password of any user.
     *
     * @param id      the user id
     * @param request new name, e-mail and password
     * @return the updated user
     * @throws ResourceNotFoundException if the user does not exist
     * @throws ConflictException         if the new e-mail belongs to another user
     */
    public UserResponse update(Long id, UserRequest request) {
        User user = getUser(id);
        String email = normalize(request.email());

        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new ConflictException(EMAIL_TAKEN);
        }

        user.setName(request.name());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        return UserResponse.from(userRepository.save(user));
    }

    /**
     * Self-service operation (id comes from the token): updates only name and e-mail.
     *
     * @param id      the authenticated user's id
     * @param request new name and e-mail
     * @return the updated user
     * @throws ResourceNotFoundException if the user does not exist
     * @throws ConflictException         if the new e-mail belongs to another user
     */
    public UserResponse updateProfile(Long id, ProfileRequest request) {
        User user = getUser(id);
        String email = normalize(request.email());

        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new ConflictException(EMAIL_TAKEN);
        }

        user.setName(request.name());
        user.setEmail(email);

        return UserResponse.from(userRepository.save(user));
    }

    /**
     * Changes the password of the authenticated user. Requires the current password,
     * so that someone holding a borrowed token cannot take over the account.
     *
     * @param id      the authenticated user's id
     * @param request current and new password
     * @throws ResourceNotFoundException if the user does not exist
     * @throws BusinessRuleException     if the current password is wrong or equals the new one
     */
    public void changePassword(Long id, ChangePasswordRequest request) {
        User user = getUser(id);

        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new BusinessRuleException("A senha atual está incorreta");
        }
        if (request.currentPassword().equals(request.newPassword())) {
            throw new BusinessRuleException("A nova senha deve ser diferente da atual");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    /**
     * Deletes a user.
     *
     * @param id the user id
     * @throws ResourceNotFoundException if the user does not exist
     */
    public void delete(Long id) {
        userRepository.delete(getUser(id));
    }

    private UserResponse create(UserRequest request, Role role) {
        String email = normalize(request.email());

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException(EMAIL_TAKEN);
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(role);

        return UserResponse.from(userRepository.save(user));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));
    }

    private String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
