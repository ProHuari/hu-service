package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.AiHistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Orchestrates an AI query: asks the configured {@link AiResponseGenerator}
 * and stores the question/answer pair in the user's history.
 */
@Service
@RequiredArgsConstructor
public class AiQueryService {

    private final AiResponseGenerator aiResponseGenerator;
    private final AiHistoryService aiHistoryService;

    /**
     * Sends a question to the AI and records the answer.
     *
     * @param userId   the authenticated user's id
     * @param question the question to ask
     * @return the stored history entry containing the answer
     * @throws ResponseStatusException 502 if the AI returns an empty answer, or the
     *                                 generator's own status on integration failures
     */
    public AiHistoryResponse ask(Long userId, String question) {
        String answer = aiResponseGenerator.generateAnswer(question);

        if (answer == null || answer.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "A IA não retornou uma resposta válida."
            );
        }

        return aiHistoryService.record(userId, question, answer);
    }
}
