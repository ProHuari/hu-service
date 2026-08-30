package com.huariservice.huariia.DTOS;

import com.huariservice.huariia.entities.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Historico_IaRequest {

    private String pergunta;
    private String resposta;
    private LocalDateTime dataConsulta;
    private Usuario usuario;
}
