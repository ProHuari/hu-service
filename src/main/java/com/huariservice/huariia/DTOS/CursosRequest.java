package com.huariservice.huariia.DTOS;

import com.huariservice.huariia.entities.Autores;
import com.huariservice.huariia.entities.Categorias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CursosRequest {
    @NotBlank(message = "O título é obrigatório")
    @Size(max = 155, message = "O título deve ter no máximo 155 caracteres")
    private String titulos;
    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;
    @NotBlank(message = "A URL do vídeo é obrigatória")
    @Size(max = 254)
    private String urlVideo;
    @NotNull(message = "A categoria é obrigatória")
    private Categorias categoria;
    @NotNull(message = "O autor é obrigatório")
    private Autores autores;
}
