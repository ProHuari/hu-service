package com.huariservice.huariia.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.huariservice.huariia.entities.CourseModule;

import java.util.Collections;
import java.util.List;

/**
 * Outgoing representation of a {@link CourseModule}.
 *
 * @param id          database identifier
 * @param title       module title
 * @param description optional description
 * @param position    1-based position inside the course
 * @param courseId    id of the owning course
 * @param lessons     ordered lessons in this module
 */
public record CourseModuleResponse(
        Long id,
        String title,
        String description,
        Integer position,
        Long courseId,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<LessonResponse> lessons
) {

    public CourseModuleResponse(Long id, String title, String description, Integer position, Long courseId) {
        this(id, title, description, position, courseId, Collections.emptyList());
    }

    /**
     * Converts a {@link CourseModule} entity into its API representation.
     *
     * <p>Only the course id is read, which Hibernate can resolve from the LAZY proxy
     * without hitting the database.</p>
     *
     * @param module the entity to convert (must not be {@code null})
     * @return a new immutable {@code CourseModuleResponse}
     */
    public static CourseModuleResponse from(CourseModule module) {
        return new CourseModuleResponse(
                module.getId(),
                module.getTitle(),
                module.getDescription(),
                module.getPosition(),
                module.getCourse().getId(),
                Collections.emptyList()
        );
    }

    public static CourseModuleResponse fromWithLessons(CourseModule module, List<LessonResponse> lessons) {
        return new CourseModuleResponse(
                module.getId(),
                module.getTitle(),
                module.getDescription(),
                module.getPosition(),
                module.getCourse().getId(),
                lessons != null ? lessons : Collections.emptyList()
        );
    }
}
