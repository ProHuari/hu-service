package com.huariservice.huariia.dto.camara;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Data transfer record mapping a federal deputy summary from the Camara dos Deputados API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CamaraDeputyDto(
        Long id,
        String uri,
        String nome,
        String siglaPartido,
        String uriPartido,
        String siglaUf,
        Integer idLegislatura,
        String urlFoto,
        String email
) {}
