package com.huariservice.huariia.DTOs;

import jakarta.validation.constraints.NotBlank;

// TEMPORÁRIO: quando a IA for integrada, a resposta vem do service, não do cliente.
// O usuário vem do token (login), não do corpo
public record HistoricoIaRequest(
        @NotBlank(message = "A pergunta é obrigatória")
        String pergunta
) {}
