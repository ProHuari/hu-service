package com.huariservice.huariia.dto.senado;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Data transfer record mapping a legislative matter (materia) from the Senado Federal API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SenadoMatterDto(
        Long codigo,
        String sigla,
        Integer numero,
        Integer ano,
        String ementa,
        String autor,
        String urlDetalhes
) {}
