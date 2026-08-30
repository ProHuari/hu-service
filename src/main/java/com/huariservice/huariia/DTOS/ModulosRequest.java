package com.huariservice.huariia.DTOS;

import com.huariservice.huariia.entities.Cursos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModulosRequest {

    private String titulo;
    private String descricao;
    private Integer ordem;
    private Cursos cursos;
}
