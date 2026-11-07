package com.huariservice.huariia.dto.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for simplifying a legislative bill's formal text ("juridiques") for citizens.
 */
public record ExplainBillRequest(
        @NotBlank(message = "O código ou identificação do projeto de lei é obrigatório")
        @Size(max = 100, message = "O código do projeto de lei não pode ultrapassar 100 caracteres")
        String billCode,

        @NotBlank(message = "A ementa ou texto do projeto de lei é obrigatória")
        @Size(max = 10000, message = "O texto da ementa não pode ultrapassar 10000 caracteres")
        String ementa,

        String author,
        String theme
) {}
