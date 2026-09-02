package com.huariservice.huariia.DTOS;

import com.huariservice.huariia.entities.Usuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Historico_IaRequest {
    @NotBlank(message = "A pergunta é obrigatória")
    private String pergunta;
    @NotBlank(message = "A resposta é obrigatória")
    private String resposta;
    private LocalDateTime dataConsulta;
    @NotNull(message = "O usuário é obrigatório")
    private Usuario usuario;
}
