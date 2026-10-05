package com.huariservice.huariia.DTOs;

import com.huariservice.huariia.entities.Cursos;


public record CursoResponse(
        Long id,
        String titulo,
        String descricao,
        String urlVideo,
        CategoriaResponse categoria,
        AutorResponse autor
) {
    // Chamar dentro de uma transação (@Transactional) por causa do LAZY.
    public static CursoResponse from(Cursos curso) {
        return new CursoResponse(
                curso.getId(),
                curso.getTitulo(),
                curso.getDescricao(),
                curso.getUrlVideo(),
                CategoriaResponse.from(curso.getCategoria()),
                AutorResponse.from(curso.getAutor())
        );
    }
}