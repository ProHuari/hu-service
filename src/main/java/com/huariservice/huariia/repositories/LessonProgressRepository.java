package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.LessonProgress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link LessonProgress} entities.
 */
@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

    Optional<LessonProgress> findByUserIdAndLessonId(Long userId, Long lessonId);

    List<LessonProgress> findByUserId(Long userId);

    Page<LessonProgress> findByUserId(Long userId, Pageable pageable);
}
