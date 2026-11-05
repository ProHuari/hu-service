package com.huariservice.huariia.services;

import com.huariservice.huariia.dto.ChangePasswordRequest;
import com.huariservice.huariia.dto.UserRequest;
import com.huariservice.huariia.dto.UserResponse;
import com.huariservice.huariia.entities.User;
import com.huariservice.huariia.entities.enums.Role;
import com.huariservice.huariia.exceptions.BusinessRuleException;
import com.huariservice.huariia.exceptions.ConflictException;
import com.huariservice.huariia.repositories.UserRepository;
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
 * Testes unitários para o UserService cobrindo cadastro, verificação de duplicidade e troca de senha.
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Deve cadastrar um usuário com perfil STUDENT e senha criptografada")
    void deveCadastrarUsuarioAlunoComSucesso() {
        UserRequest request = new UserRequest("Maria Santos", "maria@huari.com", "senha123");

        when(userRepository.existsByEmail("maria@huari.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("hashSeguro123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(5L);
            return u;
        });

        UserResponse response = userService.registerStudent(request);

        assertNotNull(response);
        assertEquals(5L, response.id());
        assertEquals("Maria Santos", response.name());
        assertEquals("maria@huari.com", response.email());
        assertEquals(Role.STUDENT, response.role());
    }

    @Test
    @DisplayName("Deve lançar ConflitoException ao tentar cadastrar usuário com e-mail já existente")
    void deveLancarConflitoAoCadastrarEmailExistente() {
        UserRequest request = new UserRequest("Maria Santos", "maria@huari.com", "senha123");

        when(userRepository.existsByEmail("maria@huari.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.registerStudent(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException ao tentar trocar senha com a senha atual incorreta")
    void deveLancarExcecaoQuandoSenhaAtualIncorreta() {
        Long usuarioId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest("senhaErrada", "novaSenha123");
        User usuario = new User();
        usuario.setId(usuarioId);
        usuario.setPasswordHash("hashCorreto");

        when(userRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaErrada", "hashCorreto")).thenReturn(false);

        assertThrows(BusinessRuleException.class, () -> userService.changePassword(usuarioId, request));
    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException quando a nova senha for igual à senha atual")
    void deveLancarExcecaoQuandoNovaSenhaIgualAtual() {
        Long usuarioId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest("mesmaSenha", "mesmaSenha");
        User usuario = new User();
        usuario.setId(usuarioId);
        usuario.setPasswordHash("hashMesmaSenha");

        when(userRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("mesmaSenha", "hashMesmaSenha")).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> userService.changePassword(usuarioId, request));
    }
}
