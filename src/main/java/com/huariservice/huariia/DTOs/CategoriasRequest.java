package com.huariservice.huariia.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriasRequest {
    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100)
    private String nome;
    private String descricao;
}
