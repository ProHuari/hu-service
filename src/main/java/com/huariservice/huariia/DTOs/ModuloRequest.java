package com.huariservice.huariia.DTOs;

import com.huariservice.huariia.entities.Cursos;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public record ModuloRequest(
        @NotBlank(message = "O título é obrigatório")
        @Size(max = 150)
        String titulo,

        String descricao,

        @NotNull(message = "A ordem é obrigatória")
        @Min(value = 1, message = "A ordem deve ser no mínimo 1")
        Integer ordem,

        @NotNull(message = "O curso é obrigatório")
        Long cursoId
) {}