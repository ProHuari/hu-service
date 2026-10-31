package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Course} entities.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Checks whether a course with the given title already exists.
     *
     * @param title the course title to check
     * @return {@code true} if the title is already taken
     */
    boolean existsByTitle(String title);

    /**
     * Checks whether a course already uses the given presentation video URL.
     *
     * @param videoUrl the video URL to check
     * @return {@code true} if the URL is already registered
     */
    boolean existsByVideoUrl(String videoUrl);
}
