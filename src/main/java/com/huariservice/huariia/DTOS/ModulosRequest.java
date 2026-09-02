package com.huariservice.huariia.DTOS;

import com.huariservice.huariia.entities.Cursos;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModulosRequest {
    @NotBlank(message = "O título é obrigatório")
    @Size(max = 150)
    private String titulo;

    private String descricao;
    @NotNull(message = "A ordem é obrigatória")
    private Integer ordem;
    @NotNull(message = "O curso é obrigatório")
    private Cursos cursos;
}
