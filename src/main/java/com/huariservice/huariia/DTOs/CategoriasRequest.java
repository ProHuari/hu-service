package com.huariservice.huariia.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriasRequest(
        @NotBlank(message = "O nome é obrigatório") @Size(max = 100) String nome,
        String descricao
) {}
