package com.huariservice.huariia.services;

import com.huariservice.huariia.dto.ai.GeminiApiDtos.GeminiRequest;
import com.huariservice.huariia.dto.ai.GeminiApiDtos.GeminiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

/**
 * Implementation of {@link AiAnswerGenerator} backed by the unified Google Gemini REST client.
 *
 * <p>Activated when {@code app.ia.enabled=true}. Uses the shared {@code geminiRestClient}
 * configured with {@code v1beta} base URL and {@code gemini-2.5-flash} default model.</p>
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "app.ia.enabled", havingValue = "true")
public class GeminiAiAnswerGenerator implements AiAnswerGenerator {

    private static final String SYSTEM_INSTRUCTION = """
            Você é o assistente de educação política, cidadania e estudos da plataforma Huari.ia. \
            Sua missão é traduzir o "juridiquês" e explicar projetos de lei (PLs), atuação parlamentar, \
            orçamento público e conteúdos de estudo de forma clara, didática, apartidária e objetiva, \
            em português do Brasil. Quando um contexto legislativo (RAG) for fornecido, utilize-o como \
            base factual principal. Se a pergunta fugir completamente de educação política, cidadania \
            ou aprendizado, diga educadamente que só pode ajudar com esses temas.""";

    private final RestClient client;
    private final String model;

    public GeminiAiAnswerGenerator(
            @Qualifier("geminiRestClient") RestClient geminiRestClient,
            @Value("${app.ai.gemini.model:${app.ia.model:gemini-2.5-flash}}") String model) {
        this.client = geminiRestClient;
        this.model = model;
    }

    @Override
    public String generateAnswer(String question) {
        try {
            GeminiRequest request = GeminiRequest.text(SYSTEM_INSTRUCTION, question);

            GeminiResponse response = client.post()
                    .uri("/models/{model}:generateContent", model)
                    .body(request)
                    .retrieve()
                    .body(GeminiResponse.class);

            return response != null ? response.extractText() : null;
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
}
