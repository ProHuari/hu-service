package com.huariservice.huariia.DTOs;

import jakarta.validation.constraints.NotBlank;

// TEMPORÁRIO: em produção, a resposta vem da IA (gravada pelo service), não do cliente.
// O usuário vem do token (login), não do corpo.
public record HistoricoIaRequest(
        @NotBlank(message = "A pergunta é obrigatória")
        String pergunta,

        @NotBlank(message = "A resposta é obrigatória")
        String resposta
) {}