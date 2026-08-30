package com.huariservice.huariia.DTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriasRequest {

    private String nome;
    private String descricao;
}
