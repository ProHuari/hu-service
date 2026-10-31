package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Data access for {@link Lesson} entities.
 */
@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    /**
     * Lists the lessons of a module ordered by their position.
     *
     * @param moduleId the module id
     * @return the module lessons in ascending position order
     */
    List<Lesson> findByModuleIdOrderByPositionAsc(Long moduleId);

    /**
     * Checks whether a position is already used inside a module.
     *
     * @param moduleId the module id
     * @param position the lesson position
     * @return {@code true} if another lesson already occupies this position
     */
    boolean existsByModuleIdAndPosition(Long moduleId, Integer position);

    /**
     * Checks whether a lesson with the given video URL already exists.
     *
     * @param videoUrl the lesson video URL
     * @return {@code true} if the video URL is already taken
     */
    boolean existsByVideoUrl(String videoUrl);
}
