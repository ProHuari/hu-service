package com.huariservice.huariia.dto.camara;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Data transfer record mapping a legislative bill / proposition from the Camara dos Deputados API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CamaraPropositionDto(
        Long id,
        String uri,
        String siglaTipo,
        Integer codTipo,
        Integer numero,
        Integer ano,
        String ementa
) {}
