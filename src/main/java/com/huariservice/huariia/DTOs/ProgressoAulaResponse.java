package com.huariservice.huariia.DTOs;

import com.huariservice.huariia.entities.Aula;
import com.huariservice.huariia.entities.ProgressoAula;
import com.huariservice.huariia.entities.Usuario;
import com.huariservice.huariia.entities.enums.StatusAula;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


public record ProgressoAulaResponse (
        Long id,
        StatusAula statusAula,
        LocalDateTime dataConclusao,
        Long usuarioId,
        Long aulaId
) {
    public static ProgressoAulaResponse from(ProgressoAula progresso) {
        return new ProgressoAulaResponse(
                progresso.getId(),
                progresso.getStatusAula(),
                progresso.getDataConclusao(),
                progresso.getUsuario().getId(),
                progresso.getAula().getId()
        );
    }
}
