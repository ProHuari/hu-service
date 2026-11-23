package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.CourseModule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Spring Data JPA repository for {@link CourseModule} entities.
 */
@Repository
public interface CourseModuleRepository extends JpaRepository<CourseModule, Long> {

    List<CourseModule> findByCourseIdOrderByPositionAsc(Long courseId);

    List<CourseModule> findByCourseIdInOrderByCourseIdAscPositionAsc(Collection<Long> courseIds);

    Page<CourseModule> findByCourseIdOrderByPositionAsc(Long courseId, Pageable pageable);

    boolean existsByCourseIdAndPosition(Long courseId, Integer position);
}
