package com.huariservice.huariia.DTOs;

import com.huariservice.huariia.entities.Usuario;
import com.huariservice.huariia.entities.enums.TipoPerfil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        TipoPerfil tipoPerfil
) {
    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipoPerfil()
        );
    }
}
