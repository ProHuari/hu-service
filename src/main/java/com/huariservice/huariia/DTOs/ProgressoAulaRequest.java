package com.huariservice.huariia.DTOs;


import com.huariservice.huariia.entities.enums.StatusAula;
import jakarta.validation.constraints.NotNull;

public record ProgressoAulaRequest (
        @NotNull(message = "O status é obrigatório")
        StatusAula statusAula,

        @NotNull(message = "O usuário é obrigatório")
        Long usuarioId,

        @NotNull(message = "A aula é obrigatória")
        Long aulaId
) {}


