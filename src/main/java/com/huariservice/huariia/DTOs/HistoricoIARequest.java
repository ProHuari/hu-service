package com.huariservice.huariia.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record HistoricoIARequest(
        @NotBlank(message = "A pergunta é obrigatória")
        String pergunta,

        @NotBlank(message = "A resposta é obrigatória")
        String resposta,

        @NotNull(message = "O usuário é obrigatório")
        Long usuarioId
) {}
