package com.huariservice.huariia.dto.location;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Data transfer record mapping postal code (CEP) lookup from ViaCEP API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CepLookupResponse(
        String cep,
        String logradouro,
        String complemento,
        String bairro,
        String localidade,
        String uf,
        String estado,
        String regiao,
        String ibge,
        String ddd,
        Boolean erro
) {
    public boolean hasError() {
        return Boolean.TRUE.equals(erro);
    }

    public static CepLookupResponse fallback(String cepClean) {
        return new CepLookupResponse(
                cepClean,
                "",
                "",
                "",
                "Consulta Indisponível",
                "BR",
                "Brasil",
                "Nacional",
                "",
                "",
                true
        );
    }
}
