package com.huariservice.huariia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Incoming payload used to create or update a {@link com.huariservice.huariia.entities.Category}.
 *
 * @param name        unique category name (required, max 100 chars)
 * @param description optional free-text description
 */
public record CategoryRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must be at most 100 characters")
        String name,

        String description
) {}
