package com.huariservice.huariia.services;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Fallback implementation of {@link AiAnswerGenerator} used when AI integration is disabled.
 *
 * <p>Activated when {@code app.ia.enabled=false} or when the property is absent.
 * Throws a {@link ResponseStatusException} with HTTP 503 (Service Unavailable)
 * when invoked.</p>
 */
@Component
@ConditionalOnProperty(name = "app.ia.enabled", havingValue = "false", matchIfMissing = true)
public class DisabledAiAnswerGenerator implements AiAnswerGenerator {

    /**
     * Throws an exception indicating the AI service is disabled.
     *
     * @param question the user's question (ignored)
     * @return never returns normally
     * @throws ResponseStatusException always with HTTP 503
     */
    @Override
    public String generateAnswer(String question) {
        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "AI service is not enabled in this environment."
        );
    }
}