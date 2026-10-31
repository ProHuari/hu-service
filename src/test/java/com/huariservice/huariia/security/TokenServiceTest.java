package com.huariservice.huariia.security;

import com.huariservice.huariia.entities.User;
import com.huariservice.huariia.entities.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TokenService.
 * Validates JWT generation and safe claims/subject extraction.
 */
class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService(
                "chave-secreta-de-teste-com-mais-de-32-caracteres-para-hmac-sha",
                60
        );
    }

    @Test
    @DisplayName("Deve gerar token JWT válido e extrair os dados do usuário corretamente")
    void deveGerarEValidarTokenComSucesso() {
        User user = new User();
        user.setId(42L);
        user.setName("Aluno Teste");
        user.setEmail("aluno@huari.com");
        user.setRole(Role.STUDENT);

        String token = tokenService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());

        Optional<AuthenticatedUser> authUserOpt = tokenService.validate(token);
        assertTrue(authUserOpt.isPresent());

        AuthenticatedUser authUser = authUserOpt.get();
        assertEquals(42L, authUser.userId());
        assertEquals("aluno@huari.com", authUser.email());
        assertEquals("STUDENT", authUser.role());
        assertFalse(authUser.isAdmin());
    }

    @Test
    @DisplayName("Deve identificar corretamente quando o usuário possui perfil ADMIN")
    void deveIdentificarUsuarioAdmin() {
        User admin = new User();
        admin.setId(1L);
        admin.setName("Admin Teste");
        admin.setEmail("admin@huari.com");
        admin.setRole(Role.ADMIN);

        String token = tokenService.generateToken(admin);
        Optional<AuthenticatedUser> authUserOpt = tokenService.validate(token);

        assertTrue(authUserOpt.isPresent());
        assertTrue(authUserOpt.get().isAdmin());
    }

    @Test
    @DisplayName("Deve retornar vazio ao validar token inválido ou malformado")
    void deveRetornarVazioParaTokenInvalido() {
        Optional<AuthenticatedUser> authUserOpt = tokenService.validate("token-invalido-qualquer");
        assertTrue(authUserOpt.isEmpty());
    }
}
