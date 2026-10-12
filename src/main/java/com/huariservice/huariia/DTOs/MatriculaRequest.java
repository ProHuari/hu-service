package com.huariservice.huariia.DTOs;

import jakarta.validation.constraints.NotNull;

// Data e status são definidos pelo servidor. O usuário vem do token (login).
public record MatriculaRequest(
        @NotNull(message = "O curso é obrigatório")
        Long cursoId
) {}