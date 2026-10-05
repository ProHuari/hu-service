package com.huariservice.huariia.DTOs;

import com.huariservice.huariia.entities.Aula;


public record AulaResponse(
        Long id,
        String titulo,
        String descricao,
        String urlVideo,
        Integer ordem,
        Integer duracaoEmMinutos,
        Long moduloId
) {
    public static AulaResponse from(Aula aula) {
        return new AulaResponse(
                aula.getId(),
                aula.getTitulo(),
                aula.getDescricao(),
                aula.getUrlVideo(),
                aula.getOrdem(),
                aula.getDuracaoEmMinutos(),
                aula.getModulo().getId()
        );
    }
}