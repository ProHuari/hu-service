package com.huariservice.huariia.DTOS;

import com.huariservice.huariia.entities.enums.TipoPerfil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponse {

    private Long id;
    private String nome;
    private TipoPerfil tipoPerfil;
}
