package com.huariservice.huariia.DTOS;

import com.huariservice.huariia.entities.enums.TipoPerfil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRequest {

    private String nome;
    private String email;
    private String senha;
    private TipoPerfil tipoPerfil;
}
