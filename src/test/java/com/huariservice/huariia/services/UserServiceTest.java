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

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should register a user with STUDENT role and encrypted password")
    void shouldRegisterStudentSuccessfully() {
        UserRequest request = new UserRequest("Maria Santos", "maria@huari.com", "password123");

        when(userRepository.existsByEmail("maria@huari.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("secureHash123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(5L);
            return user;
        });

        UserResponse response = userService.registerStudent(request);

        assertNotNull(response);
        assertEquals(5L, response.id());
        assertEquals("Maria Santos", response.name());
        assertEquals("maria@huari.com", response.email());
        assertEquals(Role.STUDENT, response.role());
    }

    @Test
    @DisplayName("Should throw ConflictException when registering user with duplicate e-mail")
    void shouldThrowConflictWhenEmailAlreadyExists() {
        UserRequest request = new UserRequest("Maria Santos", "maria@huari.com", "password123");

        when(userRepository.existsByEmail("maria@huari.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.registerStudent(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when current password is incorrect")
    void shouldThrowExceptionWhenCurrentPasswordIsIncorrect() {
        Long userId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest("wrongPassword", "newPassword123");
        User user = new User();
        user.setId(userId);
        user.setPasswordHash("correctHash");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "correctHash")).thenReturn(false);

        assertThrows(BusinessRuleException.class, () -> userService.changePassword(userId, request));
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when new password equals current password")
    void shouldThrowExceptionWhenNewPasswordEqualsCurrent() {
        Long userId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest("samePassword", "samePassword");
        User user = new User();
        user.setId(userId);
        user.setPasswordHash("samePasswordHash");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("samePassword", "samePasswordHash")).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> userService.changePassword(userId, request));
    }
}

