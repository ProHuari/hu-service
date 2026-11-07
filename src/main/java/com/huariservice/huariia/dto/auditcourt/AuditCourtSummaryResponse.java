package com.huariservice.huariia.dto.auditcourt;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Consolidated response payload for the Front-end containing transparency data from the Tribunal de Contas.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuditCourtSummaryResponse(
        String provider,
        String status,
        String message,
        int totalItems,
        LocalDateTime queriedAt,
        List<AuditCourtProcessResponse> processes,
        List<AuditCourtIrregularAccountResponse> irregularAccounts,
        String sourceUrl
) {
    public static AuditCourtSummaryResponse success(
            String provider,
            List<AuditCourtProcessResponse> processes,
            List<AuditCourtIrregularAccountResponse> irregularAccounts,
            String sourceUrl) {
        int total = (processes != null ? processes.size() : 0)
                + (irregularAccounts != null ? irregularAccounts.size() : 0);
        return new AuditCourtSummaryResponse(
                provider,
                "ONLINE",
                "Dados obtidos com sucesso do Tribunal de Contas.",
                total,
                LocalDateTime.now(),
                processes,
                irregularAccounts,
                sourceUrl
        );
    }

    public static AuditCourtSummaryResponse fallback(String provider, String warningMessage, String sourceUrl) {
        return new AuditCourtSummaryResponse(
                provider,
                "DEGRADED",
                warningMessage,
                0,
                LocalDateTime.now(),
                List.of(),
                List.of(),
                sourceUrl
        );
    }
}
