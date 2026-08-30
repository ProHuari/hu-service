package com.huariservice.huariia.DTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriasResponse {

    private Long id;
    private String nome;
    private String descricao;
}
