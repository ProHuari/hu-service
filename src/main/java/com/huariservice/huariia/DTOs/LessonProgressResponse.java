package com.huariservice.huariia.DTOs;

import com.huariservice.huariia.entities.LessonProgress;
import com.huariservice.huariia.entities.enums.LessonStatus;

import java.time.LocalDateTime;

/**
 * Outgoing representation of a {@link LessonProgress} record.
 *
 * @param id          database identifier
 * @param status      current progress state
 * @param completedAt completion timestamp ({@code null} unless status is {@code COMPLETED})
 * @param userId      id of the learner
 * @param lessonId    id of the lesson
 */
public record LessonProgressResponse(
        Long id,
        LessonStatus status,
        LocalDateTime completedAt,
        Long userId,
        Long lessonId
) {

    /**
     * Converts a {@link LessonProgress} entity into its API representation.
     *
     * @param progress the entity to convert (must not be {@code null})
     * @return a new immutable {@code LessonProgressResponse}
     */
    public static LessonProgressResponse from(LessonProgress progress) {
        return new LessonProgressResponse(
                progress.getId(),
                progress.getStatus(),
                progress.getCompletedAt(),
                progress.getUser().getId(),
                progress.getLesson().getId()
        );
    }
}
