package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.EnrollmentRequest;
import com.huariservice.huariia.DTOs.EnrollmentResponse;
import com.huariservice.huariia.entities.Course;
import com.huariservice.huariia.entities.Enrollment;
import com.huariservice.huariia.entities.User;
import com.huariservice.huariia.entities.enums.EnrollmentStatus;
import com.huariservice.huariia.exceptions.ConflictException;
import com.huariservice.huariia.exceptions.ResourceNotFoundException;
import com.huariservice.huariia.repositories.CourseRepository;
import com.huariservice.huariia.repositories.EnrollmentRepository;
import com.huariservice.huariia.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business rules for course enrollments.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class EnrollmentService {

    private static final String ENROLLMENT_NOT_FOUND = "Essa matrícula não foi realizada";

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    /**
     * Enrolls a user in a course. The user id always comes from the token, never from the request body.
     *
     * @param userId  the authenticated user's id
     * @param request the course to enroll in
     * @return the created enrollment
     * @throws ResourceNotFoundException if the user or course does not exist
     * @throws ConflictException         if the user is already enrolled in the course
     */
    public EnrollmentResponse create(Long userId, EnrollmentRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Esse usuário não foi cadastrado"));
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("Esse curso não foi publicado"));

        if (enrollmentRepository.existsByUserIdAndCourseId(user.getId(), course.getId())) {
            throw new ConflictException("O usuário já está matriculado nesse curso");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setUser(user);
        enrollment.setCourse(course);

        return EnrollmentResponse.from(enrollmentRepository.save(enrollment));
    }

    /**
     * Lists all enrollments (admin view).
     *
     * @return every enrollment
     */
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> findAll() {
        return enrollmentRepository.findAll().stream().map(EnrollmentResponse::from).toList();
    }

    /**
     * Lists the enrollments of a user.
     *
     * @param userId the user id
     * @return the user's enrollments
     */
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> findByUser(Long userId) {
        return enrollmentRepository.findByUserId(userId).stream()
                .map(EnrollmentResponse::from).toList();
    }

    /**
     * Finds an enrollment by id. Students only see their own enrollments; for others a 404
     * (not 403) is returned so the existence of the id is not revealed.
     *
     * @param id      the enrollment id
     * @param userId  the authenticated user's id
     * @param isAdmin whether the authenticated user is an admin
     * @return the enrollment
     * @throws ResourceNotFoundException if the enrollment does not exist or belongs to another user
     */
    @Transactional(readOnly = true)
    public EnrollmentResponse findById(Long id, Long userId, boolean isAdmin) {
        Enrollment enrollment = getEnrollment(id);

        if (!isAdmin && !enrollment.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException(ENROLLMENT_NOT_FOUND);
        }
        return EnrollmentResponse.from(enrollment);
    }

    /**
     * Changes the status of an enrollment. User and course never change after enrolling.
     *
     * @param id        the enrollment id
     * @param newStatus the new status
     * @return the updated enrollment
     * @throws ResourceNotFoundException if the enrollment does not exist
     */
    public EnrollmentResponse updateStatus(Long id, EnrollmentStatus newStatus) {
        Enrollment enrollment = getEnrollment(id);
        enrollment.setStatus(newStatus);

        return EnrollmentResponse.from(enrollmentRepository.save(enrollment));
    }

    /**
     * Deletes an enrollment.
     *
     * @param id the enrollment id
     * @throws ResourceNotFoundException if the enrollment does not exist
     */
    public void delete(Long id) {
        enrollmentRepository.delete(getEnrollment(id));
    }

    private Enrollment getEnrollment(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ENROLLMENT_NOT_FOUND));
    }
}
