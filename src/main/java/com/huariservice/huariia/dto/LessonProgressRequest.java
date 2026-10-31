package com.huariservice.huariia.dto;

import com.huariservice.huariia.entities.enums.LessonStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Incoming payload used by a student to create or update their progress on a lesson.
 *
 * <p>The user is resolved from the authenticated JWT, not from the body.</p>
 *
 * @param status   new progress state (required)
 * @param lessonId id of the lesson being tracked (required)
 */
public record LessonProgressRequest(
        @NotNull(message = "Status is required")
        LessonStatus status,

        @NotNull(message = "Lesson is required")
        Long lessonId
) {}