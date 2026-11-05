package com.huariservice.huariia.security;

import com.huariservice.huariia.entities.User;
import com.huariservice.huariia.entities.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService(
                "test-secret-key-with-more-than-32-characters-for-hmac-sha256",
                60,
                10080
        );
    }

    @Test
    @DisplayName("Should generate valid JWT token and extract user data accurately")
    void shouldGenerateAndValidateTokenSuccessfully() {
        User user = new User();
        user.setId(42L);
        user.setName("Student Test");
        user.setEmail("student@huari.com");
        user.setRole(Role.STUDENT);

        String token = tokenService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());

        Optional<TokenData> tokenDataOpt = tokenService.validateToken(token);
        assertTrue(tokenDataOpt.isPresent());

        TokenData tokenData = tokenDataOpt.get();
        assertEquals(42L, tokenData.userId());
        assertEquals("student@huari.com", tokenData.email());
        assertEquals("STUDENT", tokenData.role());
        assertFalse(tokenData.isAdmin());
    }

    @Test
    @DisplayName("Should generate and validate Refresh Token and prevent its use as an Access Token")
    void shouldGenerateAndValidateRefreshToken() {
        User user = new User();
        user.setId(99L);
        user.setName("Student Refresh");
        user.setEmail("refresh@huari.com");
        user.setRole(Role.STUDENT);

        String refreshToken = tokenService.generateRefreshToken(user);

        assertNotNull(refreshToken);
        assertTrue(tokenService.validateToken(refreshToken).isEmpty());
        assertEquals(Optional.of(99L), tokenService.validateRefreshToken(refreshToken));
        assertTrue(tokenService.extractExpiration(refreshToken).isPresent());
    }

    @Test
    @DisplayName("Should accurately identify when user has ADMIN role")
    void shouldIdentifyAdminUser() {
        User admin = new User();
        admin.setId(1L);
        admin.setName("Admin Test");
        admin.setEmail("admin@huari.com");
        admin.setRole(Role.ADMIN);

        String token = tokenService.generateToken(admin);
        Optional<TokenData> tokenDataOpt = tokenService.validateToken(token);

        assertTrue(tokenDataOpt.isPresent());
        assertTrue(tokenDataOpt.get().isAdmin());
    }

    @Test
    @DisplayName("Should return empty when validating invalid or malformed token")
    void shouldReturnEmptyForInvalidToken() {
        Optional<TokenData> tokenDataOpt = tokenService.validateToken("invalid-token-string");
        assertTrue(tokenDataOpt.isEmpty());
    }
}
