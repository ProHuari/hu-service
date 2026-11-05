package com.huariservice.huariia.security;

import com.huariservice.huariia.entities.User;
import com.huariservice.huariia.entities.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para o TokenService.
 * Valida a geração de JWT e a extração segura de claims/sujeito.
 */
class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        // Segredo com pelo menos 32 caracteres (256 bits) exigido pelo algoritmo HMAC-SHA
        tokenService = new TokenService(
                "chave-secreta-de-teste-com-mais-de-32-caracteres-para-hmac-sha",
                60
        );
    }

    @Test
    @DisplayName("Deve gerar token JWT válido e extrair os dados do usuário corretamente")
    void deveGerarEValidarTokenComSucesso() {
        User usuario = new User();
        usuario.setId(42L);
        usuario.setName("Aluno Teste");
        usuario.setEmail("aluno@huari.com");
        usuario.setRole(Role.STUDENT);

        String token = tokenService.gerarToken(usuario);

        assertNotNull(token);
        assertFalse(token.isBlank());

        Optional<DadosToken> dadosOpt = tokenService.validar(token);
        assertTrue(dadosOpt.isPresent());

        DadosToken dados = dadosOpt.get();
        assertEquals(42L, dados.usuarioId());
        assertEquals("aluno@huari.com", dados.email());
        assertEquals("STUDENT", dados.perfil());
        assertFalse(dados.isAdmin());
    }

    @Test
    @DisplayName("Deve identificar corretamente quando o usuário possui perfil ADMIN")
    void deveIdentificarUsuarioAdmin() {
        User admin = new User();
        admin.setId(1L);
        admin.setName("Admin Teste");
        admin.setEmail("admin@huari.com");
        admin.setRole(Role.ADMIN);

        String token = tokenService.gerarToken(admin);
        Optional<DadosToken> dadosOpt = tokenService.validar(token);

        assertTrue(dadosOpt.isPresent());
        assertTrue(dadosOpt.get().isAdmin());
    }

    @Test
    @DisplayName("Deve retornar vazio ao validar token inválido ou malformado")
    void deveRetornarVazioParaTokenInvalido() {
        Optional<DadosToken> dadosOpt = tokenService.validar("token-invalido-qualquer");
        assertTrue(dadosOpt.isEmpty());
    }
}
