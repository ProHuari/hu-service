package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Data access for {@link Course} entities.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Checks whether a course with the given title already exists.
     *
     * @param title the course title
     * @return {@code true} if the title is already taken
     */
    boolean existsByTitle(String title);

    /**
     * Checks whether a course with the given video URL already exists.
     *
     * @param videoUrl the course video URL
     * @return {@code true} if the video URL is already taken
     */
    boolean existsByVideoUrl(String videoUrl);
}
