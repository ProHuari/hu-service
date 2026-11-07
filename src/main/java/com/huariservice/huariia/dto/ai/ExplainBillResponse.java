package com.huariservice.huariia.dto.ai;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Response payload containing citizen-friendly explanation and practical impact analysis of a bill.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExplainBillResponse(
        String billCode,
        String originalEmenta,
        String simplifiedExplanation,
        String practicalImpacts,
        List<String> pointsInFavor,
        List<String> pointsAgainst,
        String mode,
        LocalDateTime generatedAt
) {
    public ExplainBillResponse {
        if (pointsInFavor == null) {
            pointsInFavor = Collections.emptyList();
        }
        if (pointsAgainst == null) {
            pointsAgainst = Collections.emptyList();
        }
        if (generatedAt == null) {
            generatedAt = LocalDateTime.now();
        }
    }

    public static ExplainBillResponse ofReal(
            String billCode,
            String originalEmenta,
            String simplifiedExplanation,
            String practicalImpacts,
            List<String> pros,
            List<String> cons) {
        return new ExplainBillResponse(
                billCode,
                originalEmenta,
                simplifiedExplanation,
                practicalImpacts,
                pros,
                cons,
                "REAL_API",
                LocalDateTime.now()
        );
    }

    public static ExplainBillResponse ofSimulation(
            String billCode,
            String originalEmenta,
            String simplifiedExplanation,
            String practicalImpacts,
            List<String> pros,
            List<String> cons) {
        return new ExplainBillResponse(
                billCode,
                originalEmenta,
                simplifiedExplanation,
                practicalImpacts,
                pros,
                cons,
                "FALLBACK_SIMULATION",
                LocalDateTime.now()
        );
    }
}
