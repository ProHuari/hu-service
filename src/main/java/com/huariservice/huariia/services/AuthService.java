package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.LoginRequest;
import com.huariservice.huariia.DTOs.LoginResponse;
import com.huariservice.huariia.entities.User;
import com.huariservice.huariia.exceptions.InvalidCredentialsException;
import com.huariservice.huariia.repositories.UserRepository;
import com.huariservice.huariia.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/**
 * Authenticates users and issues JWTs.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    /**
     * Validates e-mail and password and returns a signed JWT.
     *
     * @param request login e-mail and password
     * @return the bearer token with the user id and role
     * @throws InvalidCredentialsException if the e-mail does not exist or the password is wrong
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository
                .findByEmail(request.email().trim().toLowerCase(Locale.ROOT))
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(InvalidCredentialsException::new);

        String token = tokenService.generateToken(user);
        return LoginResponse.of(token, user.getId(), user.getRole().name());
    }
}
