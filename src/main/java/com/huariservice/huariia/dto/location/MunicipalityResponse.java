package com.huariservice.huariia.dto.location;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Data transfer record mapping a municipality from the IBGE API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MunicipalityResponse(
        Long id,
        String nome
) {}
