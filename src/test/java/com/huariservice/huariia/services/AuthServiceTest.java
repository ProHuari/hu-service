package com.huariservice.huariia.services;

import com.huariservice.huariia.dto.LoginRequest;
import com.huariservice.huariia.dto.LoginResponse;
import com.huariservice.huariia.dto.RefreshTokenRequest;
import com.huariservice.huariia.entities.User;
import com.huariservice.huariia.entities.enums.Role;
import com.huariservice.huariia.exceptions.InvalidCredentialsException;
import com.huariservice.huariia.exceptions.TooManyRequestsException;
import com.huariservice.huariia.repositories.UserRepository;
import com.huariservice.huariia.security.LoginAttemptService;
import com.huariservice.huariia.security.TokenBlacklistService;
import com.huariservice.huariia.security.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @Spy
    private LoginAttemptService loginAttemptService = new LoginAttemptService();

    @Spy
    private TokenBlacklistService tokenBlacklistService = new TokenBlacklistService();

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Should authenticate successfully when e-mail and password are valid")
    void shouldAuthenticateSuccessfully() {
        LoginRequest request = new LoginRequest("student@huari.com", "validPassword123");
        User user = new User();
        user.setId(10L);
        user.setEmail("student@huari.com");
        user.setPasswordHash("bcryptHash");
        user.setRole(Role.STUDENT);

        when(userRepository.findByEmail("student@huari.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("validPassword123", "bcryptHash")).thenReturn(true);
        when(tokenService.generateToken(user)).thenReturn("mocked.jwt.token");
        when(tokenService.generateRefreshToken(user)).thenReturn("mocked.refresh.token");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.token());
        assertEquals("mocked.refresh.token", response.refreshToken());
        assertEquals(10L, response.userId());
        assertEquals("STUDENT", response.role());
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when password does not match")
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        LoginRequest request = new LoginRequest("student@huari.com", "wrongPassword");
        User user = new User();
        user.setId(10L);
        user.setEmail("student@huari.com");
        user.setPasswordHash("bcryptHash");

        when(userRepository.findByEmail("student@huari.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "bcryptHash")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
        verify(tokenService, never()).generateToken(any());
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when e-mail is not registered")
    void shouldThrowExceptionWhenEmailIsNotRegistered() {
        LoginRequest request = new LoginRequest("missing@huari.com", "password123");

        when(userRepository.findByEmail("missing@huari.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
        verify(tokenService, never()).generateToken(any());
    }

    @Test
    @DisplayName("Should block login with TooManyRequestsException after 5 failed attempts")
    void shouldLockAfterFiveFailedAttempts() {
        LoginRequest request = new LoginRequest("bruteforce@huari.com", "wrong");
        when(userRepository.findByEmail("bruteforce@huari.com")).thenReturn(Optional.empty());

        for (int i = 0; i < 5; i++) {
            assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
        }

        assertThrows(TooManyRequestsException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("Should revoke token on logout and reject refresh of revoked token")
    void shouldRevokeTokenOnLogoutAndRejectRefresh() {
        when(tokenService.extractExpiration("my.jwt.token"))
                .thenReturn(Optional.of(Instant.now().plusSeconds(3600)));

        authService.logout("Bearer my.jwt.token");

        assertTrue(tokenBlacklistService.isRevoked("my.jwt.token"));
        assertThrows(InvalidCredentialsException.class,
                () -> authService.refresh(new RefreshTokenRequest("my.jwt.token")));
    }
}
