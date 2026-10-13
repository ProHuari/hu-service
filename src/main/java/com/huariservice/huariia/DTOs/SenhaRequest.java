package com.huariservice.huariia.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Troca de senha: exige a senha atual para confirmar que é o próprio dono.
public record SenhaRequest(
        @NotBlank(message = "A senha atual é obrigatória")
        String senhaAtual,

        // BCrypt só considera os primeiros 72 bytes
        @NotBlank(message = "A nova senha é obrigatória")
        @Size(min = 6, max = 72, message = "A nova senha deve ter entre 6 e 72 caracteres")
        String novaSenha
) {}