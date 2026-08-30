package com.huariservice.huariia.DTOS;

import com.huariservice.huariia.entities.Cursos;
import com.huariservice.huariia.entities.Usuario;
import com.huariservice.huariia.entities.enums.StatusMT;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatriculasRequest {

    private LocalDate dtMatricula;
    private StatusMT statusMT;
    private Usuario usuario;
    private Cursos cursos;
}
