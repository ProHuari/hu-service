package com.huariservice.huariia.dto.auditcourt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO representing a legal audit/oversight process from the Tribunal de Contas (TCU / TCE).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AuditCourtProcessResponse(
        @JsonProperty("numeroProcesso")
        String processNumber,

        @JsonProperty("relator")
        String relator,

        @JsonProperty("situacao")
        String status,

        @JsonProperty("assunto")
        String subject,

        @JsonProperty("orgao")
        String organ,

        @JsonProperty("ano")
        Integer decisionYear
) {}
