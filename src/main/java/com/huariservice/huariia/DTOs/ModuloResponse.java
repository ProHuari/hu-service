package com.huariservice.huariia.DTOs;

import com.huariservice.huariia.entities.Modulo;

public record ModuloResponse(
        Long id,
        String titulo,
        String descricao,
        Integer ordem,
        Long cursoId
) {
    public static ModuloResponse from(Modulo modulo) {
        return new ModuloResponse(
                modulo.getId(),
                modulo.getTitulo(),
                modulo.getDescricao(),
                modulo.getOrdem(),
                modulo.getCurso().getId()
        );
    }
}