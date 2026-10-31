package com.huariservice.huariia.DTOs;

import com.huariservice.huariia.entities.Lesson;

/**
 * Outgoing representation of a {@link Lesson}.
 *
 * @param id                database identifier
 * @param title             lesson title
 * @param description       lesson summary
 * @param videoUrl          video URL
 * @param position          1-based position inside the module
 * @param durationInMinutes duration in minutes (may be {@code null})
 * @param moduleId          id of the owning module
 */
public record LessonResponse(
        Long id,
        String title,
        String description,
        String videoUrl,
        Integer position,
        Integer durationInMinutes,
        Long moduleId
) {

    /**
     * Converts a {@link Lesson} entity into its API representation.
     *
     * @param lesson the entity to convert (must not be {@code null})
     * @return a new immutable {@code LessonResponse}
     */
    public static LessonResponse from(Lesson lesson) {
        return new LessonResponse(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getDescription(),
                lesson.getVideoUrl(),
                lesson.getPosition(),
                lesson.getDurationInMinutes(),
                lesson.getModule().getId()
        );
    }
}