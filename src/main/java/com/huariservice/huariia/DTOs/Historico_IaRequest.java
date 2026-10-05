package com.huariservice.huariia.DTOs;

import com.huariservice.huariia.entities.Usuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public record  Historico_IaRequest (
        @NotBlank(message = "A pergunta é obrigatória")
        String pergunta,

        @NotBlank(message = "A resposta é obrigatória")
        String resposta,

        @NotNull(message = "O usuário é obrigatório")
        Long usuarioId
) {}
