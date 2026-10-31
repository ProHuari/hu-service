package com.huariservice.huariia.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

/**
 * Incoming payload used to create or update a {@link com.huariservice.huariia.entities.Lesson}.
 *
 * @param title             lesson title (required, max 155 chars)
 * @param description       lesson summary (required)
 * @param videoUrl          unique video URL (required, valid URL, max 254 chars)
 * @param position          1-based position inside the module (required, unique per module)
 * @param durationInMinutes optional duration in minutes (non-negative)
 * @param moduleId          id of the owning module (required)
 */
public record LessonRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 155, message = "Title must be at most 155 characters")
        String title,

        @NotBlank(message = "Description is required")
        String description,

        @NotBlank(message = "Video URL is required")
        @Size(max = 254, message = "Video URL must be at most 254 characters")
        @URL(message = "Invalid video URL")
        String videoUrl,

        @NotNull(message = "Position is required")
        @Min(value = 1, message = "Position must be at least 1")
        Integer position,

        @Min(value = 0, message = "Duration cannot be negative")
        Integer durationInMinutes,

        @NotNull(message = "Module is required")
        Long moduleId
) {}
