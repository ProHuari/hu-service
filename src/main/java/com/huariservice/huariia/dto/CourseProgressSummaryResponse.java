package com.huariservice.huariia.dto;

import com.huariservice.huariia.entities.enums.EnrollmentStatus;

/**
 * Consolidated completion progress of a learner inside a specific course.
 */
public record CourseProgressSummaryResponse(
        Long courseId,
        long totalLessons,
        long completedLessons,
        double completionPercentage,
        EnrollmentStatus enrollmentStatus
) {
}
