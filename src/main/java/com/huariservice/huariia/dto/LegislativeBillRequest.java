package com.huariservice.huariia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LegislativeBillRequest(
        @NotBlank(message = "Bill code is required (e.g. PL 2338/2023).")
        @Size(max = 50)
        String code,
        @NotBlank(message = "Title is required.")
        @Size(max = 200)
        String title,
        @NotBlank(message = "Summary is required.")
        String summary,
        String fullText,
        @NotBlank(message = "Status is required.")
        @Size(max = 100)
        String status,
        Long authorId
) {}
