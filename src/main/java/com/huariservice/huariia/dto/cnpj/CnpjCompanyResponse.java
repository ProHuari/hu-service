package com.huariservice.huariia.dto.cnpj;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Company corporate and registration profile retrieved from BrasilAPI,
 * enriched with civic transparency audit alerts.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CnpjCompanyResponse(
        String cnpj,
        String cnpjFormatado,
        String razaoSocial,
        String nomeFantasia,
        String situacaoCadastral,
        String dataSituacaoCadastral,
        String dataInicioAtividade,
        Integer cnaeFiscal,
        String cnaeFiscalDescricao,
        BigDecimal capitalSocial,
        String porte,
        String logradouro,
        String numero,
        String municipio,
        String uf,
        String cep,
        List<CnpjPartnerDto> qsa,
        List<String> alertasTransparencia,
        String status,
        String mensagem
) {
    public static CnpjCompanyResponse ofOnline(
            String cnpj,
            String cnpjFormatado,
            String razaoSocial,
            String nomeFantasia,
            String situacaoCadastral,
            String dataSituacaoCadastral,
            String dataInicioAtividade,
            Integer cnaeFiscal,
            String cnaeFiscalDescricao,
            BigDecimal capitalSocial,
            String porte,
            String logradouro,
            String numero,
            String municipio,
            String uf,
            String cep,
            List<CnpjPartnerDto> qsa,
            List<String> alertasTransparencia) {
        return new CnpjCompanyResponse(
                cnpj,
                cnpjFormatado,
                razaoSocial,
                nomeFantasia,
                situacaoCadastral,
                dataSituacaoCadastral,
                dataInicioAtividade,
                cnaeFiscal,
                cnaeFiscalDescricao,
                capitalSocial != null ? capitalSocial : BigDecimal.ZERO,
                porte,
                logradouro,
                numero,
                municipio,
                uf,
                cep,
                qsa != null ? Collections.unmodifiableList(qsa) : Collections.emptyList(),
                alertasTransparencia != null ? Collections.unmodifiableList(alertasTransparencia) : Collections.emptyList(),
                "ONLINE",
                "Dados cadastrais da empresa recuperados com sucesso via BrasilAPI."
        );
    }

    public static CnpjCompanyResponse ofFallback(
            String cnpj,
            String cnpjFormatado,
            String razaoSocial,
            String nomeFantasia,
            String situacaoCadastral,
            String dataInicioAtividade,
            String cnaeFiscalDescricao,
            BigDecimal capitalSocial,
            String municipio,
            String uf,
            List<CnpjPartnerDto> qsa,
            List<String> alertasTransparencia,
            String motivo) {
        return new CnpjCompanyResponse(
                cnpj,
                cnpjFormatado,
                razaoSocial,
                nomeFantasia,
                situacaoCadastral,
                null,
                dataInicioAtividade,
                null,
                cnaeFiscalDescricao,
                capitalSocial != null ? capitalSocial : BigDecimal.ZERO,
                "DEMAIS",
                "Endereço Comercial de Contingência",
                "SN",
                municipio,
                uf,
                "00000000",
                qsa != null ? Collections.unmodifiableList(qsa) : Collections.emptyList(),
                alertasTransparencia != null ? Collections.unmodifiableList(alertasTransparencia) : Collections.emptyList(),
                "FALLBACK",
                motivo != null ? motivo : "Serviço da BrasilAPI indisponível. Exibindo dados locais consolidados de contingência."
        );
    }
}
