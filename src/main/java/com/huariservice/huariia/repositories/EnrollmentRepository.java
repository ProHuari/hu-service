package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Data access for {@link Enrollment} entities.
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    /**
     * Checks whether a user is already enrolled in a course (a user can enroll only once).
     *
     * @param userId   the user id
     * @param courseId the course id
     * @return {@code true} if the enrollment already exists
     */
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    /**
     * Lists all enrollments of a user ("my courses").
     *
     * @param userId the user id
     * @return the user's enrollments
     */
    List<Enrollment> findByUserId(Long userId);
}
