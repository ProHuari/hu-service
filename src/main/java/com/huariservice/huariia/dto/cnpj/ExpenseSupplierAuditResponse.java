package com.huariservice.huariia.dto.cnpj;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.huariservice.huariia.dto.ParliamentarianExpenseResponse;

import java.util.Collections;
import java.util.List;

/**
 * Audit cross-reference combining a public parliamentary expense (CEAP)
 * with the corporate profile and civic risk indicators of the receiving supplier.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExpenseSupplierAuditResponse(
        ParliamentarianExpenseResponse despesa,
        CnpjCompanyResponse fornecedor,
        List<String> alertasConsolidados,
        String nivelRisco,
        String mensagem
) {
    public static ExpenseSupplierAuditResponse of(
            ParliamentarianExpenseResponse despesa,
            CnpjCompanyResponse fornecedor,
            List<String> alertasConsolidados,
            String nivelRisco,
            String mensagem) {
        return new ExpenseSupplierAuditResponse(
                despesa,
                fornecedor,
                alertasConsolidados != null ? Collections.unmodifiableList(alertasConsolidados) : Collections.emptyList(),
                nivelRisco != null ? nivelRisco : "BAIXO",
                mensagem != null ? mensagem : "Auditoria cruzada de fornecedor concluída."
        );
    }
}
