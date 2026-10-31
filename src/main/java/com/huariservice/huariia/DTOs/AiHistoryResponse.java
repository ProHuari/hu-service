package com.huariservice.huariia.DTOs;

import com.huariservice.huariia.entities.AiHistory;

import java.time.LocalDateTime;

/**
 * Outgoing representation of an {@link AiHistory} entry (one question/answer pair).
 *
 * @param id        database identifier
 * @param question  the question asked
 * @param answer    the AI-generated answer
 * @param queriedAt when the query was made
 * @param userId    id of the user who asked
 */
public record AiHistoryResponse(
        Long id,
        String question,
        String answer,
        LocalDateTime queriedAt,
        Long userId
) {

    /**
     * Converts an {@link AiHistory} entity into its API representation.
     *
     * @param history the entity to convert (must not be {@code null})
     * @return a new immutable {@code AiHistoryResponse}
     */
    public static AiHistoryResponse from(AiHistory history) {
        return new AiHistoryResponse(
                history.getId(),
                history.getQuestion(),
                history.getAnswer(),
                history.getQueriedAt(),
                history.getUser().getId()
        );
    }
}
