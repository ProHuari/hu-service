package com.huariservice.huariia.DTOs;

import jakarta.validation.constraints.NotNull;


public record MatriculaRequest(
        @NotNull(message = "O usuário é obrigatório")
        Long usuarioId,

        @NotNull(message = "O curso é obrigatório")
        Long cursoId
) {}