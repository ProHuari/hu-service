package com.huariservice.huariia.dto;

import com.huariservice.huariia.entities.CourseModule;

/**
 * Outgoing representation of a {@link CourseModule}.
 *
 * @param id          database identifier
 * @param title       module title
 * @param description optional description
 * @param position    1-based position inside the course
 * @param courseId    id of the owning course
 */
public record CourseModuleResponse(
        Long id,
        String title,
        String description,
        Integer position,
        Long courseId
) {

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
                module.getCourse().getId()
        );
    }
}