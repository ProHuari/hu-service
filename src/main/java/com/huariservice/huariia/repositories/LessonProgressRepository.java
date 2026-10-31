package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link LessonProgress} entities.
 */
@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

    /**
     * Finds the progress record of a user for a specific lesson. Used to decide whether to
     * create a new record or update the existing one (upsert behavior).
     *
     * @param userId   id of the learner
     * @param lessonId id of the lesson
     * @return the progress record, or {@link Optional#empty()} if the user never touched the lesson
     */
    Optional<LessonProgress> findByUserIdAndLessonId(Long userId, Long lessonId);

    /**
     * Lists all progress records of a user across every lesson.
     *
     * @param userId id of the learner
     * @return the user's progress records (empty list if none)
     */
    List<LessonProgress> findByUserId(Long userId);
}
