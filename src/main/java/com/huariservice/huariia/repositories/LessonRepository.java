package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Lesson} entities.
 */
@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findByModuleIdOrderByPositionAsc(Long moduleId);

    Page<Lesson> findByModuleIdOrderByPositionAsc(Long moduleId, Pageable pageable);

    boolean existsByModuleIdAndPosition(Long moduleId, Integer position);

    boolean existsByVideoUrl(String videoUrl);
}
