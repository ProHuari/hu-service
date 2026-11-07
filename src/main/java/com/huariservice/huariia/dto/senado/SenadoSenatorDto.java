package com.huariservice.huariia.dto.senado;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Data transfer record mapping a federal senator from the Senado Federal Open Data API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SenadoSenatorDto(
        Long codigo,
        String nomeParlamentar,
        String nomeCompleto,
        String sexo,
        String formaTratamento,
        String urlFoto,
        String siglaPartido,
        String siglaUf,
        String email,
        String participacao
) {}
