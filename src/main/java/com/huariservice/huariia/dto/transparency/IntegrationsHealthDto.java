package com.huariservice.huariia.dto.transparency;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Health and status overview DTO of all external integrations for the Frontend monitoring dashboard.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record IntegrationsHealthDto(
        LocalDateTime checkedAt,
        String overallStatus,
        List<IntegrationItemStatusDto> integrations
) {
    public record IntegrationItemStatusDto(
            String id,
            String name,
            String category,
            String baseUrl,
            String status,
            String details
    ) {}
}
