package com.huariservice.huariia.DTOs;

import com.huariservice.huariia.entities.Course;

/**
 * Outgoing representation of a {@link Course}, including its nested category and author.
 *
 * @param id          database identifier
 * @param title       course title
 * @param description course description
 * @param videoUrl    presentation video URL
 * @param category    nested category summary
 * @param author      nested author summary
 */
public record CourseResponse(
        Long id,
        String title,
        String description,
        String videoUrl,
        CategoryResponse category,
        AuthorResponse author
) {

    /**
     * Converts a {@link Course} entity into its API representation.
     *
     * <p><strong>Must be called inside an active transaction</strong> ({@code @Transactional}):
     * {@code category} and {@code author} are LAZY associations and
     * {@code spring.jpa.open-in-view} is disabled.</p>
     * <ol>
     *   <li>Copies the scalar fields of the course.</li>
     *   <li>Initializes and converts the lazy category via {@link CategoryResponse#from}.</li>
     *   <li>Initializes and converts the lazy author via {@link AuthorResponse#from}.</li>
     * </ol>
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
                AuthorResponse.from(course.getAuthor())
        );
    }
}