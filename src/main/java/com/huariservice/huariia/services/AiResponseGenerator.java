package com.huariservice.huariia.services;

/**
 * Contract for generating answers with an AI provider.
 * Exactly one implementation is active, selected by the {@code app.ia.enabled} property.
 */
public interface AiResponseGenerator {

    /**
     * Generates an answer for the given question.
     *
     * @param question the user's question
     * @return the generated answer, or {@code null} if the provider returned nothing
     * @throws org.springframework.web.server.ResponseStatusException on provider/integration failures
     */
    String generateAnswer(String question);
}
