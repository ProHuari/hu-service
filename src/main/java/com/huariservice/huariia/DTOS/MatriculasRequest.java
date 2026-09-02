package com.huariservice.huariia.DTOS;

import com.huariservice.huariia.entities.Cursos;
import com.huariservice.huariia.entities.Usuario;
import com.huariservice.huariia.entities.enums.StatusMT;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatriculasRequest {
    @NotNull(message = "A data de matrícula é obrigatória")
    private LocalDate dtMatricula;
    @NotNull(message = "O status é obrigatório")
    private StatusMT statusMT;
    @NotNull(message = "O usuário é obrigatório")
    private Usuario usuario;
    @NotNull(message = "O curso é obrigatório")
    private Cursos cursos;
}
