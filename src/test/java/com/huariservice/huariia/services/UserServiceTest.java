package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.ChangePasswordRequest;
import com.huariservice.huariia.DTOs.UserRequest;
import com.huariservice.huariia.DTOs.UserResponse;
import com.huariservice.huariia.entities.User;
import com.huariservice.huariia.entities.enums.Role;
import com.huariservice.huariia.exceptions.ConflictException;
import com.huariservice.huariia.exceptions.BusinessRuleException;
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
 * Unit tests for UserService covering registration, duplicate checking, and password change.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

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

        UserResponse response = userService.createStudent(request);

        assertNotNull(response);
        assertEquals(5L, response.id());
        assertEquals("Maria Santos", response.name());
        assertEquals("maria@huari.com", response.email());
        assertEquals(Role.STUDENT, response.role());
    }

    @Test
    @DisplayName("Deve lançar ConflictException ao tentar cadastrar usuário com e-mail já existente")
    void deveLancarConflitoAoCadastrarEmailExistente() {
        UserRequest request = new UserRequest("Maria Santos", "maria@huari.com", "senha123");

        when(userRepository.existsByEmail("maria@huari.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.createStudent(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException ao tentar trocar senha com a senha atual incorreta")
    void deveLancarExcecaoQuandoSenhaAtualIncorreta() {
        Long userId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest("senhaErrada", "novaSenha123");
        User user = new User();
        user.setId(userId);
        user.setPasswordHash("hashCorreto");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senhaErrada", "hashCorreto")).thenReturn(false);

        assertThrows(BusinessRuleException.class, () -> userService.changePassword(userId, request));
    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException quando a nova senha for igual à senha atual")
    void deveLancarExcecaoQuandoNovaSenhaIgualAtual() {
        Long userId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest("mesmaSenha", "mesmaSenha");
        User user = new User();
        user.setId(userId);
        user.setPasswordHash("hashMesmaSenha");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("mesmaSenha", "hashMesmaSenha")).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> userService.changePassword(userId, request));
    }
}

