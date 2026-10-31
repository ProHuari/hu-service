package com.huariservice.huariia.services;

import com.huariservice.huariia.dto.LoginRequest;
import com.huariservice.huariia.dto.LoginResponse;
import com.huariservice.huariia.entities.User;
import com.huariservice.huariia.exceptions.CredenciaisInvalidasException;
import com.huariservice.huariia.repositories.UserRepository;
import com.huariservice.huariia.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/**
 * Authentication use case: validates credentials and issues a JWT.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    /**
     * Authenticates a user by e-mail and password.
     * <ol>
     *   <li>Normalizes the e-mail (trim + lower-case) and loads the user.</li>
     *   <li>Compares the raw password with the stored BCrypt hash.</li>
     *   <li>If either step fails, throws the same generic exception, so callers cannot tell
     *       whether the e-mail or the password was wrong (prevents user enumeration).</li>
     *   <li>Generates a signed JWT and returns it with the user id and role.</li>
     * </ol>
     *
     * @param request e-mail and plain-text password
     * @return the bearer token, user id and role
     * @throws CredenciaisInvalidasException if the credentials are invalid
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository
                .findByEmail(request.email().trim().toLowerCase(Locale.ROOT))
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(CredenciaisInvalidasException::new);

        String token = tokenService.gerarToken(user);
        return LoginResponse.of(token, user.getId(), user.getRole().name());
    }
}