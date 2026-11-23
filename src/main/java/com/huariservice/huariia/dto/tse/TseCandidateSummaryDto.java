package com.huariservice.huariia.dto.tse;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

/**
 * Summary DTO of an electoral candidate retrieved from TSE (DivulgaCandContas).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TseCandidateSummaryDto(
        Long id,
        String nomeUrna,
        String nomeCompleto,
        Integer numero,
        String siglaPartido,
        String cargo,
        String uf,
        Integer anoEleicao,
        BigDecimal totalBens,
        String situacao,
        String fotoUrl
) {
}
