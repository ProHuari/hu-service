package com.huariservice.huariia.services;

import com.huariservice.huariia.dto.SenhaRequest;
import com.huariservice.huariia.dto.UsuarioRequest;
import com.huariservice.huariia.dto.UsuarioResponse;
import com.huariservice.huariia.entities.Usuario;
import com.huariservice.huariia.entities.enums.TipoPerfil;
import com.huariservice.huariia.exceptions.ConflitoException;
import com.huariservice.huariia.exceptions.RegraNegocioException;
import com.huariservice.huariia.repositories.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para o UsuarioService cobrindo cadastro, verificação de duplicidade e troca de senha.
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Deve cadastrar um usuário com perfil ALUNO e senha criptografada")
    void deveCadastrarUsuarioAlunoComSucesso() {
        UsuarioRequest request = new UsuarioRequest("Maria Santos", "maria@huari.com", "senha123");

        when(usuarioRepository.existsByEmail("maria@huari.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("hashSeguro123");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(5L);
            return u;
        });

        UsuarioResponse response = usuarioService.pubUsuario(request);

        assertNotNull(response);
        assertEquals(5L, response.id());
        assertEquals("Maria Santos", response.nome());
        assertEquals("maria@huari.com", response.email());
        assertEquals(TipoPerfil.ALUNO, response.tipoPerfil());
    }

    @Test
    @DisplayName("Deve lançar ConflitoException ao tentar cadastrar usuário com e-mail já existente")
    void deveLancarConflitoAoCadastrarEmailExistente() {
        UsuarioRequest request = new UsuarioRequest("Maria Santos", "maria@huari.com", "senha123");

        when(usuarioRepository.existsByEmail("maria@huari.com")).thenReturn(true);

        assertThrows(ConflitoException.class, () -> usuarioService.pubUsuario(request));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao tentar trocar senha com a senha atual incorreta")
    void deveLancarExcecaoQuandoSenhaAtualIncorreta() {
        Long usuarioId = 1L;
        SenhaRequest request = new SenhaRequest("senhaErrada", "novaSenha123");
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setSenhaHash("hashCorreto");

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaErrada", "hashCorreto")).thenReturn(false);

        assertThrows(RegraNegocioException.class, () -> usuarioService.trocarSenha(usuarioId, request));
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException quando a nova senha for igual à senha atual")
    void deveLancarExcecaoQuandoNovaSenhaIgualAtual() {
        Long usuarioId = 1L;
        SenhaRequest request = new SenhaRequest("mesmaSenha", "mesmaSenha");
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setSenhaHash("hashMesmaSenha");

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("mesmaSenha", "hashMesmaSenha")).thenReturn(true);

        assertThrows(RegraNegocioException.class, () -> usuarioService.trocarSenha(usuarioId, request));
    }
}
