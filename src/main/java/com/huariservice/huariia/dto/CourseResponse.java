package com.huariservice.huariia.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.huariservice.huariia.entities.Course;

import java.util.Collections;
import java.util.List;

/**
 * Outgoing representation of a {@link Course}, including its nested category, author, and modules.
 *
 * @param id          database identifier
 * @param title       course title
 * @param description course description
 * @param videoUrl    presentation video URL
 * @param category    nested category summary
 * @param author      nested author summary
 * @param modules     ordered course modules with lessons
 */
public record CourseResponse(
        Long id,
        String title,
        String description,
        String videoUrl,
        CategoryResponse category,
        AuthorResponse author,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<CourseModuleResponse> modules
) {

    public CourseResponse(Long id, String title, String description, String videoUrl, CategoryResponse category, AuthorResponse author) {
        this(id, title, description, videoUrl, category, author, Collections.emptyList());
    }

    /**
     * Converts a {@link Course} entity into its API representation.
     *
     * <p><strong>Must be called inside an active transaction</strong> ({@code @Transactional}):
     * {@code category} and {@code author} are LAZY associations and
     * {@code spring.jpa.open-in-view} is disabled.</p>
     *
     * @param course the entity to convert (must not be {@code null})
     * @return a new immutable {@code CourseResponse}
     */
    public static CourseResponse from(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getVideoUrl(),
                CategoryResponse.from(course.getCategory()),
                AuthorResponse.from(course.getAuthor()),
                Collections.emptyList()
        );
    }

    public static CourseResponse fromWithModules(Course course, List<CourseModuleResponse> modules) {
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getVideoUrl(),
                CategoryResponse.from(course.getCategory()),
                AuthorResponse.from(course.getAuthor()),
                modules != null ? modules : Collections.emptyList()
        );
    }
}
