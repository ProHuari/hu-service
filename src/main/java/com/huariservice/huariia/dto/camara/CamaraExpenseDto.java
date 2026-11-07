package com.huariservice.huariia.dto.camara;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/**
 * Data transfer record mapping a parliamentary quota expense (CEAP) from the Camara dos Deputados API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CamaraExpenseDto(
        Integer ano,
        Integer mes,
        String tipoDespesa,
        Long codDocumento,
        String tipoDocumento,
        Integer codTipoDocumento,
        String dataDocumento,
        String numDocumento,
        BigDecimal valorDocumento,
        String urlDocumento,
        String nomeFornecedor,
        String cnpjCpfFornecedor,
        BigDecimal valorLiquido,
        BigDecimal valorGlosa,
        String numRessarcimento,
        Integer codLote,
        Integer parcela
) {}
