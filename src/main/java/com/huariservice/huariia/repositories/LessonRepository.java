package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Lesson} entities.
 */
@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    /**
     * Lists all lessons of a module in display order.
     *
     * <p>Derived query: {@code WHERE module.id = ? ORDER BY position ASC}.</p>
     *
     * @param moduleId id of the owning module
     * @return lessons sorted by ascending position (empty list if none)
     */
    List<Lesson> findByModuleIdOrderByPositionAsc(Long moduleId);

    /**
     * Checks whether a position is already used inside a module, mirroring the
     * {@code (modulo_id, ordem)} unique constraint.
     *
     * @param moduleId id of the owning module
     * @param position the position to check
     * @return {@code true} if another lesson already occupies that position
     */
    boolean existsByModuleIdAndPosition(Long moduleId, Integer position);

    /**
     * Checks whether a lesson already uses the given video URL.
     *
     * @param videoUrl the video URL to check
     * @return {@code true} if the URL is already registered
     */
    boolean existsByVideoUrl(String videoUrl);
}
