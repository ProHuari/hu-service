package com.huariservice.huariia.DTOS;

import com.huariservice.huariia.entities.Modulo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AulasRequest {
    @NotBlank(message = "O título é obrigatório")
    @Size(max = 155)
    private String titulos;
    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;
    @NotBlank(message = "A URL do vídeo é obrigatória")
    @Size(max = 254)
    private String urlVideo;
    @NotNull(message = "A ordem é obrigatória")
    private Integer ordem;
    private Integer duracaoEmMinutos;
    @NotNull(message = "O módulo é obrigatório")
    private Modulo modulo;
}
