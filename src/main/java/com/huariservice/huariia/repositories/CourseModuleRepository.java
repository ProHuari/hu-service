package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.CourseModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link CourseModule} entities.
 */
@Repository
public interface CourseModuleRepository extends JpaRepository<CourseModule, Long> {

    /**
     * Lists all modules of a course in display order.
     *
     * <p>Derived query: {@code WHERE course.id = ? ORDER BY position ASC}.</p>
     *
     * @param courseId id of the owning course
     * @return modules sorted by ascending position (empty list if none)
     */
    List<CourseModule> findByCourseIdOrderByPositionAsc(Long courseId);

    /**
     * Checks whether a position is already used inside a course, mirroring the
     * {@code (curso_id, ordem)} unique constraint so the service can return a 409.
     *
     * @param courseId id of the owning course
     * @param position the position to check
     * @return {@code true} if another module already occupies that position
     */
    boolean existsByCourseIdAndPosition(Long courseId, Integer position);
}
