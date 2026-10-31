package com.huariservice.huariia.dto;

import com.huariservice.huariia.entities.Enrollment;
import com.huariservice.huariia.entities.enums.EnrollmentStatus;

import java.time.LocalDate;

/**
 * Outgoing representation of an {@link Enrollment}.
 *
 * @param id             database identifier
 * @param enrollmentDate date the user enrolled
 * @param status         current enrollment status
 * @param userId         id of the enrolled user
 * @param courseId       id of the course
 */
public record EnrollmentResponse(
        Long id,
        LocalDate enrollmentDate,
        EnrollmentStatus status,
        Long userId,
        Long courseId
) {

    /**
     * Converts an {@link Enrollment} entity into its API representation.
     *
     * @param enrollment the entity to convert (must not be {@code null})
     * @return a new immutable {@code EnrollmentResponse}
     */
    public static EnrollmentResponse from(Enrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getEnrollmentDate(),
                enrollment.getStatus(),
                enrollment.getUser().getId(),
                enrollment.getCourse().getId()
        );
    }
}
