package com.huariservice.huariia.dto.auditcourt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO representing an irregular public account judgment record from the Tribunal de Contas.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AuditCourtIrregularAccountResponse(
        @JsonProperty("nome")
        String responsibleName,

        @JsonProperty("cpfCnpj")
        String documentNumber,

        @JsonProperty("cargo")
        String roleOrTitle,

        @JsonProperty("processo")
        String processNumber,

        @JsonProperty("dataJulgamento")
        String deliberationDate,

        @JsonProperty("acordao")
        String deliberationNumber
) {}
