package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Enrollment} entities.
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    /**
     * Checks whether a user is already enrolled in a course, so a user can enroll only once
     * (mirrors the {@code (usuario_id, curso_id)} unique constraint).
     *
     * @param userId   id of the user
     * @param courseId id of the course
     * @return {@code true} if the enrollment already exists
     */
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    /**
     * Lists every enrollment of a user — backs the "my courses" screen.
     *
     * @param userId id of the user
     * @return the user's enrollments (empty list if none)
     */
    List<Enrollment> findByUserId(Long userId);
}
