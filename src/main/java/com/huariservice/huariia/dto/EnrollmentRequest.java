package com.huariservice.huariia.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Incoming payload used by a student to enroll in a course.
 *
 * <p>The enrollment date and status are set by the server, and the user is taken from the
 * authenticated JWT — never from the request body — so a client cannot enroll someone else.</p>
 *
 * @param courseId id of the course to enroll in (required)
 */
public record EnrollmentRequest(
        @NotNull(message = "Course is required")
        Long courseId
) {}