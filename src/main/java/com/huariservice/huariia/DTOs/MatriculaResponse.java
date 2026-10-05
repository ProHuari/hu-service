package com.huariservice.huariia.DTOs;

import com.huariservice.huariia.entities.Matricula;
import com.huariservice.huariia.entities.enums.StatusMatricula;

import java.time.LocalDate;


public record MatriculaResponse(
        Long id,
        LocalDate dataMatricula,
        StatusMatricula statusMatricula,
        Long usuarioId,
        Long cursoId
) {
    public static MatriculaResponse from(Matricula matricula) {
        return new MatriculaResponse(
                matricula.getId(),
                matricula.getDataMatricula(),
                matricula.getStatusMatricula(),
                matricula.getUsuario().getId(),
                matricula.getCurso().getId()
        );
    }
}

