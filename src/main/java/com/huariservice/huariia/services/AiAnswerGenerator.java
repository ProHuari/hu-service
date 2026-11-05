package com.huariservice.huariia.services;

/**
 * Strategy interface defining the contract for generating AI answers.
 *
 * <p>Concrete implementations can integrate with third-party providers
 * (e.g., Google Gemini) or return fallback responses when AI is disabled.</p>
 */
public interface AiAnswerGenerator {

    /**
     * Generates a text answer for a given user prompt/question.
     *
     * @param question the user's question or prompt
     * @return generated answer text, or {@code null} / empty if no valid answer was produced
     */
    String generateAnswer(String question);

    /**
     * Generates a text answer enriched with retrieved RAG context (e.g., Legislative Bills).
     *
     * @param question the user's question or prompt
     * @param context  optional retrieved context to ground the response
     * @return generated answer text, or {@code null} / empty if no valid answer was produced
     */
    default String generateAnswerWithContext(String question, String context) {
        if (context == null || context.isBlank()) {
            return generateAnswer(question);
        }
        return generateAnswer(context + "\nPergunta do usuário: " + question);
    }
}
