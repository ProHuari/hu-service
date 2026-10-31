package com.huariservice.huariia.entities.enums;

/**
 * Lifecycle state of an {@link com.huariservice.huariia.entities.Enrollment}
 * (the link between a user and a course).
 *
 * <p>Persisted as a {@code STRING} in the {@code matriculas.status_matricula} column.</p>
 */
public enum EnrollmentStatus {

    /** The user is currently enrolled and may access the course content (default state). */
    ACTIVE,

    /** The user has finished every lesson of the course. */
    COMPLETED,

    /** The enrollment was cancelled by the user or by an administrator. */
    CANCELLED
}
