package com.huariservice.huariia.dto.location;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Data transfer record mapping a Brazilian federative state (UF) from the IBGE API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record StateResponse(
        Long id,
        String sigla,
        String nome,
        RegionDto regiao
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RegionDto(
            Long id,
            String sigla,
            String nome
    ) {}
}
