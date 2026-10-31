package com.huariservice.huariia.services;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Fallback {@link AiResponseGenerator} used when the AI integration is disabled
 * ({@code app.ia.enabled=false} or missing).
 */
@Component
@ConditionalOnProperty(name = "app.ia.enabled", havingValue = "false", matchIfMissing = true)
public class DisabledAiResponseGenerator implements AiResponseGenerator {

    /**
     * Always fails, signaling that the AI service is not available in this environment.
     *
     * @param question ignored
     * @return never returns normally
     * @throws ResponseStatusException always, with status 503
     */
    @Override
    public String generateAnswer(String question) {
        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "O serviço de IA não está habilitado no ambiente."
        );
    }
}
