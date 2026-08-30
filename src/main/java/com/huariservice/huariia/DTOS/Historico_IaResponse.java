package com.huariservice.huariia.DTOS;

import com.huariservice.huariia.entities.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Historico_IaResponse {

    private Long id;
    private LocalDateTime dataConsulta;
    private Usuario usuario;
}
