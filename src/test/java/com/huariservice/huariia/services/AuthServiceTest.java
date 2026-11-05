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

/**
 * Testes unitários para o fluxo de autenticação, rate limiting, refresh token e logout no AuthService.
 */
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
        when(tokenService.gerarRefreshToken(usuario)).thenReturn("mocked.refresh.token");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.token());
        assertEquals("mocked.refresh.token", response.refreshToken());
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

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
        verify(tokenService, never()).gerarToken(any());
    }

    @Test
    @DisplayName("Deve lançar InvalidCredentialsException quando o e-mail não existir")
    void deveLancarExcecaoQuandoEmailNaoCadastrado() {
        LoginRequest request = new LoginRequest("inexistente@huari.com", "senha123");

        when(userRepository.findByEmail("inexistente@huari.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
        verify(tokenService, never()).gerarToken(any());
    }

    @Test
    @DisplayName("Deve bloquear login com TooManyRequestsException após 5 tentativas falhas")
    void deveBloquearAposCincoTentativasFalhas() {
        LoginRequest request = new LoginRequest("bruteforce@huari.com", "errada");
        when(userRepository.findByEmail("bruteforce@huari.com")).thenReturn(Optional.empty());

        for (int i = 0; i < 5; i++) {
            assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
        }

        assertThrows(TooManyRequestsException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("Deve revogar token no logout e rejeitar refresh de token revogado")
    void deveRevogarTokenNoLogoutERefresh() {
        when(tokenService.extrairExpiracao("meu.token.jwt"))
                .thenReturn(Optional.of(Instant.now().plusSeconds(3600)));

        authService.logout("Bearer meu.token.jwt");

        assertTrue(tokenBlacklistService.isRevoked("meu.token.jwt"));
        assertThrows(InvalidCredentialsException.class,
                () -> authService.refresh(new RefreshTokenRequest("meu.token.jwt")));
    }
}
