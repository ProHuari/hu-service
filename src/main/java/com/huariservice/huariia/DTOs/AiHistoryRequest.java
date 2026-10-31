package com.huariservice.huariia.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Incoming payload containing a question for the AI assistant.
 *
 * @param question the user's prompt (required, max 2000 chars to bound token usage)
 */
public record AiHistoryRequest(
        @NotBlank(message = "Question is required.")
        @Size(max = 2000, message = "Question must be at most 2000 characters.")
        String question
) {}