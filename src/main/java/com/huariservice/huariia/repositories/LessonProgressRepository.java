package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data access for {@link LessonProgress} entities.
 */
@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

    /**
     * Finds the progress record of a user for a specific lesson.
     *
     * @param userId   the user id
     * @param lessonId the lesson id
     * @return the progress record, or empty if none was registered
     */
    Optional<LessonProgress> findByUserIdAndLessonId(Long userId, Long lessonId);

    /**
     * Lists all progress records of a user.
     *
     * @param userId the user id
     * @return the user's lesson progress records
     */
    List<LessonProgress> findByUserId(Long userId);
}
