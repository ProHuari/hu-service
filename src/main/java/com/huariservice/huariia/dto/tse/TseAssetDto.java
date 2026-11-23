package com.huariservice.huariia.dto.tse;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

/**
 * Itemized declared asset from a candidate's patrimony declaration in TSE.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TseAssetDto(
        Integer ordem,
        String descricao,
        String tipo,
        BigDecimal valor
) {
}
