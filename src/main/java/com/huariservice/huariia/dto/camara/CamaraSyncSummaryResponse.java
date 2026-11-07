package com.huariservice.huariia.dto.camara;

import java.time.LocalDateTime;

/**
 * Summary DTO reporting the outcome of synchronizing external Camara dos Deputados data to the local database.
 */
public record CamaraSyncSummaryResponse(
        String status,
        String message,
        int totalFound,
        int createdCount,
        int updatedCount,
        LocalDateTime syncedAt
) {
    public static CamaraSyncSummaryResponse success(int totalFound, int createdCount, int updatedCount) {
        return new CamaraSyncSummaryResponse(
                "SUCCESS",
                "Sincronização de parlamentares concluída com sucesso.",
                totalFound,
                createdCount,
                updatedCount,
                LocalDateTime.now()
        );
    }

    public static CamaraSyncSummaryResponse fallback(String warningMessage) {
        return new CamaraSyncSummaryResponse(
                "DEGRADED",
                warningMessage,
                0,
                0,
                0,
                LocalDateTime.now()
        );
    }
}
