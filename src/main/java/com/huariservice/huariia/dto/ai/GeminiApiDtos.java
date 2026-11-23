package com.huariservice.huariia.dto.ai;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Shared request/response payload records for Google Gemini Generative Language API (v1beta).
 */
public final class GeminiApiDtos {

    private GeminiApiDtos() {
    }

    public record ContentPart(String text) {
    }

    public record Content(List<ContentPart> parts) {
    }

    public record SystemContent(@JsonProperty("parts") List<ContentPart> parts) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record GenerationConfig(
            @JsonProperty("responseMimeType") String responseMimeType
    ) {
        public static GenerationConfig jsonOutput() {
            return new GenerationConfig("application/json");
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record GeminiRequest(
            @JsonProperty("systemInstruction") SystemContent systemInstruction,
            List<Content> contents,
            @JsonProperty("generationConfig") GenerationConfig generationConfig
    ) {
        public static GeminiRequest text(String systemInstruction, String userPrompt) {
            return new GeminiRequest(
                    new SystemContent(List.of(new ContentPart(systemInstruction))),
                    List.of(new Content(List.of(new ContentPart(userPrompt)))),
                    null
            );
        }

        public static GeminiRequest structuredJson(String systemInstruction, String userPrompt) {
            return new GeminiRequest(
                    new SystemContent(List.of(new ContentPart(systemInstruction))),
                    List.of(new Content(List.of(new ContentPart(userPrompt)))),
                    GenerationConfig.jsonOutput()
            );
        }
    }

    public record Candidate(Content content) {
    }

    public record GeminiResponse(List<Candidate> candidates) {

        public String extractText() {
            if (candidates == null || candidates.isEmpty()) {
                return null;
            }
            Candidate first = candidates.get(0);
            if (first == null || first.content() == null || first.content().parts() == null) {
                return null;
            }
            String text = first.content().parts().stream()
                    .map(ContentPart::text)
                    .filter(t -> t != null && !t.isBlank())
                    .reduce("", String::concat)
                    .trim();
            return text.isBlank() ? null : text;
        }
    }
}
