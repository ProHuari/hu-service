package com.huariservice.huariia.DTOs;

import com.huariservice.huariia.entities.enums.StatusAula;
import jakarta.validation.constraints.NotNull;

// O usuário vem do token (login), não do corpo.
public record ProgressoAulaRequest(
        @NotNull(message = "O status é obrigatório")
        StatusAula statusAula,

        @NotNull(message = "A aula é obrigatória")
        Long aulaId
) {}