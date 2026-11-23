package com.huariservice.huariia.dto.tse;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Consolidated declared assets and patrimonial evolution of a candidate from TSE.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TseCandidateAssetsResponse(
        Long candidatoId,
        String nomeUrna,
        String nomeCompleto,
        String partido,
        String cargo,
        String uf,
        Integer anoEleicao,
        BigDecimal valorTotalBens,
        Integer quantidadeBens,
        List<TseAssetDto> bens,
        String status,
        String mensagem
) {
    public static TseCandidateAssetsResponse ofOnline(
            Long candidatoId,
            String nomeUrna,
            String nomeCompleto,
            String partido,
            String cargo,
            String uf,
            Integer anoEleicao,
            BigDecimal valorTotalBens,
            List<TseAssetDto> bens) {
        return new TseCandidateAssetsResponse(
                candidatoId,
                nomeUrna,
                nomeCompleto,
                partido,
                cargo,
                uf,
                anoEleicao,
                valorTotalBens != null ? valorTotalBens : BigDecimal.ZERO,
                bens != null ? bens.size() : 0,
                bens != null ? Collections.unmodifiableList(bens) : Collections.emptyList(),
                "ONLINE",
                "Dados patrimoniais recuperados com sucesso da base do TSE (DivulgaCandContas)."
        );
    }

    public static TseCandidateAssetsResponse ofFallback(
            Long candidatoId,
            String nomeUrna,
            String nomeCompleto,
            String partido,
            String cargo,
            String uf,
            Integer anoEleicao,
            BigDecimal valorTotalBens,
            List<TseAssetDto> bens,
            String motivo) {
        return new TseCandidateAssetsResponse(
                candidatoId,
                nomeUrna,
                nomeCompleto,
                partido,
                cargo,
                uf,
                anoEleicao,
                valorTotalBens != null ? valorTotalBens : BigDecimal.ZERO,
                bens != null ? bens.size() : 0,
                bens != null ? Collections.unmodifiableList(bens) : Collections.emptyList(),
                "FALLBACK",
                motivo != null ? motivo : "Serviço do TSE temporariamente indisponível. Exibindo dados consolidados de contingência."
        );
    }
}
