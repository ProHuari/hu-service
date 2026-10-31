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
 * Implementation of {@link AiAnswerGenerator} backed by the Google Gemini REST API.
 *
 * <p>Activated when {@code app.ia.enabled=true}. Sends the prompt to the configured Gemini model
 * with predefined system instructions tailored to the platform.</p>
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "app.ia.enabled", havingValue = "true")
public class GeminiAiAnswerGenerator implements AiAnswerGenerator {

    private static final String SYSTEM_INSTRUCTION = """
            Você é o assistente da plataforma de cursos Huari.ia. \
            Ajude o aluno com dúvidas sobre os conteúdos de estudo, de forma clara e objetiva, \
            em português do Brasil. Se a pergunta fugir completamente de estudo e aprendizado, \
            diga educadamente que só pode ajudar com isso.""";

    private final RestClient client;
    private final String model;

    public GeminiAiAnswerGenerator(RestClient iaRestClient,
                                   @Value("${app.ia.model:gemini-3.5-flash-lite}") String model) {
        this.client = iaRestClient;
        this.model = model;
    }

    /**
     * Calls the Gemini generateContent endpoint and extracts the text response.
     * <ol>
     *   <li>Constructs the payload with system instruction and the user query.</li>
     *   <li>Executes the HTTP POST request via {@link RestClient}.</li>
     *   <li>Extracts text fragments from the candidate response.</li>
     * </ol>
     *
     * @param question the user prompt
     * @return extracted response text or {@code null} if empty
     * @throws ResponseStatusException on rate limit (503), timeout (504), or remote error (502)
     */
    @Override
    public String generateAnswer(String question) {
        try {
            GeminiRequest request = new GeminiRequest(
                    new SystemContent(List.of(new ContentPart(SYSTEM_INSTRUCTION))),
                    List.of(new Content(List.of(new ContentPart(question))))
            );

            GeminiResponse response = client.post()
                    .uri("/v1beta/models/{modelo}:generateContent", model)
                    .body(request)
                    .retrieve()
                    .body(GeminiResponse.class);

            if (response == null || response.candidates() == null || response.candidates().isEmpty()) {
                return null;
            }

            Candidate candidate = response.candidates().get(0);
            if (candidate.content() == null || candidate.content().parts() == null) {
                return null;
            }

            return candidate.content().parts().stream()
                    .map(ContentPart::text)
                    .filter(t -> t != null && !t.isBlank())
                    .reduce("", String::concat)
                    .trim();

        } catch (RestClientResponseException e) {
            log.error("Gemini API call failed: status {}", e.getStatusCode().value());
            int status = e.getStatusCode().value();
            if (status == 429) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                        "AI service is currently busy or reached rate limit. Please try again shortly.");
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Failed to retrieve response from AI provider.");
        } catch (ResourceAccessException e) {
            log.error("Timeout or connection failure with Gemini API: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT,
                    "AI service timed out while generating response.");
        }
    }

    private record ContentPart(String text) {}

    private record Content(List<ContentPart> parts) {}

    private record SystemContent(@JsonProperty("parts") List<ContentPart> parts) {}

    private record GeminiRequest(
            @JsonProperty("systemInstruction") SystemContent systemInstruction,
            List<Content> contents) {}

    private record Candidate(Content content) {}

    private record GeminiResponse(List<Candidate> candidates) {}
}
