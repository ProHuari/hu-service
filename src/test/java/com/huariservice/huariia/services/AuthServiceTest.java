package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.LoginRequest;
import com.huariservice.huariia.DTOs.LoginResponse;
import com.huariservice.huariia.entities.User;
import com.huariservice.huariia.entities.enums.Role;
import com.huariservice.huariia.exceptions.InvalidCredentialsException;
import com.huariservice.huariia.repositories.UserRepository;
import com.huariservice.huariia.security.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for authentication flow and credential validation in AuthService.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Deve autenticar com sucesso quando email e senha forem válidos")
    void deveAutenticarComSucesso() {
        LoginRequest request = new LoginRequest("aluno@huari.com", "senhaCorreta123");
        User user = new User();
        user.setId(10L);
        user.setEmail("aluno@huari.com");
        user.setPasswordHash("hashBcrypted");
        user.setRole(Role.STUDENT);

        when(userRepository.findByEmail("aluno@huari.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senhaCorreta123", "hashBcrypted")).thenReturn(true);
        when(tokenService.generateToken(user)).thenReturn("mocked.jwt.token");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.token());
        assertEquals(10L, response.userId());
        assertEquals("STUDENT", response.role());
    }

    @Test
    @DisplayName("Deve lançar InvalidCredentialsException quando a senha não conferir")
    void deveLancarExcecaoQuandoSenhaInvalida() {
        LoginRequest request = new LoginRequest("aluno@huari.com", "senhaIncorreta");
        User user = new User();
        user.setId(10L);
        user.setEmail("aluno@huari.com");
        user.setPasswordHash("hashBcrypted");

        when(userRepository.findByEmail("aluno@huari.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senhaIncorreta", "hashBcrypted")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
        verify(tokenService, never()).generateToken(any());
    }

    @Test
    @DisplayName("Deve lançar InvalidCredentialsException quando o e-mail não existir")
    void deveLancarExcecaoQuandoEmailNaoCadastrado() {
        LoginRequest request = new LoginRequest("inexistente@huari.com", "senha123");

        when(userRepository.findByEmail("inexistente@huari.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
        verify(tokenService, never()).generateToken(any());
    }
}
