package com.huariservice.huariia.DTOs;

import com.huariservice.huariia.entities.Cursos;
import com.huariservice.huariia.entities.Usuario;
import com.huariservice.huariia.entities.enums.StatusMatricula;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


public record MatriculasRequest (
        @NotNull(message = "O usuário é obrigatório")
        Long usuarioId,

        @NotNull(message = "O curso é obrigatório")
        Long cursoId
) {}