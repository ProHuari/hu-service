package com.huariservice.huariia.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Incoming payload used to create or update a {@link com.huariservice.huariia.entities.CourseModule}.
 *
 * @param title       module title (required, max 150 chars)
 * @param description optional description
 * @param position    1-based position inside the course (required, unique per course)
 * @param courseId    id of the owning course (required)
 */
public record CourseModuleRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 150, message = "Title must be at most 150 characters")
        String title,

        String description,

        @NotNull(message = "Position is required")
        @Min(value = 1, message = "Position must be at least 1")
        Integer position,

        @NotNull(message = "Course is required")
        Long courseId
) {}