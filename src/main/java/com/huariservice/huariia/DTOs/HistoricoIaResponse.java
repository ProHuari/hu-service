package com.huariservice.huariia.DTOs;

import com.huariservice.huariia.entities.HistoricoIa;

import java.time.LocalDateTime;


public record HistoricoIaResponse(
        Long id,
        String pergunta,
        String resposta,
        LocalDateTime dataConsulta,
        Long usuarioId
) {
    public static HistoricoIaResponse from(HistoricoIa historico) {
        return new HistoricoIaResponse(
                historico.getId(),
                historico.getPergunta(),
                historico.getResposta(),
                historico.getDataConsulta(),
                historico.getUsuario().getId()
        );
    }
}
