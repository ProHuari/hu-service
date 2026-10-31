package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.CourseModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Data access for {@link CourseModule} entities.
 */
@Repository
public interface CourseModuleRepository extends JpaRepository<CourseModule, Long> {

    /**
     * Lists the modules of a course ordered by their position.
     *
     * @param courseId the course id
     * @return the course modules in ascending position order
     */
    List<CourseModule> findByCourseIdOrderByPositionAsc(Long courseId);

    /**
     * Checks whether a position is already used inside a course.
     *
     * @param courseId the course id
     * @param position the module position
     * @return {@code true} if another module already occupies this position
     */
    boolean existsByCourseIdAndPosition(Long courseId, Integer position);
}
