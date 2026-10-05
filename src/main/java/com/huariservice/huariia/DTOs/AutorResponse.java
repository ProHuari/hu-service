package com.huariservice.huariia.DTOs;


import com.huariservice.huariia.entities.Autor;

public record AutorResponse(
        Long id,
        String nomeCanal,
        String linkCanal
) {
    public static AutorResponse from(Autor autor) {
        return new AutorResponse(autor.getId(), autor.getNomeCanal(), autor.getLinkCanal());
    }
}
