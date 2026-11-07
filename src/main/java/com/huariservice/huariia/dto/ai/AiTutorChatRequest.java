package com.huariservice.huariia.dto.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for the Citizen AI Tutor asking citizenship, political education, or course questions.
 */
public record AiTutorChatRequest(
        @NotBlank(message = "A dúvida ou pergunta é obrigatória")
        @Size(max = 2000, message = "A pergunta não pode ultrapassar 2000 caracteres")
        String question,

        String subject,
        String lessonContext
) {}
