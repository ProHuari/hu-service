package com.huariservice.huariia.dto.ai;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Response payload from the Citizen AI Tutor.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AiTutorChatResponse(
        String question,
        String answer,
        String subject,
        String mode,
        List<String> suggestedTopics,
        Long historyId,
        LocalDateTime answeredAt
) {
    public AiTutorChatResponse {
        if (suggestedTopics == null) {
            suggestedTopics = Collections.emptyList();
        }
        if (answeredAt == null) {
            answeredAt = LocalDateTime.now();
        }
    }

    public static AiTutorChatResponse ofReal(
            String question,
            String answer,
            String subject,
            List<String> suggestedTopics,
            Long historyId) {
        return new AiTutorChatResponse(
                question,
                answer,
                subject,
                "REAL_API",
                suggestedTopics,
                historyId,
                LocalDateTime.now()
        );
    }

    public static AiTutorChatResponse ofSimulation(
            String question,
            String answer,
            String subject,
            List<String> suggestedTopics,
            Long historyId) {
        return new AiTutorChatResponse(
                question,
                answer,
                subject,
                "FALLBACK_SIMULATION",
                suggestedTopics,
                historyId,
                LocalDateTime.now()
        );
    }
}
