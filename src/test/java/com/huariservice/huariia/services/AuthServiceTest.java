package com.huariservice.huariia.services;

import com.huariservice.huariia.dto.LoginRequest;
import com.huariservice.huariia.dto.LoginResponse;
import com.huariservice.huariia.entities.User;
import com.huariservice.huariia.entities.enums.Role;
import com.huariservice.huariia.exceptions.CredenciaisInvalidasException;
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
 * Testes unitários para o fluxo de autenticação e validação de credenciais no AuthService.
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
        User usuario = new User();
        usuario.setId(10L);
        usuario.setEmail("aluno@huari.com");
        usuario.setPasswordHash("hashBcrypted");
        usuario.setRole(Role.STUDENT);

        when(userRepository.findByEmail("aluno@huari.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaCorreta123", "hashBcrypted")).thenReturn(true);
        when(tokenService.gerarToken(usuario)).thenReturn("mocked.jwt.token");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.token());
        assertEquals(10L, response.userId());
        assertEquals("STUDENT", response.role());
    }

    @Test
    @DisplayName("Deve lançar CredenciaisInvalidasException quando a senha não conferir")
    void deveLancarExcecaoQuandoSenhaInvalida() {
        LoginRequest request = new LoginRequest("aluno@huari.com", "senhaIncorreta");
        User usuario = new User();
        usuario.setId(10L);
        usuario.setEmail("aluno@huari.com");
        usuario.setPasswordHash("hashBcrypted");

        when(userRepository.findByEmail("aluno@huari.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaIncorreta", "hashBcrypted")).thenReturn(false);

        assertThrows(CredenciaisInvalidasException.class, () -> authService.login(request));
        verify(tokenService, never()).gerarToken(any());
    }

    @Test
    @DisplayName("Deve lançar CredenciaisInvalidasException quando o e-mail não existir")
    void deveLancarExcecaoQuandoEmailNaoCadastrado() {
        LoginRequest request = new LoginRequest("inexistente@huari.com", "senha123");

        when(userRepository.findByEmail("inexistente@huari.com")).thenReturn(Optional.empty());

        assertThrows(CredenciaisInvalidasException.class, () -> authService.login(request));
        verify(tokenService, never()).gerarToken(any());
    }
}
