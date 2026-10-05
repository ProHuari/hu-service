package com.huariservice.huariia.DTOs;

import com.huariservice.huariia.entities.Cursos;
import com.huariservice.huariia.entities.Usuario;
import com.huariservice.huariia.entities.enums.StatusMatricula;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatriculasResponse {

    private Long id;
    private LocalDate dtMatricula;
    private StatusMatricula statusMatricula;
    private Usuario usuario;
    private Cursos cursos;
}
