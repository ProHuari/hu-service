package com.huariservice.huariia.services;

import com.huariservice.huariia.dto.AiHistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * High-level orchestration service for querying the AI assistant.
 *
 * <p>Coordinates RAG context enrichment via {@link PoliticalTransparencyService},
 * answer generation through {@link AiAnswerGenerator}, and persists
 * the interaction via {@link AiHistoryService}.</p>
 */
@Service
@RequiredArgsConstructor
public class AiQueryService {

    private final AiAnswerGenerator aiAnswerGenerator;
    private final AiHistoryService aiHistoryService;
    private final PoliticalTransparencyService politicalTransparencyService;

    /**
     * Processes an AI query from a user with automatic RAG context from Legislative Bills.
     *
     * @param userId   the ID of the requesting user
     * @param question the prompt text
     * @return the created history record response
     * @throws ResponseStatusException HTTP 502 if the AI did not return a valid response
     */
    public AiHistoryResponse query(Long userId, String question) {
        String ragContext = politicalTransparencyService != null
                ? politicalTransparencyService.buildRagContextForQuestion(question)
                : "";
        String answer = aiAnswerGenerator.generateAnswerWithContext(question, ragContext);

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

    /**
     * Processes an AI query with RAG context and streams the response via Server-Sent Events (SSE).
     *
     * @param userId   the ID of the requesting user
     * @param question the prompt text
     * @return an {@link SseEmitter} streaming {@code chunk} events and a final {@code done} event
     */
    public SseEmitter streamQuery(Long userId, String question) {
        SseEmitter emitter = new SseEmitter(60_000L);
        CompletableFuture.runAsync(() -> {
            try {
                AiHistoryResponse saved = query(userId, question);
                String fullAnswer = saved.answer();
                int chunkSize = 80;
                for (int i = 0; i < fullAnswer.length(); i += chunkSize) {
                    String chunk = fullAnswer.substring(i, Math.min(fullAnswer.length(), i + chunkSize));
                    emitter.send(SseEmitter.event().name("chunk").data(chunk));
                }
                emitter.send(SseEmitter.event().name("done").data(saved));
                emitter.complete();
            } catch (IOException | RuntimeException ex) {
                emitter.completeWithError(ex);
            }
        });
        return emitter;
    }
}
