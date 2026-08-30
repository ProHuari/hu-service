package com.huariservice.huariia.DTOS;

import com.huariservice.huariia.entities.Autores;
import com.huariservice.huariia.entities.Categorias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CursosRequest {

    private String titulos;
    private String descricao;
    private String urlVideo;
    private Categorias categoria;
    private Autores autores;
}
