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
}
