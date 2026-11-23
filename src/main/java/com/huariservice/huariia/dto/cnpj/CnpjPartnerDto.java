package com.huariservice.huariia.dto.cnpj;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Partner or shareholder (QSA) in the corporate structure of a supplier company.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CnpjPartnerDto(
        String nomeSocio,
        String qualificacaoSocio,
        String faixaEtaria,
        String dataEntradaSociedade
) {
}
