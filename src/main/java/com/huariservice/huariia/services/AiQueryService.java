package com.huariservice.huariia.services;

import com.huariservice.huariia.dto.AiHistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * High-level orchestration service for querying the AI assistant.
 *
 * <p>Coordinates answer generation through {@link AiAnswerGenerator} and persists
 * the interaction via {@link AiHistoryService}.</p>
 */
@Service
@RequiredArgsConstructor
public class AiQueryService {

    private final AiAnswerGenerator aiAnswerGenerator;
    private final AiHistoryService aiHistoryService;

    /**
     * Processes an AI query from a user.
     * <ol>
     *   <li>Requests an answer from the configured {@link AiAnswerGenerator}.</li>
     *   <li>Validates that a non-blank response was returned.</li>
     *   <li>Saves the question and answer to the user's history and returns the DTO.</li>
     * </ol>
     *
     * @param userId   the ID of the requesting user
     * @param question the prompt text
     * @return the created history record response
     * @throws ResponseStatusException HTTP 502 if the AI did not return a valid response
     */
    public AiHistoryResponse query(Long userId, String question) {
        String answer = aiAnswerGenerator.generateAnswer(question);

        if (answer == null || answer.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "AI did not return a valid response."
            );
        }

        return aiHistoryService.saveHistory(
                userId,
                question,
                answer
        );
    }
}
