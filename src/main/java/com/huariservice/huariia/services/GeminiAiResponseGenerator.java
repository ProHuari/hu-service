package com.huariservice.huariia.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * {@link AiResponseGenerator} backed by the Google Gemini
 * {@code generateContent} REST API.
 * Active only when {@code app.ia.enabled=true}.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "app.ia.enabled", havingValue = "true")
public class GeminiAiResponseGenerator implements AiResponseGenerator {

    /**
     * System prompt sent to the model; it is user-facing behavior, so it stays in
     * Portuguese.
     */
    private static final String SYSTEM_PROMPT = """
            Você é o assistente da plataforma de cursos Huari.ia. \
            Ajude o aluno com dúvidas sobre os conteúdos de estudo, de forma clara e objetiva, \
            em português do Brasil. Se a pergunta fugir completamente de estudo e aprendizado, \
            diga educadamente que só pode ajudar com isso.""";

    private final RestClient client;
    private final String model;

    /**
     * Creates the Gemini generator.
     *
     * @param aiRestClient preconfigured REST client (base URL, API key, timeouts)
     * @param model        Gemini model name, from {@code app.ia.model}
     */
    public GeminiAiResponseGenerator(RestClient aiRestClient,
            @Value("${app.ia.model:gemini-1.5-flash}") String model) {
        this.client = aiRestClient;
        this.model = model;
    }

    /**
     * Asks Gemini for an answer.
     *
     * @param question the user's question
     * @return the concatenated text parts of the first candidate, or {@code null}
     *         if empty
     * @throws ResponseStatusException 503 on rate limiting (429), 502 on other API
     *                                 errors,
     *                                 504 on timeouts/connection failures
     */
    @Override
    public String generateAnswer(String question) {
        try {
            GenerateRequest request = new GenerateRequest(
                    new SystemContent(List.of(new Part(SYSTEM_PROMPT))),
                    List.of(new Content(List.of(new Part(question)))));

            GenerateResponse response = client.post()
                    .uri("/v1beta/models/{model}:generateContent", model)
                    .body(request)
                    .retrieve()
                    .body(GenerateResponse.class);

            if (response == null || response.candidates() == null || response.candidates().isEmpty()) {
                return null;
            }

            Candidate candidate = response.candidates().get(0);
            if (candidate.content() == null || candidate.content().parts() == null) {
                return null;
            }

            return candidate.content().parts().stream()
                    .map(Part::text)
                    .filter(t -> t != null && !t.isBlank())
                    .reduce("", String::concat)
                    .trim();

        } catch (RestClientResponseException e) {
            int status = e.getStatusCode().value();
            log.error("Gemini API failure: status {}", status);
            if (status == 429) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                        "A IA está sobrecarregada ou atingiu o limite gratuito. Tente novamente em instantes.");
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Não foi possível obter resposta da IA.");
        } catch (ResourceAccessException e) {
            log.error("Timeout or connection failure with the Gemini API: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT,
                    "A IA demorou demais para responder.");
        }
    }

    private record Part(String text) {
    }

    private record Content(List<Part> parts) {
    }

    private record SystemContent(@JsonProperty("parts") List<Part> parts) {
    }

    private record GenerateRequest(
            @JsonProperty("systemInstruction") SystemContent systemInstruction,
            List<Content> contents) {
    }

    private record Candidate(Content content) {
    }

    private record GenerateResponse(List<Candidate> candidates) {
    }
}
