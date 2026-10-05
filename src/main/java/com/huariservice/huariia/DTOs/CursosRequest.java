package com.huariservice.huariia.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;


public record CursosRequest (
        @NotBlank(message = "O título é obrigatório")
        @Size(max = 155, message = "O título deve ter no máximo 155 caracteres")
        String titulo,

        @NotBlank(message = "A descrição é obrigatória")
        String descricao,

        @NotBlank(message = "A URL do vídeo é obrigatória")
        @Size(max = 254)
        @URL(message = "URL do vídeo inválida")
        String urlVideo,

        @NotNull(message = "A categoria é obrigatória")
        Long categoriaId,

        @NotNull(message = "O autor é obrigatório")
        Long autorId
) {}
